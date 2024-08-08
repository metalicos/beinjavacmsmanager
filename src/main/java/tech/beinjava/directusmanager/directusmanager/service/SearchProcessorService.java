package tech.beinjava.directusmanager.directusmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tech.beinjava.directusmanager.directusmanager.config.DirectusAppProperties;
import tech.beinjava.directusmanager.directusmanager.model.*;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchProcessorService {
    private final DirectusAppProperties properties;
    private final RestClient restClient;

    public void updateAllArticles() {
        List<Integer> articlesToUpdate = getArticlesToUpdate();
        articlesToUpdate.stream().parallel().forEach(this::updateSearchMetadata);

    }

    public void updateSearchMetadata(Integer id) {
        PatchSearchMetadata patchSearchMetadata = populateSearchMetadata(id);
        updateSearchMetadata(patchSearchMetadata, id);
    }

    public List<Integer> getArticlesToUpdate() {
        Articles data = restClient.get().uri(URI.create("http://54.161.78.47/items/articles?fields=id"))
                .header(HttpHeaders.AUTHORIZATION, properties.getDirectusApiUserToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .retrieve()
                .body(Articles.class);

        return data.getData().stream().map(ArticleDataModel::getId).collect(Collectors.toList());
    }

    public PatchSearchMetadata populateSearchMetadata(Integer id) {
        ArticleDataModel data = restClient.get().uri(URI.create(String.format("http://54.161.78.47/items/articles/%s?fields=*,chapters.*,chapters.parts.*", id)))
                .header(HttpHeaders.AUTHORIZATION, properties.getDirectusApiUserToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .retrieve()
                .body(Article.class).getData();

        String title = data.getTitle();
        String description = data.getDescription();
        String tags = String.join(" ", data.getTags());
        String articleText = tags + title + " " + description + " " + Optional.ofNullable(data.getChapters()).orElse(List.of())
                .stream()
                .map(chapter -> chapter.getTitle() + Optional.ofNullable(chapter.getParts()).orElse(List.of()).stream()
                        .map(Part::getTitle).collect(Collectors.joining(" ")))
                .collect(Collectors.joining(" "));

        String transformedText = transformArticleText(articleText);
        return new PatchSearchMetadata(transformedText, tags.toUpperCase());
    }

    public void updateSearchMetadata(PatchSearchMetadata patchSearchMetadata, Integer id) {
        log.info("Updating article={}", id);
        Object obj = restClient.patch()
                .uri(URI.create("http://54.161.78.47/items/articles/" + id))
                .header(HttpHeaders.AUTHORIZATION, properties.getDirectusApiUserToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .body(patchSearchMetadata)
                .retrieve()
                .body(Object.class);

        if (obj == null) throw new RuntimeException("Empty Update Article Data");
    }

    private static String transformArticleText(String articleText) {
        // Step 1: Remove all special characters, commas, etc. (keep only alphabet, numbers, and whitespaces)
        String cleanedText = articleText.replaceAll("[^\\p{L}\\p{N}\\s]", "");

        // Step 1: Remove all null
        cleanedText = cleanedText.replace("null", "");

        // Step 2: Trim all sequential spaces to only one
        cleanedText = cleanedText.replaceAll("\\s+", " ");

        // Step 3: Remove spaces at the beginning and end
        cleanedText = cleanedText.trim();

        // Step 4: Remove words less than 3 characters
        String[] words = cleanedText.split(" ");
        StringBuilder filteredText = new StringBuilder();
        for (String word : words) {
            if (word.length() >= 3) {
                filteredText.append(word).append(" ");
            }
        }

        // Step 5: Deduplicate words while preserving order
        Set<String> uniqueWords = new LinkedHashSet<>();
        for (String word : filteredText.toString().trim().split(" ")) {
            uniqueWords.add(word);
        }

        // Join unique words back into a single string
        String uniqueWordsStr = String.join(" ", uniqueWords);

        return uniqueWordsStr.toUpperCase();
    }
}

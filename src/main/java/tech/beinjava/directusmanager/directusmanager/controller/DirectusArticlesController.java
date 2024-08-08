package tech.beinjava.directusmanager.directusmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.beinjava.directusmanager.directusmanager.model.BatchSearchUpdate;
import tech.beinjava.directusmanager.directusmanager.service.SearchProcessorService;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class DirectusArticlesController {
    private final SearchProcessorService searchProcessorService;

    @GetMapping("/search-metadata")
    public String updateSearchMetadata() {
        searchProcessorService.updateAllArticles();
        return "OK";
    }

    @GetMapping("/{id}/search-metadata/refresh")
    public String updateSearchMetadata(@PathVariable Integer id) {
        searchProcessorService.updateSearchMetadata(id);
        return "OK";
    }

    @PostMapping("/batch/search-metadata/refresh")
    public String updateSearchMetadata(@RequestBody BatchSearchUpdate batchSearchUpdate) {
        batchSearchUpdate.getIds().stream().parallel().forEach(searchProcessorService::updateSearchMetadata);
        return "OK";
    }
}

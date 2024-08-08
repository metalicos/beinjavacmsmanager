package tech.beinjava.directusmanager.directusmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ArticleDataModel {
    private Integer id;
    private String title;
    private String description;
    private List<String> tags;
    private List<Chapter> chapters;
}

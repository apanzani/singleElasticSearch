package com.elastic.demo.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "#{@elasticConfigData.indexName}")
@Builder
@Data
public class Article {

    @Id
    private String id;

    private String title;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Author> authors;
}

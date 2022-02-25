package com.elastic.demo.repository;

import com.elastic.demo.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    Page<Article> findByAuthorsName(String name, Pageable pageable);
    List<Article> findByAuthorsName(String name);
    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}

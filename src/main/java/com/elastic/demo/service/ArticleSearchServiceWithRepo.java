package com.elastic.demo.service;

import com.elastic.demo.model.Article;
import com.elastic.demo.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleSearchServiceWithRepo {

    private final ArticleRepository articleRepository;

    public ArticleSearchServiceWithRepo(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void createArticlesIndexBulk(final List<Article> products) {
        articleRepository.saveAll(products);
    }

    public void createArticleIndex(final Article product) {
        articleRepository.save(product);
    }

    public List<Article> findByAuthorName(final String articleName) {
        return articleRepository.findByAuthorsName(articleName);
    }
}

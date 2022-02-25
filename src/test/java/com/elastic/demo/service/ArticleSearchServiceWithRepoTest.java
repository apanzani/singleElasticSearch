package com.elastic.demo.service;

import com.elastic.demo.model.Article;
import com.elastic.demo.model.Author;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class ArticleSearchServiceWithRepoTest {

    private final Logger LOG = LoggerFactory.getLogger(ArticleSearchServiceWithRepoTest.class);
    @Autowired
    private ArticleSearchServiceWithRepo articleSearchServiceWithRepo;

    @Test
    void testCreateArticlesIndexBulk() {
        Author author = Author.builder().name("Andrea").surName("Panzani").build();
        Author author1 = Author.builder().name("Gildo").surName("Ugolotto").build();

        Article article = Article.builder().authors(List.of(author1, author)).id("123455").title("Primo articolo").build();
        Article article1 = Article.builder().authors(List.of(author1)).id("123435").title("Secondo articolo").build();
        Article article2 = Article.builder().authors(List.of(author)).id("145435").title("Terzo articolo").build();

        List<Article> articles = List.of(article, article1, article2);
        articleSearchServiceWithRepo.createArticlesIndexBulk(articles);
    }

    @Test
    void testFindArticleName() {
        Author author = Author.builder().name("Andrea").surName("Panzani").build();
        Author author1 = Author.builder().name("Gildo").surName("Ugolotto").build();

        Article article = Article.builder().authors(List.of(author1, author)).id("123455").title("Primo articolo").build();
        Article article1 = Article.builder().authors(List.of(author1)).id("123435").title("Secondo articolo").build();
        Article article2 = Article.builder().authors(List.of(author)).id("145435").title("Terzo articolo").build();

        List<Article> articles = List.of(article, article1, article2);
        articleSearchServiceWithRepo.createArticlesIndexBulk(articles);
        List<Article> articlesReturned = articleSearchServiceWithRepo.findByAuthorName("Andrea");
        if(!CollectionUtils.isEmpty(articlesReturned)){
            List<String> articlesPerAuthor = articlesReturned.stream().map(Article::getTitle).collect(Collectors.toList());
            LOG.info("L'autore {} ha scritto i seguenti articoli {} ","Andrea", articlesPerAuthor.toString() );
        }
    }


}

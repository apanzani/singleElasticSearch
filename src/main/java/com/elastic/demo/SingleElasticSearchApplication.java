package com.elastic.demo;

import com.elastic.demo.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@SpringBootApplication
@Slf4j
public class SingleElasticSearchApplication {

/*    @Autowired
    private ElasticsearchOperations esOps;

    @Autowired
    private ArticleRepository articleRepository;*/


    public static void main(String[] args) {
        SpringApplication.run(SingleElasticSearchApplication.class, args);
    }
}

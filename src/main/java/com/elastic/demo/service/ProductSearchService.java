package com.elastic.demo.service;

import com.elastic.demo.config.ElasticConfigData;
import com.elastic.demo.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSearchService {

    private final Logger LOG = LoggerFactory.getLogger(ProductSearchService.class);

    private final ElasticConfigData elasticConfigData;
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductSearchService(ElasticConfigData elasticConfigData, ElasticsearchOperations elasticsearchOperations) {
        this.elasticConfigData = elasticConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<String> createProductIndexBulk(final List<Product> products){
        List<IndexQuery> queries = products.stream().map(product -> new IndexQueryBuilder()
                .withId(product.getId())
                .withObject(product).build()).collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()))
        .stream().map(IndexedObjectInformation::getId).collect(Collectors.toList());
    }

    public String createProductIndex(Product product){
        IndexQuery indexQuery = new IndexQueryBuilder().withId(product.getId().toString())
                .withObject(product).build();

        return elasticsearchOperations.index(indexQuery, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));
    }
}

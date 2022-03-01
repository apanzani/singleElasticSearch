package com.elastic.demo.service;

import com.elastic.demo.config.ElasticConfigData;
import com.elastic.demo.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    public List<String> createProductIndexBulk(final List<Product> products) {
        List<IndexQuery> queries = products.stream().map(product -> new IndexQueryBuilder()
                .withId(product.getId())
                .withObject(product).build()).collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()))
                .stream().map(IndexedObjectInformation::getId).collect(Collectors.toList());
    }

    public String createProductIndex(Product product) {
        IndexQuery indexQuery = new IndexQueryBuilder().withId(product.getId().toString())
                .withObject(product).build();

        return elasticsearchOperations.index(indexQuery, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));
    }

    /*NativeQuery provides the maximum flexibility for building a query using objects representing
    Elasticsearch constructs like aggregation, filter, and sort.
    Here is a NativeQuery for searching products matching a particular manufacturer:*/
    public List<SearchHit<Product>> findProductsByBrand(final String brandName) {
        MatchQueryBuilder manufacturer = QueryBuilders.matchQuery("manufacturer", brandName);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(manufacturer).build();

        SearchHits<Product> productHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));

        LOG.info("Product hits size {}, {}", productHits.getSearchHits().size(), productHits.getSearchHits());

        List<SearchHit<Product>> searchHits = productHits.getSearchHits();

        if (!CollectionUtils.isEmpty(searchHits)) {
            searchHits.forEach(search -> LOG.info("{}", search));
        }
        return searchHits;
    }

    /*A StringQuery gives full control by allowing the use of the native Elasticsearch query
    as a JSON string as shown here:*/
    public SearchHits<Product> findByProductName(final String productName) {

        StringQuery stringQuery = new StringQuery("{\"match\":{\"name\":{\"query\":\"" + productName + "\"}}}\"");

        SearchHits<Product> search = elasticsearchOperations.search(stringQuery, Product.class, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));
        LOG.info("Producut with StringQuery found {}", search);

        return search;
    }

    /*With CriteriaQuery we can build queries without knowing any terminology of Elasticsearch.
    The queries are built using method chaining with Criteria objects.
    Each object specifies some criteria used for searching documents:*/
    public SearchHits<Product> findByProductPrice() {
        Criteria price = new Criteria("price").greaterThan(1.0).lessThan(4.0);
        CriteriaQuery criteriaQuery = new CriteriaQuery(price);
        SearchHits<Product> search = elasticsearchOperations.search(criteriaQuery, Product.class,
                IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));
        ;
        LOG.info("Number of Products with CriteriaQuery found {} element {}", search.getTotalHits(), search);
        return search;
    }

    //Multi-Field and Fuzzy Search
    //We also attach the fuzziness() to search for closely matching text
    // to account (per tenere conto) for spelling errors.
    public List<Product> processSearch(final String query) {
        log.info("Search with query {}", query);

        // 1. Create query on multiple fields enabling fuzzy search
        MultiMatchQueryBuilder fuzziness = QueryBuilders.multiMatchQuery(query, "name", "description").fuzziness(Fuzziness.AUTO);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(fuzziness).build();

        // 2. Execute search
        SearchHits<Product> productHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));

        // 3. Map searchHits to product list
        List<Product> productMatches = new ArrayList<>();
        productHits.forEach(searchHit -> productMatches.add(searchHit.getContent()));

        return productMatches;
    }

    //Fetching Suggestions with Wildcard Search
    //When we type into the search text field, we will fetch suggestions by performing a wild card search
    // with the characters entered in the search box.
    public List<String> fetchSuggestions(String query) {
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name", query + "*");
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withFilter(queryBuilder)
                .withPageable(PageRequest.of(0, 5)).build();

        SearchHits<Product> suggestionsHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(elasticConfigData.getIndexNameProduct()));

        List<String> suggestions = new ArrayList<>();

        suggestionsHits.getSearchHits().forEach(suggestionHit -> suggestions.add(suggestionHit.getContent().getName()));
        return suggestions;
    }
}

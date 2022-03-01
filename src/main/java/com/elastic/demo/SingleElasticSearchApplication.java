package com.elastic.demo;

import com.elastic.demo.config.ElasticConfigData;
import com.elastic.demo.model.Product;
import com.elastic.demo.repository.ProductRepository;
import com.elastic.demo.service.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import javax.annotation.PostConstruct;
import java.util.*;

@SpringBootApplication
@Slf4j
public class SingleElasticSearchApplication {

    private final Logger LOG = LoggerFactory.getLogger(SingleElasticSearchApplication.class);

    @Autowired
    private ElasticsearchOperations esOps;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticConfigData elasticConfigData;
/*
    @Autowired
    private ArticleRepository articleRepository;*/

    @PostConstruct
    public void buildIndex() {
        esOps.indexOps(Product.class).refresh();
        productRepository.saveAll(prepareDataset());

    }

    private Collection<Product> prepareDataset() {
        ClassPathResource classPathResource = new ClassPathResource("fashion-products.csv");
        List<Product> productList = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(classPathResource.getInputStream());
            int lineNo = 0;
            while (scanner.hasNextLine()) {
                ++lineNo;
                String line = scanner.nextLine();
                if (lineNo == 1) continue;
                Optional<Product> product = csvRowToProductMapper(line);
                if(product.isPresent()){
                    productList.add(product.get());
                }
            }
        } catch (Exception e) {
            log.error("File read error {}",e);
        }
        return productList;
    }

    private Optional<Product> csvRowToProductMapper(final String line) {
        try (
                Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(elasticConfigData.getCommaDelimiter());
            while (rowScanner.hasNext()) {
                String name = rowScanner.next();
                String description = rowScanner.next();
                String manufacturer = rowScanner.next();
                return Optional.of(
                        Product.builder()
                                .name(name)
                                .description(description)
                                .manufacturer(manufacturer)
                                .build());

            }
        }
        return Optional.of(null);
    }

    public static void main(String[] args) {
        SpringApplication.run(SingleElasticSearchApplication.class, args);
    }
}

package com.elastic.demo.service;

import com.elastic.demo.model.Article;
import com.elastic.demo.model.Author;
import com.elastic.demo.model.Product;
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

import static org.springframework.util.Assert.notNull;

@SpringBootTest
@Slf4j
public class ProductSearchServiceIndexTest {

    private final Logger LOG = LoggerFactory.getLogger(ProductSearchServiceIndexTest.class);
    @Autowired
    private ProductSearchService productSearchService;

    @Test
    void testCreateProduct() {
        Product javaProduct = Product.builder().id("1").category("Informatica").price(2.2)
                .manufacturer("Apogeo").quantity(23).name("Java").description("Learn Java").build();

        Product storiaProduct = Product.builder().id("2").category("Storia").price(3.2)
                .manufacturer("Rizzoli").quantity(2).name("La prima guerra mondiale")
                .description("Storia della prima guerra mondiale").build();

        String productIndex = productSearchService.createProductIndex(javaProduct);
        notNull(productIndex, "Product index null");
    }

    @Test
    void testCreateProductBulk() {
        Product javaProduct = Product.builder().id("1").category("Informatica").price(2.2)
                .manufacturer("Apogeo").quantity(23).name("Java").description("Learn Java").build();

        Product storiaProduct = Product.builder().id("2").category("Storia").price(3.2)
                .manufacturer("Rizzoli").quantity(2).name("La prima guerra mondiale")
                .description("Storia della prima guerra mondiale").build();

        List<String> productIndexBulk = productSearchService.createProductIndexBulk(List.of(javaProduct, storiaProduct));

        notNull(productIndexBulk, "Lista di prodotti null");
    }


}

package com.elastic.demo.controller;


import com.elastic.demo.model.Product;
import com.elastic.demo.service.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
public class SearchController {

    private ProductSearchService productSearchService;

    public SearchController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @GetMapping("/products")
    @ResponseBody
    public List<Product> fetchByNameOrDesc(@RequestParam(value = "q", required = false) String query){
        log.info("searching by name {}", query);
        List<Product> productList = productSearchService.processSearch(query);
        log.info("products found {}", productList );

        return productList;
    }

    @GetMapping("/suggestions")
    @ResponseBody
    public List<String> fetchSuggestions(@RequestParam(value = "q", required = false) String query){
        log.info(" fetcg suggests {}", query);
        List<String> suggests = productSearchService.fetchSuggestions(query);
        log.info("suggests found {}", suggests);
        return suggests;
    }
}

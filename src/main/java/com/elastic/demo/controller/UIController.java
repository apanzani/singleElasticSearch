package com.elastic.demo.controller;

import com.elastic.demo.model.Product;
import com.elastic.demo.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
public class UIController {

    private SearchService searchService;

    public UIController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public String home(Model model){
        List<Product> productList = searchService.fetchProductNamesContaining("Hornby");
        List<String> names = productList.stream().flatMap(
                prod -> {
                    return Stream.of(prod.getName());
                }).collect(Collectors.toList());
        log.info("product names {}", names);
        model.addAttribute("names",names);
        return "search";
    }
}

package com.springboot.app.item.comspringbootappitem.model.service;

import com.springboot.app.item.comspringbootappitem.model.Item;
import com.commons.springbootcommons.models.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service("serviceRestTemplate")
public class ItemServiceImp implements ItemService {

    private final RestTemplate clientRest;

    @Autowired
    public ItemServiceImp(RestTemplate clientRest) {
        this.clientRest = clientRest;
    }

    @Override
    public List<Item> findAll() {
        List<Product> products = Arrays.asList(Objects.requireNonNull(clientRest.getForObject("http://service-product/list", Product[].class)));

        return products.stream().map(p -> new Item(p,1)).collect(Collectors.toList());
    }

    @Override
    public Item findById(Long id, Integer amount) {
        Map<String,String> pathVariables = new HashMap<>();
        pathVariables.put("id",id.toString());
        Product product = clientRest.getForObject("http://service-product/show/{id}",Product.class,pathVariables);
        return new Item(product,amount);
    }

    @Override
    public Product save(Product product) {
        HttpEntity<Product> body= new HttpEntity<>(product);
        ResponseEntity<Product> response = clientRest.exchange("http://service-product/create", HttpMethod.POST, body, Product.class);
        return response.getBody();
    }

    @Override
    public Product update(Product product, Long id) {
        HttpEntity<Product> body= new HttpEntity<>(product);
        Map<String,String> pathVariables = new HashMap<>();
        pathVariables.put("id",id.toString());
        ResponseEntity<Product> response = clientRest.exchange("http://service-product/edit/{id}",
                HttpMethod.PUT, body, Product.class,pathVariables);
        return response.getBody();
    }

    @Override
    public Product delete(Long id) {
        Map<String,String> pathVariables = new HashMap<>();
        pathVariables.put("id",id.toString());
        clientRest.delete("http://service-products/delete/{id}",pathVariables);
        return null;
    }
}

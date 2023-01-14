package com.springboot.app.item.comspringbootappitem.clients;

import com.commons.springbootcommons.models.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "service-product")
public interface ProductClientRest {

    @GetMapping("/list")
    List<Product> list();

    @GetMapping("/show/{id}")
    Product detail(@PathVariable Long id);

    @PostMapping("/create")
    Product create(@RequestBody Product product);

    @PutMapping("/edit/{id}")
    Product update(@RequestBody Product product,@PathVariable Long id);

    @DeleteMapping("/delete/{id}")
    Product delete (@PathVariable Long id);
}

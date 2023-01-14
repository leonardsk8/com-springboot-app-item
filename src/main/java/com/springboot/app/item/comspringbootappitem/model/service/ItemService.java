package com.springboot.app.item.comspringbootappitem.model.service;

import com.springboot.app.item.comspringbootappitem.model.Item;
import com.commons.springbootcommons.models.entity.Product;

import java.util.List;

public interface ItemService {

    List<Item> findAll();

    Item findById(Long id, Integer amount);

    Product save(Product product);

    Product update(Product product, Long id);

    Product delete(Long id);
}

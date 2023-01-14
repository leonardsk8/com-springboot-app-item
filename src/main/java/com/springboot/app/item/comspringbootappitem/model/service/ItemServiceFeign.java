package com.springboot.app.item.comspringbootappitem.model.service;

import com.springboot.app.item.comspringbootappitem.clients.ProductClientRest;
import com.springboot.app.item.comspringbootappitem.model.Item;
import com.commons.springbootcommons.models.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("serviceFeign")
public class ItemServiceFeign  implements ItemService{

    @Autowired
    private ProductClientRest clientFeign;

    @Override
    public List<Item> findAll() {
        return clientFeign.list().stream().map(p -> new Item(p,1)).collect(Collectors.toList());
    }

    @Override
    public Item findById(Long id, Integer amount) {
        return new Item(clientFeign.detail(id),amount);
    }

    @Override
    public Product save(Product product) {
        return clientFeign.create(product);
    }

    @Override
    public Product update(Product product, Long id) {
        return clientFeign.update(product,id);
    }

    @Override
    public Product delete(Long id) {
        return clientFeign.delete(id);
    }
}

package com.springboot.app.item.comspringbootappitem.model;
import com.commons.springbootcommons.models.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {


    private Product product;
    private Integer quantity;

    public Item() {
    }

    public Item(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

}

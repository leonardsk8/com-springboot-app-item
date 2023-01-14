package com.springboot.app.item.comspringbootappitem.controllers;

import com.springboot.app.item.comspringbootappitem.model.Item;
import com.commons.springbootcommons.models.entity.Product;
import com.springboot.app.item.comspringbootappitem.model.service.ItemService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@RefreshScope
@RestController
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private Environment environment;

    @Value("${configuration.text}")
    private String text;

    @Autowired
    private CircuitBreakerFactory cbFactory;
    
    @Autowired
    @Qualifier("serviceFeign")
    private ItemService itemService;
    
    @GetMapping("/list")
    public List<Item> list(@RequestParam(name="name",required = false) String name,@RequestHeader(name="token-request",required = false) String token){
        System.out.println(name);
        System.out.println(token);
        return itemService.findAll();
    }



    @GetMapping("/show/{id}/amount/{amount}")
    public Item detail(@PathVariable Long id,@PathVariable Integer amount){
        return cbFactory.create("items").run(()-> itemService.findById(id,amount),e-> alternativeMethod(id, amount,e));
    }


    @CircuitBreaker(name = "items" ,fallbackMethod = "alternativeMethod")
    @GetMapping("/show2/{id}/amount/{amount}")
    public Item detail2(@PathVariable Long id,@PathVariable Integer amount){
        return itemService.findById(id,amount);
    }

    @CircuitBreaker(name = "items" ,fallbackMethod = "alternativeMethod2")
    @TimeLimiter(name = "items")
    @GetMapping("/show3/{id}/amount/{amount}")
    public CompletableFuture<Item> detail3(@PathVariable Long id, @PathVariable Integer amount){
        return CompletableFuture.supplyAsync(() -> itemService.findById(id,amount));
    }

    @GetMapping("/get-config")
    public ResponseEntity<?> getConfig(@Value("${server.port}") String port){
        System.out.println(text);
        Map<String,String> json = new HashMap<>();
        json.put("text",text);
        json.put("json",port);
        if(environment.getActiveProfiles().length >0 && environment.getActiveProfiles()[0].equalsIgnoreCase("dev")){
              json.put("autor.name",environment.getProperty("configuration.autor.name"));
              json.put("autor.email",environment.getProperty("configuration.autor.email"));
        }
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product){
        return itemService.save(product);
    }

    @PutMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product edit(@RequestBody Product product, @PathVariable Long id){
        return itemService.update(product,id);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        itemService.delete(id);
    }

    //Do not handler circuit breaker
//    @TimeLimiter(name = "items",fallbackMethod = "alternativeMethod2")
//    @GetMapping("/show3/{id}/amount/{amount}")
//    public CompletableFuture<Item> detail3(@PathVariable Long id, @PathVariable Integer amount){
//        return CompletableFuture.supplyAsync(() -> itemService.findById(id,amount));
//    }

    public Item alternativeMethod(Long id, Integer amount, Throwable e){
        logger.info(e.getMessage());
        var item = new Item();
        var product = new Product();
        item.setQuantity(amount);
        product.setId(id);
        product.setNameProduct("Camera");
        product.setPrice(500.00);
        item.setProduct(product);
        return item;
    }

    public CompletableFuture<Item> alternativeMethod2(Long id, Integer amount, Throwable e){
        logger.info(e.getMessage());
        var item = new Item();
        var product = new Product();
        item.setQuantity(amount);
        product.setId(id);
        product.setNameProduct("Camera");
        product.setPrice(500.00);
        item.setProduct(product);
        return CompletableFuture.supplyAsync(() -> item);
    }

}

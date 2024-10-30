package com.example.demo.controllers;

import com.example.demo.dtos.ResponseDTOs;
import com.example.demo.models.Product;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final String GET_PRODUCT_BY_ID = "/{id}";

    @Autowired
    private ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('VIEW', 'EDIT')")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping(GET_PRODUCT_BY_ID)
    @PreAuthorize("hasAnyAuthority('VIEW', 'EDIT')")
    public ResponseEntity<ResponseDTOs.ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if(product == null)
            return ResponseEntity.ok(ResponseDTOs.ProductResponse.builder().message("Product not found.").build());
        return ResponseEntity.ok(ResponseDTOs.ProductResponse.builder().product(product).build());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('EDIT')")
    public ResponseEntity<ResponseDTOs.AddProductResponse> addProduct(@RequestBody Product product) {
        try {
            Product newProduct = productService.addProduct(product);
            return ResponseEntity.ok(ResponseDTOs.AddProductResponse.builder().status(ResponseDTOs.Status.SUCCESS).product(product).build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseDTOs.AddProductResponse.builder().status(ResponseDTOs.Status.FAILURE).message(e.getMessage()).build());
        }
    }
}

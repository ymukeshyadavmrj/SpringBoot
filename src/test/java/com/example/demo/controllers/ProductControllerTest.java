package com.example.demo.controllers;

import com.example.demo.dtos.ResponseDTOs;
import com.example.demo.models.Product;
import com.example.demo.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Setter
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllProducts_IfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "harry")
    public void getAllProducts_WhenProductsPresent_AndAuthenticated() throws Exception {
        Product product1 = new Product(1L, "egg", 2.0, 3, null, null);
        Product product2 = new Product(1L, "book", 2.0, 3, null, null);
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    @WithMockUser(username = "harry")
    public void getAllProducts_WhenProductsNotPresent_AndAuthenticated() throws Exception {
        List<Product> products = new ArrayList<>();

        when(productService.getAllProducts()).thenReturn(products);
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    public void getProductsById_IfNotAuthenticated() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "harry")
    public void getProductsById_WhenProductFound_AndAuthenticated() throws Exception {
        Product product = new Product(1L, "book", 2.0, 3, null, null);

        when(productService.getProductById(product.getId())).thenReturn(product);
        MockHttpServletResponse response = mockMvc.perform(get("/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        ResponseDTOs.ProductResponse productResponse = objectMapper.readValue(response.getContentAsString(), ResponseDTOs.ProductResponse.class);

        assertNull(productResponse.getMessage());
        assertEquals(product.getId(), productResponse.getProduct().getId());
        assertEquals(product.getName(), productResponse.getProduct().getName());
        assertEquals(product.getPrice(), productResponse.getProduct().getPrice());
        assertEquals(product.getQuantity(), productResponse.getProduct().getQuantity());
        assertEquals(product.getImageUrl(), productResponse.getProduct().getImageUrl());
        assertEquals(product.getDescription(), productResponse.getProduct().getDescription());

    }

    @Test
    @WithMockUser(username = "harry")
    public void getProductsById_WhenProductNotFound_AndAuthenticated() throws Exception {
        Product product = new Product(1L, "book", 2.0, 3, null, null);

        when(productService.getProductById(product.getId())).thenReturn(product);
        MockHttpServletResponse response = mockMvc.perform(get("/products/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        ResponseDTOs.ProductResponse productResponse = objectMapper.readValue(response.getContentAsString(), ResponseDTOs.ProductResponse.class);

        assertEquals("Product not found.", productResponse.getMessage());
        assertNull(productResponse.getProduct());
    }

    @Test
    public void addProduct_IfNotAuthenticated() throws Exception {
        mockMvc.perform(put("/products"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "harry")
    public void addProductSuccess_forAuthenticated() throws Exception {
        Product product = new Product(1L, "book", 2.0, 3, null, null);
        String requestBody = objectMapper.writeValueAsString(product);
        when(productService.addProduct(product)).thenReturn(product);
        MockHttpServletResponse response = mockMvc.perform(put("/products").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ResponseDTOs.AddProductResponse productResponse = objectMapper.readValue(response.getContentAsString(), ResponseDTOs.AddProductResponse.class);

        assertNull(productResponse.getMessage());
        assertEquals(product.getId(), productResponse.getProduct().getId());
        assertEquals(product.getName(), productResponse.getProduct().getName());
        assertEquals(product.getPrice(), productResponse.getProduct().getPrice());
        assertEquals(product.getQuantity(), productResponse.getProduct().getQuantity());
        assertEquals(product.getImageUrl(), productResponse.getProduct().getImageUrl());
        assertEquals(product.getDescription(), productResponse.getProduct().getDescription());
    }

}

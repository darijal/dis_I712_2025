package com.ecommerce.product_service;

import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Zlatni prsten", "18K", new BigDecimal("899.99"), 5, "Prstenje");
    }

    @Test
    void getAll_shouldReturnAllProducts() {
        Product another = new Product(2L, "Srebrni prsten", "925", new BigDecimal("149.50"), 10, "Prstenje");
        when(productRepository.findAll()).thenReturn(Arrays.asList(product, another));

        List<Product> result = productService.getAll();

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getById(1L);

        assertEquals("Zlatni prsten", result.getName());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getById(99L));
    }

    @Test
    void create_shouldSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product created = productService.create(product);

        assertNotNull(created);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void create_shouldThrowException_whenPriceIsNegative() {
        Product invalid = new Product(null, "Prsten", "Opis", new BigDecimal("-10"), 3, "Prstenje");

        assertThrows(RuntimeException.class, () -> productService.create(invalid));
        verify(productRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenNameIsBlank() {
        Product invalid = new Product(null, "", "Opis", new BigDecimal("100"), 3, "Prstenje");

        assertThrows(RuntimeException.class, () -> productService.create(invalid));
    }

    @Test
    void reduceStock_shouldDecreaseStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.reduceStock(1L, 2);

        assertEquals(3, result.getStock()); // bilo 5, smanjeno za 2
    }

    @Test
    void reduceStock_shouldThrowException_whenInsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.reduceStock(1L, 100));
    }

    @Test
    void delete_shouldRemoveProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository, times(1)).delete(product);
    }
}

package com.ecommerce.product_service.service;

import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proizvod sa ID " + id + " ne postoji."));
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product create(Product product) {
        validate(product);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        validate(updated);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setCategory(updated.getCategory());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        Product existing = getById(id);
        productRepository.delete(existing);
    }

    public Product reduceStock(Long productId, Integer quantity) {
        Product product = getById(productId);
        if (product.getStock() < quantity) {
            throw new RuntimeException(
                    "Nema dovoljno na stanju. Dostupno: " + product.getStock() + ", trazeno: " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        return productRepository.save(product);
    }

    private void validate(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new RuntimeException("Ime proizvoda je obavezno.");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Cena mora biti veca od nule.");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new RuntimeException("Stanje ne moze biti negativno.");
        }
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            throw new RuntimeException("Kategorija je obavezna.");
        }
    }
}

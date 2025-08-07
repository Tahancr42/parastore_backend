package com.example.parastoreb.controller;

import com.example.parastoreb.dto.product.ProductRequest;
import com.example.parastoreb.dto.product.ProductResponse;
import com.example.parastoreb.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173") // autorise les appels depuis ton frontend
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 🔹 Créer un produit
    @PostMapping
    public ProductResponse createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    // 🔹 Récupérer tous les produits
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // 🔹 Récupérer un produit par son ID
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 🔹 Modifier un produit
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    // 🔹 Supprimer un produit
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}

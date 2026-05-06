package com.saas.inventory.service;

import com.saas.inventory.dto.PageResponse;
import com.saas.inventory.dto.ProductRequest;
import com.saas.inventory.dto.ProductResponse;
import com.saas.inventory.exception.NotFoundException;
import com.saas.inventory.model.Category;
import com.saas.inventory.model.Product;
import com.saas.inventory.model.StockMovement;
import com.saas.inventory.repository.CategoryRepository;
import com.saas.inventory.repository.ProductRepository;
import com.saas.inventory.repository.StockMovementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, StockMovementRepository stockMovementRepository, CategoryRepository categoryRepository) {
        this.productRepository = repository;
        this.stockMovementRepository = stockMovementRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(Product product) {

        if (product.getStock() < 0) {
            throw new RuntimeException("Stock no puede ser negativo");
        }

        if (product.getPrice() <= 0) {
            throw new RuntimeException("Precio inválido");
        }

        return productRepository.save(product);
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        p.getCategory().getName()
                ))
                .toList();
    }

    public Product updateStock(Long id, int quantity) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int newStock = product.getStock() + quantity;

        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }

        product.setStock(newStock);

        return productRepository.save(product);
    }

    public List<ProductResponse> lowStock(int threshold) {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getStock() <= threshold)
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        p.getCategory().getName()
                ))
                .toList();
    }

    public void adjustStock(Long productId, int quantity, String username) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int previousStock = product.getStock();
        int newStock = previousStock + quantity;

        if (newStock < 0) {
            throw new RuntimeException("Stock no puede ser negativo");
        }

        product.setStock(newStock);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setType(quantity > 0 ? "ENTRY" : "EXIT");
        movement.setQuantity(Math.abs(quantity));
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setDate(LocalDateTime.now());
        movement.setUsername(username);
        movement.setProduct(product);

        stockMovementRepository.save(movement);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        productRepository.delete(product);
    }

    public ProductResponse create(ProductRequest request){
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        Product saved = productRepository.save(product);

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getStock(),
                saved.getCategory().getName()
        );
    }

    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        p.getCategory().getName()
                ))
                .toList();
    }

    public List<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        p.getCategory().getName()
                ))
                .toList();
    }

    public Map<String, Object> getStats() {

        List<Product> products = productRepository.findAll();

        int totalProducts = products.size();
        int lowStock = (int) products.stream().filter(p -> p.getStock() <= 5).count();
        int totalStock = products.stream().mapToInt(Product::getStock).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", totalProducts);
        stats.put("lowStock", lowStock);
        stats.put("totalStock", totalStock);

        return stats;
    }

    public PageResponse<ProductResponse> findAllPaginated(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productRepository.findAll(pageable);

        List<ProductResponse> content = result.getContent()
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        p.getCategory().getName()
                ))
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        Product saved = productRepository.save(product);

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getStock(),
                saved.getCategory().getName()
        );
    }


}
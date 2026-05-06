package com.saas.inventory.controller;

import com.saas.inventory.dto.PageResponse;
import com.saas.inventory.dto.ProductRequest;
import com.saas.inventory.dto.ProductResponse;
import com.saas.inventory.dto.StockMovementDTO;
import com.saas.inventory.model.Product;
import com.saas.inventory.model.StockMovement;
import com.saas.inventory.repository.StockMovementRepository;
import com.saas.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final StockMovementRepository stockMovementRepository;

    public ProductController(ProductService productService, StockMovementRepository stockMovementRepository) {
        this.productService = productService;
        this.stockMovementRepository = stockMovementRepository;
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return productService.findAll();
    }

    @PutMapping("/{id}/stock")
    public Product updateStock(@PathVariable Long id, @RequestParam int quantity) {
        return productService.updateStock(id, quantity);
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock(@RequestParam(defaultValue = "5") int threshold) {
        return productService.lowStock(threshold);
    }

    @PutMapping("/{id}/adjust-stock")
    public ResponseEntity<?> adjustStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            Authentication auth
    ) {
        productService.adjustStock(id, quantity, auth.getName());
        return ResponseEntity.ok("Stock actualizado");
    }

    @GetMapping("/{id}/movements")
    public List<StockMovementDTO> getMovements(@PathVariable Long id) {
        return stockMovementRepository.findByProductId(id)
                .stream()
                .map(m -> {
                    StockMovementDTO dto = new StockMovementDTO();
                    dto.setType(m.getType());
                    dto.setQuantity(m.getQuantity());
                    dto.setPreviousStock(m.getPreviousStock());
                    dto.setNewStock(m.getNewStock());
                    dto.setUsername(m.getUsername());
                    dto.setDate(m.getDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok("Producto eliminado");
    }

    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam String name) {
        return productService.searchByName(name);
    }

    @GetMapping("/category/{id}")
    public List<ProductResponse> getByCategory(@PathVariable Long id) {
        return productService.findByCategory(id);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return productService.getStats();
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<ProductResponse>> getAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(productService.findAllPaginated(page, size));
    }
}
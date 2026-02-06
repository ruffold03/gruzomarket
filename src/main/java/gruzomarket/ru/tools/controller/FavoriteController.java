package gruzomarket.ru.tools.controller;

import gruzomarket.ru.tools.dto.FavoriteResponse;
import gruzomarket.ru.tools.entity.Product;
import gruzomarket.ru.tools.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Получить текущего пользователя из контекста безопасности
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // email пользователя
    }

    /**
     * Получить все избранные товары пользователя
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getUserFavorites() {
        String userEmail = getCurrentUserEmail();
        List<Product> favorites = favoriteService.getFavoriteProducts(userEmail);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Добавить товар в избранное
     */
    @PostMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addToFavorites(@PathVariable Long productId) {
        String userEmail = getCurrentUserEmail();
        favoriteService.addToFavorites(productId, userEmail);

        FavoriteResponse response = new FavoriteResponse();
        response.setProductId(productId);
        response.setFavorite(true);
        response.setFavoriteCount(favoriteService.getFavoriteCount(userEmail));

        return ResponseEntity.ok(response);
    }

    /**
     * Удалить товар из избранного
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long productId) {
        String userEmail = getCurrentUserEmail();
        favoriteService.removeFromFavorites(productId, userEmail);

        FavoriteResponse response = new FavoriteResponse();
        response.setProductId(productId);
        response.setFavorite(false);
        response.setFavoriteCount(favoriteService.getFavoriteCount(userEmail));

        return ResponseEntity.ok(response);
    }

    /**
     * Переключить избранное (добавить/удалить)
     */
    @PostMapping("/toggle/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long productId) {
        String userEmail = getCurrentUserEmail();
        boolean isNowFavorite = favoriteService.toggleFavorite(productId, userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("isFavorite", isNowFavorite);
        response.put("favoriteCount", favoriteService.getFavoriteCount(userEmail));

        return ResponseEntity.ok(response);
    }

    /**
     * Получить количество избранных товаров
     */
    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getFavoritesCount() {
        String userEmail = getCurrentUserEmail();
        long count = favoriteService.getFavoriteCount(userEmail);
        return ResponseEntity.ok(count);
    }

    /**
     * Проверить, находится ли товар в избранном
     */
    @GetMapping("/check/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long productId) {
        String userEmail = getCurrentUserEmail();
        boolean isFavorite = favoriteService.isFavorite(productId, userEmail);
        return ResponseEntity.ok(isFavorite);
    }

    /**
     * Очистить все избранное пользователя
     */
    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clearFavorites() {
        String userEmail = getCurrentUserEmail();
        favoriteService.clearFavorites(userEmail);
        return ResponseEntity.ok().build();
    }

    /**
     * Получить ID всех избранных товаров
     */
    @GetMapping("/ids")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Long>> getFavoriteProductIds() {
        String userEmail = getCurrentUserEmail();
        List<Long> productIds = favoriteService.getFavoriteProductIds(userEmail);
        return ResponseEntity.ok(productIds);
    }
}
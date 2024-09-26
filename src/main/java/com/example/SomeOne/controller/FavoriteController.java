package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Favorites.response.FavoriteResponse;
import com.example.SomeOne.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.SomeOne.config.SecurityUtil.getAuthenticatedUserId;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoritesService favoriteService;

    @PostMapping("/add")
    public ResponseEntity<Void> addFavorite(@RequestParam Long businessId) {
        Long userId = getAuthenticatedUserId();
        favoriteService.addFavorite(userId, businessId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFavorite(@RequestParam Long businessId) {
        Long userId = getAuthenticatedUserId();
        favoriteService.removeFavorite(userId, businessId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/{businessId}")
    public ResponseEntity<FavoriteResponse> getFavorite(@PathVariable Long businessId) {
        Long userId = getAuthenticatedUserId();
        FavoriteResponse response = favoriteService.getFavorite(userId, businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponse>> getFavorites() {
        Long userId = getAuthenticatedUserId();
        List<FavoriteResponse> response = favoriteService.getFavorites(userId);
        return ResponseEntity.ok(response);
    }

    // 비즈니스 타입별로 좋아요한 장소 조회 API
    @GetMapping("/{userId}/business-type/{businessType}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByBusinessType(
            @PathVariable String businessType) {
        Long userId = getAuthenticatedUserId();
        List<FavoriteResponse> response = favoriteService.getFavoritesByBusinessType(userId, businessType);
        return ResponseEntity.ok(response);
    }
}

package com.example.SomeOne.controller;

import com.example.SomeOne.dto.Favorites.response.FavoriteResponse;
import com.example.SomeOne.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor

@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoritesService favoriteService;

    @PostMapping("/add")
    public ResponseEntity<Void> addFavorite(@RequestParam Long userId, @RequestParam Long businessId) {
        favoriteService.addFavorite(userId, businessId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long businessId) {
        favoriteService.removeFavorite(userId, businessId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/{businessId}")
    public ResponseEntity<FavoriteResponse> getFavorite(@PathVariable Long userId, @PathVariable Long businessId) {
        FavoriteResponse response = favoriteService.getFavorite(userId, businessId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteResponse>> getFavorites(@PathVariable Long userId) {
        List<FavoriteResponse> response = favoriteService.getFavorites(userId);
        return ResponseEntity.ok(response);
    }
}

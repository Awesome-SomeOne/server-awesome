package com.example.SomeOne.service;

import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.Favorites;
import com.example.SomeOne.domain.Users;

import com.example.SomeOne.dto.Favorites.response.FavoriteResponse;
import com.example.SomeOne.exception.ResourceNotFoundException;
import com.example.SomeOne.repository.BusinessesRepository;

import com.example.SomeOne.repository.FavoritesRepository;
import com.example.SomeOne.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoritesService {

    private final FavoritesRepository favoriteRepository;
    private final UserRepository usersRepository;
    private final BusinessesRepository businessesRepository;

    @Transactional
    public void addFavorite(Long userId, Long businessId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Businesses business = businessesRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        // 사용자-비즈니스 조합으로 이미 좋아요가 있는지 확인
        boolean isAlreadyFavorite = favoriteRepository.existsByUserAndBusiness(user, business);
        if (!isAlreadyFavorite) {
            Favorites favorite = new Favorites(user, business);
            favoriteRepository.save(favorite);
        }
    }

    public boolean findFavorite(Long userId, Long businessId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Businesses business = businessesRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        return favoriteRepository.existsByUserAndBusiness(user, business);
    }

    @Transactional
    public void removeFavorite(Long userId, Long businessId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Businesses business = businessesRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        Favorites favorite = favoriteRepository.findByUserAndBusiness(user, business)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for userId: " + userId + " and businessId: " + businessId));

        favoriteRepository.delete(favorite);
    }

    public FavoriteResponse getFavorite(Long userId, Long businessId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Businesses business = businessesRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        Favorites favorite = favoriteRepository.findByUserAndBusiness(user, business)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for userId: " + userId + " and businessId: " + businessId));

        return new FavoriteResponse(favorite.getBusiness().getBusiness_name(),
                favorite.getBusiness().getBusinessType().name(),
                favorite.getBusiness().getAddress(),
                favorite.getBusiness().getImg_url());
    }

    public List<FavoriteResponse> getFavorites(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Favorites> favorites = favoriteRepository.findByUser(user);

        return favorites.stream()
                .map(favorite -> new FavoriteResponse(favorite.getBusiness().getBusiness_name(),
                        favorite.getBusiness().getBusinessType().name(),
                        favorite.getBusiness().getAddress(),
                        favorite.getBusiness().getImg_url()))
                .collect(Collectors.toList());
    }
}

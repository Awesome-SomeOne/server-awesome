package com.example.SomeOne.dto.Favorites.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteResponse {
    private String businessName;
    private String businessType;
    private String address;
    private String imageUrl;
}
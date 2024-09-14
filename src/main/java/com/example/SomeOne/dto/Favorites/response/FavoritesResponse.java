package com.example.SomeOne.dto.Favorites.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class FavoritesResponse {
    private String status;
    private String message;

    public FavoritesResponse(String status, String message){
        this.status = status;
        this.message = message;
    }
}

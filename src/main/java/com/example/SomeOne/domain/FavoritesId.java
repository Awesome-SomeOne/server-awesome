package com.example.SomeOne.domain;

import java.io.Serializable;
import java.util.Objects;

public class FavoritesId implements Serializable {
    private Long user;
    private Long business;

    // 기본 생성자
    public FavoritesId() {}

    // 매개변수가 있는 생성자
    public FavoritesId(Long user, Long business) {
        this.user = user;
        this.business = business;
    }

    // hashCode와 equals 메서드 구현
    @Override
    public int hashCode() {
        return Objects.hash(user, business);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FavoritesId that = (FavoritesId) obj;
        return Objects.equals(user, that.user) && Objects.equals(business, that.business);
    }
}

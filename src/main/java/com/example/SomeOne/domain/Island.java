package com.example.SomeOne.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Island {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String address;
    private String img_url;

    private Double latitude; //위도
    private Double longitude; //경도

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

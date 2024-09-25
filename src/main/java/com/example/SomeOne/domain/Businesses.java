package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.Business_category;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Businesses {

    @Id @GeneratedValue
    private Long business_id;

    private String business_name;
    @Enumerated(EnumType.STRING)
    private Business_category businessType; // 식당, 숙박, 관광지, 액티비티
    private String address;
    private String x_address;
    private String y_address;
    private String img_url;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;

    @Builder
    public Businesses(String business_name, String businessType, String address,
                    String x_address, String y_address, String img_url, Island island) {
        this.business_name = business_name;
        this.businessType = Business_category.valueOf(businessType);
        this.address = address;
        this.x_address = x_address;
        this.y_address = y_address;
        this.img_url = img_url;
        this.island = island;
    }
}

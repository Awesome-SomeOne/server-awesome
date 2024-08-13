package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.Business_category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Businesses {

    @Id @GeneratedValue
    private Long business_id;

    private String business_name;
    private Business_category business_type; // 식당, 숙박, 관광지, 액티비티
    private String address;
    private String x_address;
    private String y_address;
    private String img_url;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "island_id")
    private Island island;
}

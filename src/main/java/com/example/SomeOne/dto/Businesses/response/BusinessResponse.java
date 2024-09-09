package com.example.SomeOne.dto.Businesses.response;

import com.example.SomeOne.domain.Businesses;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResponse {

    private Long businessId;
    private String businessName;
    private String businessType;
    private String address;
    private String mapX;
    private String mapY;
    private String imageUrl;

    public BusinessResponse(Businesses business) {
        this.businessId = business.getBusiness_id();
        this.businessName = business.getBusiness_name();
        this.businessType = business.getBusinessType().name();
        this.address = business.getAddress();
        this.mapX = business.getX_address();
        this.mapY = business.getY_address();
        this.imageUrl = business.getImg_url();
    }
}

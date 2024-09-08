package com.example.SomeOne.dto.Businesses.response;

import com.example.SomeOne.domain.enums.Business_category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamousPlaceResponse {
    private Long businessId;
    private String name;
    private Business_category businessCategory;
    private String address;
    private String imgUrl;
}

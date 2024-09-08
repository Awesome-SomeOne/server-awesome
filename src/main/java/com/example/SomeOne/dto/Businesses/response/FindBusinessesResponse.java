package com.example.SomeOne.dto.Businesses.response;

import com.example.SomeOne.domain.enums.Business_category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindBusinessesResponse {
    private Long id;
    private String name;
    private String address;
    private Business_category category;
}

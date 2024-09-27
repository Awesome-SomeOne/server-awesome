package com.example.SomeOne.dto.Businesses.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPlaceSearchResponse {

    private List<Document> documents;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        private String id;
        private String place_name;
        private String address_name;
        private String x;
        private String y;
        private String category_name;
    }

    // 비즈니스 응답으로 변환하는 메서드
    public List<BusinessResponse> toBusinessResponseList() {
        return documents.stream()
                .map(doc -> BusinessResponse.builder()
                        .businessName(doc.getPlace_name())
                        .businessType(doc.getCategory_name())
                        .address(doc.getAddress_name())
                        .mapX(doc.getX())
                        .mapY(doc.getY())
                        .imageUrl(null)  // 이미지 URL이 없으므로 null 전달 가능
                        .build())
                .collect(Collectors.toList());
    }
}
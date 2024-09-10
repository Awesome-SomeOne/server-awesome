//package com.example.SomeOne.domain;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//public class Favorites {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private Users user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "business_id")
//    private Businesses business;
//
//    public Favorites(Users user, Businesses business) {
//        this.user = user;
//        this.business = business;
//    }
//}
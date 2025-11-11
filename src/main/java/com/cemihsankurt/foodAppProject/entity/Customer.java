package com.cemihsankurt.foodAppProject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "cart") // ToString'de sonsuz döngü yapma
@EqualsAndHashCode(exclude = "cart") // Equals/HashCode'da sonsuz döngü yapma
public class Customer {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Cart cart;

    @Column(name = "fcm_token")
    private String fcmToken;

    @OneToMany(
            mappedBy = "customer", // Address sınıfındaki 'customer' alanıyla bağlı
            cascade = CascadeType.ALL, // Müşteri silinirse adresleri de sil
            orphanRemoval = true // Listeden adres silinirse DB'den de sil
    )
    private List<Address> addresses = new ArrayList<>();


}

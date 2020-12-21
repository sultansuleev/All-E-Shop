package com.example.ht7.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "paid_date")
    private LocalDateTime paid_date;

    @Column(name = "cust")
    private String customer;

    @Column(name = "amount")
    private int amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private ShopItems items;

    public Cart(int i, ShopItems item) {
        this.setAmount(i);
        this.setItems(item);
    }
}

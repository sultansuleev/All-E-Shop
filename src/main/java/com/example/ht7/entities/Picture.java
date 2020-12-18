package com.example.ht7.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pictures")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "addedDate")
    private LocalDate addedDate = LocalDate.now();

    @ManyToOne(fetch = FetchType.EAGER)
    private ShopItems item;


}

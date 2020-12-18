package com.example.ht7.entities;

import com.example.ht7.DB.ShopItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "addedDate")
    private Date addedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShopItems items;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users author;
}

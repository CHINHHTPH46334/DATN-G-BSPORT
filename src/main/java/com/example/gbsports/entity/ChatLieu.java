package com.example.gbsports.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "chat_lieu")
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_chat_lieu;
    private String ma_chat_lieu;
    private String ten_chat_lieu;
}

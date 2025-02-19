package com.example.gbsports.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Roles {
@Id
    private Integer id_roles;
    private String ma_roles;
    private String ten_roles;
}

package com.example.gbsports.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Roles {
@Id
    private Integer id_roles;
//    @NotBlank(message = "Không được để trống mã roles")
    private String ma_roles;
    @NotBlank(message = "Không được để trống tên chức vụ")
    private String ten_roles;

    @Override
    public String toString() {
        return "Roles{" +
                "id_roles=" + id_roles +
                ", ma_roles='" + ma_roles + '\'' +
                ", ten_roles='" + ten_roles + '\'' +
                '}';
    }
}

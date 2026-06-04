package com.example.datn.topping.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TOPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_topping")
    private Integer idTopping;

    @Column(name = "ten_topping")
    private String tenTopping;

    @Column(name = "gia_topping")
    private BigDecimal giaTopping;

    @Column(name = "trang_thai")
    private Integer trangThai;
}
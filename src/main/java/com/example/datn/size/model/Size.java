package com.example.datn.size.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SIZE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_size")
    private Integer idSize;

    @Column(name = "ten_size")
    private String tenSize;

    @Column(name = "phu_thu")
    private BigDecimal phuThu;

    @Column(name = "thu_tu")
    private Integer thuTu;
}
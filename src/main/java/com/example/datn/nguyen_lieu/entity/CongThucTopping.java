package com.example.datn.nguyen_lieu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CONG_THUC_TOPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CongThucTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ctt")
    private Integer idCtt;

    @Column(name = "id_topping")
    private Integer idTopping;

    @ManyToOne
    @JoinColumn(name = "id_nguyen_lieu")
    private NguyenLieu nguyenLieu;

    @Column(name = "so_luong_can_dung")
    private Double soLuongCanDung;
}
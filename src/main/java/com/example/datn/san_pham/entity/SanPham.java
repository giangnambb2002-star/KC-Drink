package com.example.datn.san_pham.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "SAN_PHAM")
@Data
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_san_pham")
    private Integer idSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    @Column(name = "gia")
    private Double gia;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "id_danh_muc")
    private Integer idDanhMuc;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

}

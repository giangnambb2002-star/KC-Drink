package com.example.datn.hoa_don.model;

import com.example.datn.topping.model.Topping;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HDCT_TOPPING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HdctTopping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hdct_topping")
    private Integer idHdctTopping;

    @ManyToOne
    @JoinColumn(name = "id_hdct")
    private HoaDonChiTiet hoaDonChiTiet;

    @ManyToOne
    @JoinColumn(name = "id_topping")
    private Topping topping;
}
package com.example.datn.nhan_vien.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NhanVienUpdateRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String tenNhanVien;

    @Pattern(
            regexp = "^0[3|5|7|8|9][0-9]{8}$"
    )
    private String sdt;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String chucVu;

    @NotBlank
    @Size(min = 5, max = 30)
    private String username;

}
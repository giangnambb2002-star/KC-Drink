package com.example.datn.tai_khoan.service;

import com.example.datn.tai_khoan.model.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;

    public TaiKhoan login(String username, String password) {

        return taiKhoanRepository.findByUsername(username)
                .filter(tk -> tk.getPassword().equals(password))
                .filter(tk -> tk.getTrangThai() == 1)
                .orElse(null);
    }
}
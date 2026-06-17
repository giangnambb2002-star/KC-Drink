package com.example.datn.tai_khoan.service;

import com.example.datn.tai_khoan.dto.TaiKhoanRequest;
import com.example.datn.tai_khoan.entity.TaiKhoan;
import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaiKhoanService {
    private final TaiKhoanRepository repository;

    private final PasswordEncoder passwordEncoder;

    public List<TaiKhoan> getAll() {
        return repository.findAll();
    }

    public TaiKhoan getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
    }

    public TaiKhoan create(TaiKhoanRequest request) {

        TaiKhoan taiKhoan = new TaiKhoan();

        taiKhoan.setUsername(request.getUsername());
        taiKhoan.setPassword(passwordEncoder.encode(request.getPassword()));
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setRole(request.getRole());
        taiKhoan.setTrangThai(1);
        taiKhoan.setNgayTao(LocalDateTime.now());
        return repository.save(taiKhoan);
    }

    public TaiKhoan update(
            Integer id,
            TaiKhoanRequest request
    ) {

        TaiKhoan taiKhoan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        taiKhoan.setUsername(request.getUsername());
        taiKhoan.setPassword(passwordEncoder.encode(request.getPassword()));
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setRole(request.getRole());
        taiKhoan.setNgayCapNhat(LocalDateTime.now());
        return repository.save(taiKhoan);
    }
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài khoản");
        }repository.deleteById(id);
    }


    public TaiKhoan lock(Integer id) {
        TaiKhoan taiKhoan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        taiKhoan.setTrangThai(0);
        return repository.save(taiKhoan);
    }


    public TaiKhoan unlock(Integer id) {
        TaiKhoan taiKhoan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
        taiKhoan.setTrangThai(1);
        return repository.save(taiKhoan);
    }
}

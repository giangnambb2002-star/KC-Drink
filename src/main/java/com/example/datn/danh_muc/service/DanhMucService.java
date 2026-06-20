package com.example.datn.danh_muc.service;

import com.example.datn.danh_muc.entity.DanhMuc;
import com.example.datn.danh_muc.repository.DanhMucRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanhMucService {

    @Autowired
    private DanhMucRepository repository;

    public List<DanhMuc> getAll() {
        return repository.findAll();
    }

    public DanhMuc getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void add(DanhMuc danhMuc) {
        repository.save(danhMuc);
    }

    public void update(Integer id, DanhMuc danhMuc) {

        DanhMuc old = repository.findById(id).orElse(null);

        if (old != null) {
            old.setTenDanhMuc(danhMuc.getTenDanhMuc());
            old.setMoTa(danhMuc.getMoTa());
            old.setTrangThai(danhMuc.getTrangThai());

            repository.save(old);
        }
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
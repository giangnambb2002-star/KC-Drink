package com.example.datn.size.repository;

import com.example.datn.size.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository
        extends JpaRepository<Size, Integer> {
}
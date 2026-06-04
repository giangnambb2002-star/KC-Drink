package com.example.datn.topping.repository;

import com.example.datn.topping.model.Topping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToppingRepository
        extends JpaRepository<Topping, Integer> {
}
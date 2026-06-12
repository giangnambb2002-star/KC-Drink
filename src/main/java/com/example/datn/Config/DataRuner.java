package com.example.datn.Config;

import com.example.datn.tai_khoan.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataRuner implements CommandLineRunner {
    private final TaiKhoanRepository repository;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("==============");
        System.out.println("SO TAI KHOAN = " + repository.count());
        System.out.println("==============");

    }

}

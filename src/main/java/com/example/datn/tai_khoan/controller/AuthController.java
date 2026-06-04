package com.example.datn.tai_khoan.controller;

import com.example.datn.tai_khoan.model.TaiKhoan;
import com.example.datn.tai_khoan.service.TaiKhoanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {

        if (session.getAttribute("user") != null) {
            return "redirect:/nhan-vien";
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        TaiKhoan taiKhoan =
                taiKhoanService.login(username, password);

        if (taiKhoan == null) {

            model.addAttribute(
                    "error",
                    "Sai tài khoản hoặc mật khẩu"
            );

            return "auth/login";
        }

        session.setAttribute("user", taiKhoan);

        System.out.println("LOGIN SUCCESS");
        System.out.println("ROLE = " + taiKhoan.getRole());

        return "redirect:/nhan-vien";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/auth/login";
    }
}
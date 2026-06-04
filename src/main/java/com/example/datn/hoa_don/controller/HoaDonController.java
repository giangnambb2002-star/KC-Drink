    package com.example.datn.hoa_don.controller;
    
    import com.example.datn.hoa_don.model.HoaDon;
    import com.example.datn.hoa_don.model.HoaDonChiTiet;
    import com.example.datn.hoa_don.repository.HoaDonChiTietRepository;
    import com.example.datn.hoa_don.repository.HoaDonRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;

    import java.util.List;

    @Controller
    @RequiredArgsConstructor
    public class HoaDonController {
    
        private final HoaDonRepository hoaDonRepository;
        private final HoaDonChiTietRepository hoaDonChiTietRepository;

        @GetMapping("/nhan-vien/hoa-don")
        public String danhSachHoaDon(Model model) {
    
            model.addAttribute("menuActive", "hoa-don");
            model.addAttribute("hoaDons", hoaDonRepository.findAll());
    
            return "nhan-vien/hoa-don";
        }
        @GetMapping("/nhan-vien/hoa-don/{id}")
        public String chiTietHoaDon(
                @PathVariable Integer id,
                Model model) {

            HoaDon hoaDon = hoaDonRepository
                    .findById(id)
                    .orElse(null);

            List<HoaDonChiTiet> chiTiets = hoaDonChiTietRepository.findByHoaDon_IdHoaDon(id);

            model.addAttribute("hoaDon", hoaDon);
            model.addAttribute("chiTiets", chiTiets);

            return "nhan-vien/hoa-don-chi-tiet";
        }
    }
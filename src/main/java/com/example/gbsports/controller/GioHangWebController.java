package com.example.gbsports.controller;

import com.example.gbsports.repository.GioHangRepository;
import com.example.gbsports.repository.GioHangWebRepo;
import com.example.gbsports.entity.ChiTietGioHang;
import com.example.gbsports.entity.ChiTietGioHangId;
import com.example.gbsports.entity.GioHang;
import com.example.gbsports.repository.*;
import com.example.gbsports.response.GioHangWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = {RequestMethod.GET,
        RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@RequestMapping("/gioHangWeb")
//@PreAuthorize("hasAnyRole('ROLE_KH')")
public class GioHangWebController {
    @Autowired
    private GioHangWebRepo gioHangWebRepo;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    @GetMapping("/gioHangByKH")
    public List<GioHangWebResponse> listGHByKH(@RequestParam("idKhachHang") Integer idKhachHang) {
        return gioHangWebRepo.listGioHangByKhachHang(idKhachHang);
    }

    @GetMapping("/danhSachDiaChi")
    public List<GioHangWebResponse> listDiaChiByKH(@RequestParam("idKhachHang") Integer idKhachHang) {
        return gioHangWebRepo.listDiaChiByKH(idKhachHang);
    }

    @PostMapping("/themGHByIdKH")
    public GioHang themGHByIdKH(@RequestParam("idKH") Integer idKH,
                                @RequestParam("idCTSP") Integer idCTSP,
                                @RequestParam("soLuong") Integer soLuong) {

        if (idKH == null || idCTSP == null || soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Thông tin đầu vào không hợp lệ.");
        }

        // Lấy hoặc tạo mới giỏ hàng
        GioHang gioHang = gioHangRepository.findAll().stream()
                .filter(gh -> gh.getKhachHang().getIdKhachHang().equals(idKH))
                .findFirst()
                .orElseGet(() -> {
                    GioHang ghMoi = new GioHang();
                    ghMoi.setKhachHang(khachHangRepo.findById(idKH).orElseThrow());
                    return gioHangRepository.save(ghMoi);
                });

        // Tạo khóa chính composite
        ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangId();
        chiTietGioHangId.setIdGioHang(gioHang.getId_gio_hang());
        chiTietGioHangId.setIdChiTietSanPham(idCTSP);

        // Kiểm tra chi tiết giỏ hàng đã có chưa
        Optional<ChiTietGioHang> optionalCTGH = chiTietGioHangRepository.findById(chiTietGioHangId);

        if (optionalCTGH.isPresent()) {
            // Nếu đã có => cộng số lượng
            ChiTietGioHang ctgh = optionalCTGH.get();
            ctgh.setSoLuong(ctgh.getSoLuong() + soLuong);
            chiTietGioHangRepository.save(ctgh);
        } else {
            // Nếu chưa có => tạo mới
            ChiTietGioHang ctghMoi = new ChiTietGioHang();
            ctghMoi.setId(chiTietGioHangId);
            ctghMoi.setGioHang(gioHang);
            ctghMoi.setChiTietSanPham(chiTietSanPhamRepo.findById(idCTSP).orElseThrow());
            ctghMoi.setSoLuong(soLuong);
            chiTietGioHangRepository.save(ctghMoi);
        }

        return gioHang;

    }

    @DeleteMapping("/deleteGHByIdKH")
    public GioHang deleteGHByIdKH(@RequestParam("idKH") Integer idKH,
                                  @RequestParam("idCTSP") Integer idCTSP,
                                  @RequestParam("soLuong") Integer soLuong) {

        if (idKH == null || idCTSP == null || soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Thông tin đầu vào không hợp lệ.");
        }

        // Tìm giỏ hàng của khách
        GioHang gioHang = gioHangRepository.findAll().stream()
                .filter(gh -> gh.getKhachHang().getIdKhachHang().equals(idKH))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng cho khách hàng này."));

        // Tạo khóa composite
        ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangId();
        chiTietGioHangId.setIdGioHang(gioHang.getId_gio_hang());
        chiTietGioHangId.setIdChiTietSanPham(idCTSP);

        // Kiểm tra sản phẩm có trong giỏ hàng chưa
        ChiTietGioHang ctgh = chiTietGioHangRepository.findById(chiTietGioHangId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không có trong giỏ hàng."));

        // Xử lý giảm số lượng
        int soLuongHienTai = ctgh.getSoLuong();
        if (soLuong >= soLuongHienTai) {
            // Nếu số lượng sau giảm <= 0, xóa luôn chi tiết
            chiTietGioHangRepository.delete(ctgh);
        } else {
            // Nếu vẫn còn, cập nhật lại
            ctgh.setSoLuong(soLuongHienTai - soLuong);
            chiTietGioHangRepository.save(ctgh);
        }

        return gioHang;
    }
    @PostMapping("/addGHByIdKH")
    public GioHang addGHByIdKH(@RequestParam("idKH") Integer idKH,
                               @RequestParam("idCTSP") Integer idCTSP,
                               @RequestParam("soLuong") Integer soLuong) {

        if (idKH == null || idCTSP == null || soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Thông tin đầu vào không hợp lệ.");
        }

        // Tìm hoặc tạo giỏ hàng cho khách
        GioHang gioHang = gioHangRepository.findAll().stream()
                .filter(gh -> gh.getKhachHang().getIdKhachHang().equals(idKH))
                .findFirst()
                .orElseGet(() -> {
                    GioHang ghMoi = new GioHang();
                    ghMoi.setKhachHang(khachHangRepo.findById(idKH)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng")));
                    return gioHangRepository.save(ghMoi);
                });

        // Tạo khóa composite
        ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangId();
        chiTietGioHangId.setIdGioHang(gioHang.getId_gio_hang());
        chiTietGioHangId.setIdChiTietSanPham(idCTSP);

        // Kiểm tra xem sản phẩm đã tồn tại trong giỏ hàng chưa
        Optional<ChiTietGioHang> optionalCTGH = chiTietGioHangRepository.findById(chiTietGioHangId);

        if (optionalCTGH.isPresent()) {
            // Nếu đã có => cộng thêm số lượng
            ChiTietGioHang ctgh = optionalCTGH.get();
            ctgh.setSoLuong(ctgh.getSoLuong() + soLuong);
            chiTietGioHangRepository.save(ctgh);
        } else {
            // Nếu chưa có => thêm mới
            ChiTietGioHang ctghMoi = new ChiTietGioHang();
            ctghMoi.setId(chiTietGioHangId);
            ctghMoi.setGioHang(gioHang);
            ctghMoi.setChiTietSanPham(chiTietSanPhamRepo.findById(idCTSP)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi tiết sản phẩm")));
            ctghMoi.setSoLuong(soLuong);
            chiTietGioHangRepository.save(ctghMoi);
        }

        return gioHang;
    }


}

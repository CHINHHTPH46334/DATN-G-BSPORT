package com.example.gbsports.response;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.ThuongHieu;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;

public interface SanPhamView {
     Integer getId_san_pham();
     String getMa_san_pham();
     String getTen_san_pham();
     String getMo_ta();
     String getTrang_thai();
     String getTen_danh_muc();
     String getTen_thuong_hieu();
     String getTen_chat_lieu();
     Integer getTong_so_luong();
     String getHinh_anh();
     Date getNgay_sua_moi();
}

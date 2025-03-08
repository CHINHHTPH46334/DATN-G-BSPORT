package com.example.gbsports.respon;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.ThuongHieu;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public interface SanPhamView {
     Integer getId_san_pham();
     String getMa_san_pham();
     String getTen_san_pham();
     String getMo_ta();
     String getTrang_thai();
     Boolean getGioi_tinh();
     String getTen_danh_muc();
     String getTen_thuong_hieu();
     String getTen_chat_lieu();
     Integer getSo_luong();
     String getHinh_anh();
}

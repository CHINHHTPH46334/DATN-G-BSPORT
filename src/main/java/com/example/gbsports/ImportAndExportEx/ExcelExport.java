package com.example.gbsports.ImportAndExportEx;

import com.example.gbsports.response.ChiTietSanPhamView;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelExport {
    public static ByteArrayInputStream sanPhamToExcel(List<ChiTietSanPhamView> list) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ChiTietSanPham");
            Row headerRow = sheet.createRow(0);

            String[] headers = {"ID CTSP","Tên sản phẩm",
                    "QR Code","Giá nhập", "Giá Bán",
                    "Ngày tạo","Ngày sửa","Số Lượng",
                    "Trạng Thái", "Kích thước","Đơn vị",
                    "Chất liệu","Danh mục","Thương hiệu",
                    "Màu"};
            for (int i = 0; i < headers.length; i++) {
                ((Row) headerRow).createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (ChiTietSanPhamView chiTiet : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(chiTiet.getId_chi_tiet_san_pham());
                row.createCell(1).setCellValue(chiTiet.getTen_san_pham());
                row.createCell(2).setCellValue(chiTiet.getQr_code());
                row.createCell(3).setCellValue(chiTiet.getGia_nhap());
                row.createCell(4).setCellValue(chiTiet.getGia_ban());
                row.createCell(5).setCellValue(chiTiet.getNgay_tao().toString());
                row.createCell(6).setCellValue(chiTiet.getNgay_sua().toString());
                row.createCell(7).setCellValue(chiTiet.getSo_luong());
                row.createCell(8).setCellValue(chiTiet.getTrang_thai());
                row.createCell(9).setCellValue(chiTiet.getGia_tri());
                row.createCell(10).setCellValue(chiTiet.getDon_vi());
                row.createCell(11).setCellValue(chiTiet.getTen_chat_lieu());
                row.createCell(12).setCellValue(chiTiet.getTen_danh_muc());
                row.createCell(13).setCellValue(chiTiet.getTen_thuong_hieu());
                row.createCell(14).setCellValue(chiTiet.getTen_mau());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất file Excel: " + e.getMessage());
        }
    }
}

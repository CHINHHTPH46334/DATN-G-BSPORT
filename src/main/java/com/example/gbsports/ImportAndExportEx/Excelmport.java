package com.example.gbsports.ImportAndExportEx;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.request.SanPhamRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class Excelmport {
    @Autowired
    SanPhamRepo sanPhamRepo;
    @Autowired
    ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    ChatLieuRepo chatLieuRepo;
    @Autowired
    DanhMucRepo danhMucRepo;
    @Autowired
    KichThuocRepo kichThuocRepo;
    @Autowired
    MauSacRepo mauSacRepo;
    @Autowired
    ThuongHieuRepo thuongHieuRepo;

    public static Integer getIntegerValueFromCell(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null; // Trả về null nếu lỗi
                }
            default:
                return null;
        }
    }

    private String getStringValueFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue()); // Chuyển số thành chuỗi
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Date getDateValueFromCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue(); // Trả về kiểu Date
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày cần đọc
                return dateFormat.parse(cell.getStringCellValue().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null; // Nếu không phải kiểu hợp lệ, trả về null
    }

    private Float getFloatValueFromCell(Cell cell) {
        if (cell == null) {
            return null; // Nếu ô rỗng, trả về null
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (float) cell.getNumericCellValue(); // Trả về giá trị số dạng float
            case STRING:
                try {
                    return Float.parseFloat(cell.getStringCellValue().trim()); // Chuyển đổi chuỗi sang float
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return null; // Trả về null nếu không phải dạng hợp lệ
    }

    public static Boolean getBooleanValueFromCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim().toLowerCase();
            return value.equals("true") || value.equals("1") || value.equals("yes");
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue() == 1;
        }
        return false;
    }
    public ArrayList<ChiTietSanPhamRequest> readExcel(MultipartFile file) throws IOException {
        ArrayList<ChiTietSanPhamRequest> list = new ArrayList<>();
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("File không hợp lệ. Vui lòng chọn file excel khác");
        }
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                // ✅ Đọc dữ liệu cho Sản Phẩm
                SanPhamRequest sanPham = new SanPhamRequest();
                sanPham.setId_san_pham(getIntegerValueFromCell(row.getCell(0)));
                sanPham.setMa_san_pham(row.getCell(1).getStringCellValue());
                sanPham.setTen_san_pham(row.getCell(2).getStringCellValue());
                sanPham.setMo_ta(row.getCell(3).getStringCellValue());
                sanPham.setTrang_thai(row.getCell(4).getStringCellValue());
                sanPham.setGioi_tinh(getBooleanValueFromCell(row.getCell(5)));

                // ✅ Đọc các đối tượng liên quan (Danh m    ục, Thương hiệu, Chất liệu)
                DanhMuc danhMuc = danhMucRepo.findById(getIntegerValueFromCell(row.getCell(6)))
                        .orElseGet(() -> new DanhMuc(null,
                                getStringValueFromCell(row.getCell(7)),
                                getStringValueFromCell(row.getCell(8)),
                                getStringValueFromCell(row.getCell(9)),
                                getDateValueFromCell(row.getCell(10)),
                                getDateValueFromCell(row.getCell(11))));
                sanPham.setDanhMuc(danhMuc);
                ThuongHieu thuongHieu = thuongHieuRepo.findById(getIntegerValueFromCell(row.getCell(12)))
                        .orElseGet(() -> new ThuongHieu(null,
                                getStringValueFromCell(row.getCell(13)),
                                getStringValueFromCell(row.getCell(14)),
                                getStringValueFromCell(row.getCell(15)),
                                getDateValueFromCell(row.getCell(16)),
                                getDateValueFromCell(row.getCell(17))));
                sanPham.setThuongHieu(thuongHieu);
                ChatLieu chatLieu = chatLieuRepo.findById(getIntegerValueFromCell(row.getCell(18)))
                        .orElseGet(() -> new ChatLieu(null,
                                getStringValueFromCell(row.getCell(19)),
                                getStringValueFromCell(row.getCell(20))));
                sanPham.setChatLieu(chatLieu);

                // ✅ Đọc dữ liệu cho Chi Tiết Sản Phẩm
                SanPham sp = new SanPham();
                BeanUtils.copyProperties(sanPham, sp);
                ChiTietSanPhamRequest chiTietSanPham = new ChiTietSanPhamRequest();
                chiTietSanPham.setSanPham(sp);
                chiTietSanPham.setQr_code(row.getCell(21).getStringCellValue());
                chiTietSanPham.setGia_ban((float) row.getCell(22).getNumericCellValue());
                chiTietSanPham.setGia_nhap((float) row.getCell(23).getNumericCellValue());
                chiTietSanPham.setSo_luong((int) row.getCell(24).getNumericCellValue());
                chiTietSanPham.setTrang_thai(row.getCell(25).getStringCellValue());
                chiTietSanPham.setNgay_tao(getDateValueFromCell(row.getCell(26)));
                chiTietSanPham.setNgay_sua(getDateValueFromCell(row.getCell(27)));

                // ✅ Đọc dữ liệu cho Màu sắc & Kích thước
                MauSac mauSac = mauSacRepo.findById(getIntegerValueFromCell(row.getCell(28)))
                        .orElseGet(() -> new MauSac(null,
                                getStringValueFromCell(row.getCell(29)),
                                getStringValueFromCell(row.getCell(30))
                        ));
                chiTietSanPham.setMauSac(mauSac);
                KichThuoc kichThuoc = kichThuocRepo.findById(getIntegerValueFromCell(row.getCell(31)))
                        .orElseGet(() -> new KichThuoc(null,
                                getStringValueFromCell(row.getCell(32)),
                                getStringValueFromCell(row.getCell(33)),
                                getStringValueFromCell(row.getCell(34))
                        ));
                chiTietSanPham.setKichThuoc(kichThuoc); // Cột 14: Kích thước

                // ✅ Thêm vào danh sách kết quả
                list.add(chiTietSanPham);
            }
        }
        return list;
    }


}

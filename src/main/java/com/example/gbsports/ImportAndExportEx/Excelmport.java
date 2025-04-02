package com.example.gbsports.ImportAndExportEx;

import com.example.gbsports.entity.*;
import com.example.gbsports.repository.*;
import com.example.gbsports.request.ChiTietSanPhamRequest;
import com.example.gbsports.request.SanPhamRequest;
import com.example.gbsports.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class Excelmport {
    @Autowired
    SanPhamService sanPhamService;
    @Autowired
    ChiTietSanPhamRepo chiTietSanPhamRepo;
    @Autowired
    ChatLieuService chatLieuService;
    @Autowired
    DanhMucService danhMucService;
    @Autowired
    KichThuocService kichThuocService;
    @Autowired
    MauSacService mauSacService;
    @Autowired
    ThuongHieuService thuongHieuService;

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
                // Kiểm tra xem ô có phải là ngày tháng không
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Nếu là ngày tháng, chuyển đổi sang chuỗi theo định dạng mong muốn
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    // Nếu là số, chuyển đổi thành chuỗi mà không làm mất phần thập phân
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Xử lý ô công thức: lấy giá trị được tính toán
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getStringCellValue().trim();
                    case NUMERIC:
                        return String.valueOf(cell.getNumericCellValue());
                    case BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                    case ERROR:
                        return "ERROR: " + cell.getErrorCellValue();
                    default:
                        return null;
                }
            case ERROR:
                // Xử lý ô lỗi: trả về chuỗi biểu thị lỗi
                return "ERROR: " + cell.getErrorCellValue();
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

    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    public ArrayList<ChiTietSanPham> readExcel(MultipartFile file) throws IOException {
        ArrayList<ChiTietSanPham> list = new ArrayList<>();
        Map<String, ChiTietSanPham> mapChiTietSanPham = new HashMap<>();
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            throw new IllegalArgumentException("File không hợp lệ. Vui lòng chọn file excel khác");
        }
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                // ✅ Đọc dữ liệu cho Sản Phẩm
                String tenSanPham = getStringValueFromCell(row.getCell(0));
//                Boolean gioiTinh = getBooleanValueFromCell(row.getCell(1));
//                BigDecimal giaNhap = getBigDecimalValue(row.getCell(1));
                BigDecimal giaBan = getBigDecimalValue(row.getCell(1));
                Integer soLuong = getIntegerValueFromCell(row.getCell(2));
                String giaTriKichThuoc = getStringValueFromCell(row.getCell(3));
                String donViKichThuoc = getStringValueFromCell(row.getCell(4));
                String tenChatLieu = getStringValueFromCell(row.getCell(5));
                String tenDanhMuc = getStringValueFromCell(row.getCell(6));
                String tenThuongHieu = getStringValueFromCell(row.getCell(7));
                String mauSacInfo = getStringValueFromCell(row.getCell(8));

                // ✅ Đọc các đối tượng liên quan (Danh m    ục, Thương hiệu, Chất liệu)
                DanhMuc danhMuc = danhMucService.getDanhMucOrCreateDanhMuc(tenDanhMuc);
                ThuongHieu thuongHieu = thuongHieuService.getThuongHieuOrCreateThuongHieu(tenThuongHieu);
                ChatLieu chatLieu = chatLieuService.getChatLieuOrCreateChatLieu(tenChatLieu);
                SanPham sanPham = sanPhamService.getSanPhamOrCreateSanPham(tenSanPham, thuongHieu, danhMuc, chatLieu);
                KichThuoc kichThuoc = kichThuocService.getKichThuocOrCreateKichThuoc(giaTriKichThuoc, donViKichThuoc);
                MauSac mauSac = mauSacService.getMauSacOrCreateMauSac(mauSacInfo);
                // ✅ Đọc dữ liệu cho Chi Tiết Sản Phẩm
                String key = sanPham.getId_san_pham() + "-"
                        + mauSac.getId_mau_sac() + "-"
                        + kichThuoc.getId_kich_thuoc() + "-";


                // ✅ Kiểm tra xem biến thể đã tồn tại chưa
                if (mapChiTietSanPham.containsKey(key)) {
                    // Nếu đã tồn tại, cập nhật số lượng
                    ChiTietSanPham existingCtsp = mapChiTietSanPham.get(key);
                    existingCtsp.setSo_luong(existingCtsp.getSo_luong() + soLuong);
                } else {
                    // Nếu chưa tồn tại, thêm mới vào danh sách
                    ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
                    chiTietSanPham.setSanPham(sanPham);
                    chiTietSanPham.setGia_ban(giaBan);

                    chiTietSanPham.setSo_luong(soLuong);
                    chiTietSanPham.setTrang_thai("Hoạt động");
                    chiTietSanPham.setNgay_tao(new Date());
                    chiTietSanPham.setNgay_sua(new Date());
                    chiTietSanPham.setMauSac(mauSac);
                    chiTietSanPham.setKichThuoc(kichThuoc);

                    mapChiTietSanPham.put(key, chiTietSanPham);
                    list.add(chiTietSanPham);
                }
            }
        }
        return list;
    }


}

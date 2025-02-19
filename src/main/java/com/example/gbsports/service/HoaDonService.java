//package com.example.gbsports.service;
//
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.*;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//
//@Service
//public class HoaDonService {
//    public byte[] generateHoaDonPDF(Integer idHoaDon) {
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            Document document = new Document();
//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            // Tiêu đề
//            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
//            Paragraph title = new Paragraph("HÓA ĐƠN #" + idHoaDon, titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            document.add(title);
//            document.add(new Paragraph("\n"));
//
//            // Nội dung hóa đơn (ví dụ)
//            document.add(new Paragraph("Khách hàng: Nguyễn Văn A"));
//            document.add(new Paragraph("Ngày lập: 2024-02-19"));
//            document.add(new Paragraph("Tổng tiền: 1.000.000 VND"));
//
//            document.close();
//            return out.toByteArray();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
//

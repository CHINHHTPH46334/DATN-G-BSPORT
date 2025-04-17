package com.example.gbsports.service;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.repository.ChatLieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatLieuService {
    @Autowired
    ChatLieuRepo chatLieuRepo;

    public List<ChatLieu> listFindAllChatLieu() {
        return chatLieuRepo.findAll();
    }

    public ChatLieu getChatLieuOrCreateChatLieu(String tenChatLieu) {
        System.out.println(tenChatLieu);
        Optional<ChatLieu> existingChatLieu = chatLieuRepo.findAll().stream()
                .filter(chatLieu -> tenChatLieu.equalsIgnoreCase(Optional.ofNullable(chatLieu.getTen_chat_lieu()).orElse("")))
                .findFirst();

        if (existingChatLieu.isPresent()) {
            return existingChatLieu.get();
        }

        // Nếu không tìm thấy, tạo mã mới
        int maxNumber = chatLieuRepo.findAll().stream()
                .map(ChatLieu::getMa_chat_lieu)
                .filter(ma -> ma.startsWith("CL0"))
                .map(ma -> ma.substring(3))
                .filter(num -> num.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        // Tạo đối tượng mới
        ChatLieu newChatLieu = new ChatLieu();
        newChatLieu.setMa_chat_lieu("CL0" + (maxNumber + 1));
        newChatLieu.setTen_chat_lieu(tenChatLieu);
        newChatLieu.setTrang_thai("Hoạt động");
        newChatLieu.setNgay_tao(LocalDateTime.now());
        newChatLieu.setNgay_sua(LocalDateTime.now());
        chatLieuRepo.save(newChatLieu);
        return newChatLieu;
    }

    public ResponseEntity<?> changeTrangThaiChatLieu(@RequestParam("idChatLieu") Integer idChatLieu) {
        if (idChatLieu != null) {
            ChatLieu chatLieu = chatLieuRepo.findById(idChatLieu).get();
            if (chatLieu.getTrang_thai().equals("Hoạt động")) {
                chatLieu.setTrang_thai("Không hoạt động");
            } else {
                chatLieu.setTrang_thai("Hoạt động");
            }
            chatLieu.setNgay_sua(LocalDateTime.now());
            chatLieuRepo.save(chatLieu);
            return ResponseEntity.ok(chatLieu);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("messege", "Lỗi null");
            return ResponseEntity.badRequest().body(error);
        }
    }

    public ResponseEntity<?> updateChatLieu(@RequestBody ChatLieu chatLieu){
        if (chatLieu.getId_chat_lieu() != null){
            ChatLieu chatLieuSua = chatLieuRepo.findById(chatLieu.getId_chat_lieu()).get();
            chatLieuSua.setTen_chat_lieu(chatLieu.getTen_chat_lieu());
            chatLieuSua.setNgay_sua(LocalDateTime.now());
            chatLieuRepo.save(chatLieuSua);
            return ResponseEntity.ok(chatLieuSua);
        }else {
            Map<String, String> error = new HashMap<>();
            error.put("messege","Lỗi id null");
            return ResponseEntity.badRequest().body(error);
        }
    }
}

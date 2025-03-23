package com.example.gbsports.service;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.repository.ChatLieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        newChatLieu.setNgay_tao(new Date());
        newChatLieu.setNgay_sua(new Date());
        chatLieuRepo.save(newChatLieu);
        return newChatLieu;
    }
}

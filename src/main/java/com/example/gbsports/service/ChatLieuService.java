package com.example.gbsports.service;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.repository.ChatLieuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatLieuService {
    @Autowired
    ChatLieuRepo chatLieuRepo;

    public List<ChatLieu> listFindAllChatLieu() {
        return chatLieuRepo.findAll();
    }
}

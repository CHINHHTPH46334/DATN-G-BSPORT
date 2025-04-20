package com.example.gbsports.controller;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.repository.ChatLieuRepo;
import com.example.gbsports.service.ChatLieuService;
import com.example.gbsports.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class ChatLieuController {
    @Autowired
    ChatLieuService chatLieuService;
    @GetMapping("/ChatLieu")
    public List<ChatLieu> getAllChatLieu(){
        return chatLieuService.listFindAllChatLieu();
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PostMapping("/addChatLieu")
    public ResponseEntity<?> addChatLieu(@RequestParam("tenChatLieu") String tenChatLieu){
        return ResponseEntity.ok(chatLieuService.getChatLieuOrCreateChatLieu(tenChatLieu));
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeTrangThaiChatLieu")
    public ResponseEntity<?> changeTrangThaiChatLieu(@RequestParam("idChatLieu") Integer idChatLieu){
        return chatLieuService.changeTrangThaiChatLieu(idChatLieu);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/updateChatLieu")
    public ResponseEntity<?> updateChatLieu(@RequestBody ChatLieu chatLieu){
        return chatLieuService.updateChatLieu(chatLieu);
    }
}

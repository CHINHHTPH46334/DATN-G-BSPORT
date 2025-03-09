package com.example.gbsports.controller;

import com.example.gbsports.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {
    @Autowired
    MailService mailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam(name = "to") String to,
                            @RequestParam(name = "subject") String subject,
                            @RequestParam(name = "body") String body) {
        mailService.sendEmail(to, subject, body);
        System.out.println("đến:"+to +"\n"+"Tiêu đề:"+subject+"\n"+"Nội dung:"+body);
        return "Email đã gửi thành công!";
    }
}

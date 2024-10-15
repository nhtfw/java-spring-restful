package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    public String sendSimpleEmail() {

        this.emailService.sendSimpleEmail();

        return "ok";
    }
}

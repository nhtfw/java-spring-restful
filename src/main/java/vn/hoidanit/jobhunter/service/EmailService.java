package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private MailSender mailSender;

    public void sendSimpleEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        // email nguời nhận
        msg.setTo("odinkun20303@gmail.com");
        // đầu đề mail
        msg.setSubject("Test subject");
        // nội dung mail
        msg.setText("Hello world");

        this.mailSender.send(msg);
    }
}

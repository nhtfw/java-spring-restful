package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.repository.JobRepository;

import java.nio.charset.StandardCharsets;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private MailSender mailSender;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    private JobRepository jobRepository;

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

    // gửi email đồng bộ (Synchronize)
    public void sendEmailSync(String to, String subject, String content, boolean isMultipart,
            boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();

        // isMultipart: gửi kèm hình ảnh, file,... hay không
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);

            // nội dung mail: nếu isHtml == true thì nội dung sẽ là dưới dạng html
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    /*
     * Điều này có nghĩa là khi một phương thức được đánh dấu bằng @Async được gọi,
     * nó sẽ được thực thi trong một luồng (thread) riêng biệt, không chặn luồng
     * chính. Điều này giúp cải thiện hiệu suất ứng dụng, đặc biệt trong các tác vụ
     * tốn thời gian như gọi dịch vụ bên ngoài, xử lý tệp lớn, hoặc các tác vụ nền.
     */
    @Async
    public void sendEmailFromTemplateSync(
            String to,
            String subject,
            String templateName,
            String username,
            // không quy định nhất thiết phải là 1 kiểu dữ liệu
            Object value) {

        Context context = new Context();

        // tạo biến
        context.setVariable("name", username);
        context.setVariable("jobs", value);

        // TemplateEngine convert từ file html -> text
        // đầu vào là file html. đầu ra là string
        String content = this.springTemplateEngine.process(templateName, context);

        // sau đó gửi mail bằng hàm ở trên
        this.sendEmailSync(to, subject, content, false, true);
    }

}

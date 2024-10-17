package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SubscriberService subscriberService;

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    public String sendSimpleEmail() {

        // this.emailService.sendSimpleEmail();

        // this.emailService.sendEmailSync("odinkun20303@gmail.com", "Test send email",
        // "<h1> <b> Hello </b> </h1>", false, true);

        // this.emailService.sendEmailFromTemplateSync("odinkun20303@gmail.com", "Test
        // send email", "job");

        // "job" -> file job.html trong folder resources/templates
        /*
         * Vì sao dự án có thể hiểu job là job.html và phải tìm job.html trong folder
         * templates ? => Vì đó là cấu hình mặc định của java spring với thymeleaf, nếu
         * khai báo 1 file ở ngoài templates thì sẽ không chạy được vì không tìm thấy
         * bên trong folder templates
         */
        this.subscriberService.sendSubscribersEmailJobs();

        /*- Code HTML và style CSS trong cùng 1 file (không code riêng lẻ HTML và css) => sử
        dụng css với tag <style> </style>
        - Convert HTML/CSS => inline CSS
        - Mặc định Gmail sẽ bỏ qua CSS ở phần header => để đảm bảo an toàn hơn cho người
        dùng (tránh nhúng link javascript ở header)
        - Nên sử dụng layout table (không dùng css flex) */

        return "ok";
    }
}

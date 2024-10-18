package vn.hoidanit.jobhunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//disable security
// @SpringBootApplication(exclude = {
// 		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
// 		org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
// })

@SpringBootApplication

// Kích hoạt hỗ trợ Async
/*
 * Điều này có nghĩa là khi một phương thức được đánh dấu bằng @Async được gọi,
 * nó sẽ được thực thi trong một luồng (thread) riêng biệt, không chặn luồng
 * chính. Điều này giúp cải thiện hiệu suất ứng dụng, đặc biệt trong các tác vụ
 * tốn thời gian như gọi dịch vụ bên ngoài, xử lý tệp lớn, hoặc các tác vụ nền.
 */
@EnableAsync

/*
 * được sử dụng để kích hoạt chức năng lập lịch (scheduling) cho các tác vụ chạy
 * theo lịch trình định sẵn. Khi bạn đánh dấu một lớp cấu hình
 * bằng @EnableScheduling, Spring sẽ quét và tìm các phương thức được chú thích
 * bằng @Scheduled để thực hiện theo thời gian đã chỉ định.
 */
@EnableScheduling
public class JobhunterApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobhunterApplication.class, args);
	}

}

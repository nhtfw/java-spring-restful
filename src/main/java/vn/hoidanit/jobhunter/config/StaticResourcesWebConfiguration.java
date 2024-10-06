package vn.hoidanit.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    // biến môi trường chứa base path tới thư mục lưu
    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * mỗi lần truy cập tới http : /storage/ thì chương trình sẽ tự động tìm dữ liệu
         * bên trong đường link basePAth
         */
        // ví dụ khi ta truy cập http /storage/anh.img thì chương trình sẽ tìm file
        // anh.img bên trong đường dẫn basePath
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(baseURI);
    }
}

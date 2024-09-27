package vn.hoidanit.jobhunter.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// annotation hoạt động khi chương trình chạy
@Retention(RetentionPolicy.RUNTIME)
// phạm vi của annotation
@Target(ElementType.METHOD)
public @interface ApiMessage {
    // giá trị cần truyền khi sử dụng anno
    String value();
}

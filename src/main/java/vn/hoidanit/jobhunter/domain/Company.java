package vn.hoidanit.jobhunter.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Table(name = "companies")
@Entity
// sử dụng lombok thay thế cho getter và setter
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

    /*
     * Trong Java, các lớp Instant thuộc gói java.time được sử dụng để đại diện cho
     * một điểm thời gian chính xác (tính bằng giây và nanosecond từ EPOCH -
     * 00:00:00 UTC ngày 1 tháng 1 năm 1970).
     */
    // format sang múi giờ +7 cho frontend, backend vẫn lưu +0
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    /*
     * fetch = FetchType.LAZY: Dữ liệu của các đối tượng liên quan (danh sách
     * User) sẽ chỉ được nạp khi cần thiết, giúp tiết kiệm tài nguyên.
     */
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    // phía front end khi trả về response sẽ không hiện field này, và khi request
    // cũng không nhận dữ liệu phía frontend gửi lên
    // hiểu đơn giản là không convert từ JSON -> java và ngược lại
    @JsonIgnore
    List<User> users;

    // hàm này chạy trước khi tạo đối tượng (persist = write)
    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        // gán thời gian hiện tại
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }
}

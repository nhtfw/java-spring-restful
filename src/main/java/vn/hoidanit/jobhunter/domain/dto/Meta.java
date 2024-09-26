package vn.hoidanit.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    // số trang hiện tại
    private int page;

    // số phần tử mỗi trang
    private int pageSize;

    // tổng số trang
    private int pages;

    // tổng số phần tử
    private long total;
}

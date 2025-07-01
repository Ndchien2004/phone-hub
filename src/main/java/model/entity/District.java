package model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class District {

    /**
     * Mã quận/huyện, ví dụ: "QBD", "Q1".
     * Tương ứng với cột district_code trong database.
     */
    private String districtCode;

    /**
     * Tên đầy đủ của quận/huyện, ví dụ: "Quận Ba Đình".
     * Tương ứng với cột name trong database.
     */
    private String name;

    /**
     * Mã của tỉnh/thành phố mà quận/huyện này thuộc về.
     * Đây là khóa ngoại trỏ đến bảng provinces.
     * Tương ứng với cột province_code trong database.
     */
    private String provinceCode;

}
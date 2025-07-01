package model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ward {

    /**
     * Mã phường/xã, ví dụ: "PCD", "PBT".
     * Tương ứng với cột ward_code trong database.
     */
    private String wardCode;

    /**
     * Tên đầy đủ của phường/xã, ví dụ: "Phường Cống Vị".
     * Tương ứng với cột name trong database.
     */
    private String name;

    /**
     * Mã của quận/huyện mà phường/xã này thuộc về.
     * Đây là khóa ngoại trỏ đến bảng districts.
     * Tương ứng với cột district_code trong database.
     */
    private String districtCode;

}
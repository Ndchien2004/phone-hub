package model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Province {

    /**
     * Mã tỉnh/thành phố, ví dụ: "HN", "HCM".
     * Tương ứng với cột province_code trong database.
     */
    private String provinceCode;

    /**
     * Tên đầy đủ của tỉnh/thành phố, ví dụ: "Thành phố Hà Nội".
     * Tương ứng với cột name trong database.
     */
    private String name;

}
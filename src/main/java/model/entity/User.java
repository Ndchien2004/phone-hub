package model.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Integer userId;
    private Integer roleId; // Lưu ID trực tiếp
    private String fullName;
    private String password;
    private String image;
    private Boolean gender;
    private String email;
    private String phoneNumber;
    private String googleId;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordExpiry;
}
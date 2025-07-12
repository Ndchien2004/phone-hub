package model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Setting {
    private Integer settingId;
    private String settingType;
    private String name;
    private String description;
    private boolean isDeleted = false;
}
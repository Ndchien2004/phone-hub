package model.dao;

import model.entity.Setting;
import java.util.List;
import java.util.Optional;

public interface SettingDAO {
    List<Setting> findByType(String settingType);
    Optional<Setting> findById(int id);
    List<Setting> findAll();
}

package model.dao.impl;

import model.dao.DBContext;
import model.dao.SettingDAO;
import model.entity.Setting;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingDAOImpl implements SettingDAO {
    
    @Override
    public List<Setting> findByType(String settingType) {
        String sql = "SELECT setting_id, setting_type, name, description, is_deleted FROM settings WHERE setting_type = ? AND is_deleted = 0 ORDER BY name";
        List<Setting> settings = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, settingType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    settings.add(mapResultSetToSetting(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }
    
    @Override
    public Optional<Setting> findById(int id) {
        String sql = "SELECT setting_id, setting_type, name, description, is_deleted FROM settings WHERE setting_id = ? AND is_deleted = 0";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSetting(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Setting> findAll() {
        String sql = "SELECT setting_id, setting_type, name, description, is_deleted FROM settings WHERE is_deleted = 0 ORDER BY setting_type, name";
        List<Setting> settings = new ArrayList<>();
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                settings.add(mapResultSetToSetting(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return settings;
    }
    
    private Setting mapResultSetToSetting(ResultSet rs) throws SQLException {
        Setting setting = new Setting();
        setting.setSettingId(rs.getInt("setting_id"));
        setting.setSettingType(rs.getString("setting_type"));
        setting.setName(rs.getString("name"));
        setting.setDescription(rs.getString("description"));
        setting.setDeleted(rs.getBoolean("is_deleted"));
        return setting;
    }
}

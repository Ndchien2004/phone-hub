package model.dao.user;

import model.entity.Province;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for accessing Province data from the database.
 */
public class ProvinceDAO extends DBContext1 {

    private static final Logger logger = LogManager.getLogger(ProvinceDAO.class);

    /**
     * Get all provinces from the database.
     */
    public List<Province> getAllProvinces() {
        List<Province> list = new ArrayList<>();
        String sql = "SELECT * FROM Province";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Province p = new Province();
                p.setProvinceCode(rs.getString("provinceCode"));
                p.setName(rs.getString("name"));
                list.add(p);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving all provinces", e);
        }

        return list;
    }

    /**
     * Get a single province by provinceCode.
     */
    public Province getProvinceByCode(String code) {
        String sql = "SELECT * FROM Province WHERE provinceCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Province p = new Province();
                p.setProvinceCode(rs.getString("provinceCode"));
                p.setName(rs.getString("name"));
                return p;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving province by code: " + code, e);
        }

        return null;
    }

    /**
     * Insert a Province into the database.
     */
    public void insert(Province province) {
        String sql = "INSERT INTO Province (provinceCode, name) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, province.getProvinceCode());
            ps.setNString(2, province.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting province: " + province.getProvinceCode(), e);
        }
    }

    /**
     * Check if a province exists in the database.
     */
    public boolean exists(String provinceCode) {
        String sql = "SELECT 1 FROM Province WHERE provinceCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, provinceCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // returns true if exists
            }
        } catch (SQLException e) {
            logger.error("Error checking province existence: " + provinceCode, e);
            return false;
        }
    }

    /**
     * Insert province only if it does not exist.
     */
    public void insertIfNotExists(Province province) {
        if (!exists(province.getProvinceCode())) {
            insert(province);
        } else {
            logger.info("Province {} already exists. Skipped insert.", province.getProvinceCode());
        }
    }

    /**
     * Main method to test fetching all provinces.
     */
    public static void main(String[] args) {
        ProvinceDAO dao = new ProvinceDAO();
        List<Province> provinces = dao.getAllProvinces();
        for (Province p : provinces) {
            System.out.println(p.getProvinceCode() + " - " + p.getName());
        }
    }
}


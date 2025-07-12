package model.dao.user;

import model.entity.District;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for accessing District data from the database.
 */
public class DistrictDAO extends DBContext1 {

    private static final Logger logger = LogManager.getLogger(DistrictDAO.class);

    /**
     * Get all districts from the database.
     */
    public List<District> getAllDistricts() {
        List<District> list = new ArrayList<>();
        String sql = "SELECT * FROM District";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDistrict(rs));
            }

        } catch (SQLException e) {
            logger.error("Error retrieving all districts", e);
        }

        return list;
    }

    /**
     * Get districts by province code.
     */
    public List<District> getDistrictsByProvinceCode(String provinceCode) {
        List<District> list = new ArrayList<>();
        String sql = "SELECT * FROM District WHERE provinceCode = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, provinceCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToDistrict(rs));
            }

        } catch (SQLException e) {
            logger.error("Error retrieving districts by provinceCode: " + provinceCode, e);
        }

        return list;
    }

    /**
     * Get a district by its code.
     */
    public District getDistrictByCode(String districtCode) {
        String sql = "SELECT * FROM District WHERE districtCode = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, districtCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToDistrict(rs);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving district by code: " + districtCode, e);
        }

        return null;
    }

    /**
     * Map a ResultSet to District entity.
     */
    private District mapResultSetToDistrict(ResultSet rs) throws SQLException {
        District district = new District();
        district.setDistrictCode(rs.getString("districtCode"));
        district.setName(rs.getNString("name"));
        district.setProvinceCode(rs.getString("provinceCode"));
        return district;
    }

    /**
     * Insert a District into the database.
     */
    public void insert(District district) {
        String sql = "INSERT INTO District (districtCode, name, provinceCode) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, district.getDistrictCode());         // Set district code
            ps.setNString(2, district.getName());                // Set district name
            ps.setString(3, district.getProvinceCode());         // Set province foreign key
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting district: " + district.getDistrictCode(), e);
        }
    }

    public boolean exists(String districtCode) {
        String sql = "SELECT 1 FROM District WHERE districtCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, districtCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking district existence: " + districtCode, e);
            return false;
        }
    }

    public void insertIfNotExists(District district) {
        if (!exists(district.getDistrictCode())) {
            insert(district);
        } else {
            logger.info("District {} already exists. Skipped insert.", district.getDistrictCode());
        }
    }

    /**
     * Main method to test getAllDistricts().
     */
    public static void main(String[] args) {
        DistrictDAO dao = new DistrictDAO();
        List<District> list = dao.getAllDistricts();
        for (District d : list) {
            System.out.println(d.getDistrictCode() + " - " + d.getName());
        }
    }
}

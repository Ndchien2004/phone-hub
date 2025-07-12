package model.dao.user;

import model.entity.Ward;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for accessing Ward data from the database.
 */
public class WardDAO extends DBContext1 {

    private static final Logger logger = LogManager.getLogger(WardDAO.class);

    /**
     * Get all wards from the database.
     */
    public List<Ward> getAllWards() {
        List<Ward> list = new ArrayList<>();
        String sql = "SELECT * FROM Ward";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToWard(rs));
            }

        } catch (SQLException e) {
            logger.error("Error retrieving all wards", e);
        }

        return list;
    }

    /**
     * Get wards by district code.
     */
    public List<Ward> getWardsByDistrictCode(String districtCode) {
        List<Ward> list = new ArrayList<>();
        String sql = "SELECT * FROM Ward WHERE districtCode = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, districtCode);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToWard(rs));
            }

        } catch (SQLException e) {
            logger.error("Error retrieving wards by districtCode: " + districtCode, e);
        }

        return list;
    }

    /**
     * Get a ward by its code.
     */
    public Ward getWardByCode(String wardCode) {
        String sql = "SELECT * FROM Ward WHERE wardCode = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, wardCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToWard(rs);
            }

        } catch (SQLException e) {
            logger.error("Error retrieving ward by code: " + wardCode, e);
        }

        return null;
    }

    /**
     * Map ResultSet to Ward object.
     */
    private Ward mapResultSetToWard(ResultSet rs) throws SQLException {
        Ward ward = new Ward();
        ward.setWardCode(rs.getString("wardCode"));
        ward.setName(rs.getNString("name"));
        ward.setDistrictCode(rs.getString("districtCode"));
        return ward;
    }

    /**
     * Insert a Ward into the database.
     */
    public void insert(Ward ward) {
        String sql = "INSERT INTO Ward (wardCode, name, districtCode) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ward.getWardCode());                 // Set ward code
            ps.setNString(2, ward.getName());                    // Set ward name
            ps.setString(3, ward.getDistrictCode());             // Set district foreign key
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error inserting ward: " + ward.getWardCode(), e);
        }
    }

    public boolean exists(String wardCode) {
        String sql = "SELECT 1 FROM Ward WHERE wardCode = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, wardCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking ward existence: " + wardCode, e);
            return false;
        }
    }

    public void insertIfNotExists(Ward ward) {
        if (!exists(ward.getWardCode())) {
            insert(ward);
        } else {
            logger.info("Ward {} already exists. Skipped insert.", ward.getWardCode());
        }
    }

    /**
     * Main method to test DAO.
     */
    public static void main(String[] args) {
        WardDAO dao = new WardDAO();
        List<Ward> wards = dao.getAllWards();
        for (Ward w : wards) {
            System.out.println(w.getWardCode() + " - " + w.getName());
        }
    }
}

package model.dao.impl;

import model.dao.DBContext;
import model.dao.LocationDAO;
import model.entity.District;
import model.entity.Province;
import model.entity.Ward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp này triển khai các phương thức truy vấn cơ sở dữ liệu
 * cho các đơn vị hành chính: Tỉnh, Huyện, Xã.
 */
public class LocationDAOImpl implements LocationDAO {

    /**
     * Lấy tất cả các tỉnh/thành phố từ cơ sở dữ liệu.
     * @return một danh sách các đối tượng Province.
     */
    @Override
    public List<Province> findAllProvinces() {
        List<Province> provinces = new ArrayList<>();
        String sql = "SELECT province_code, name FROM provinces ORDER BY name ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Province province = new Province();
                province.setProvinceCode(rs.getString("province_code"));
                province.setName(rs.getString("name"));
                provinces.add(province);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn danh sách tỉnh/thành phố: " + e.getMessage());
            e.printStackTrace();
        }
        return provinces;
    }

    /**
     * Lấy tất cả các quận/huyện từ cơ sở dữ liệu.
     * @return một danh sách các đối tượng District.
     */
    @Override
    public List<District> findAllDistricts() {
        List<District> districts = new ArrayList<>();
        String sql = "SELECT district_code, name, province_code FROM districts ORDER BY name ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                District district = new District();
                district.setDistrictCode(rs.getString("district_code"));
                district.setName(rs.getString("name"));
                district.setProvinceCode(rs.getString("province_code"));
                districts.add(district);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn danh sách quận/huyện: " + e.getMessage());
            e.printStackTrace();
        }
        return districts;
    }

    /**
     * Lấy tất cả các phường/xã từ cơ sở dữ liệu.
     * @return một danh sách các đối tượng Ward.
     */
    @Override
    public List<Ward> findAllWards() {
        List<Ward> wards = new ArrayList<>();
        // Câu lệnh SQL lấy tất cả các phường/xã và sắp xếp theo tên
        String sql = "SELECT ward_code, name, district_code FROM wards ORDER BY name ASC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ward ward = new Ward();
                ward.setWardCode(rs.getString("ward_code"));
                ward.setName(rs.getString("name"));
                ward.setDistrictCode(rs.getString("district_code"));
                wards.add(ward);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn danh sách phường/xã: " + e.getMessage());
            e.printStackTrace();
        }
        return wards;
    }
}
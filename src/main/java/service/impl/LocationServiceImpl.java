package service.impl;

import model.dao.LocationDAO;
import model.dao.impl.LocationDAOImpl;
import model.entity.District;
import model.entity.Province;
import model.entity.Ward;
import service.LocationService;
import java.util.List;

public class LocationServiceImpl implements LocationService {

    // Tạo một instance của DAO để sử dụng
    private final LocationDAO locationDAO = new LocationDAOImpl();

    @Override
    public List<Province> getAllProvinces() {
        // Gọi phương thức tương ứng của DAO
        System.out.println("LocationService: Lấy danh sách Tỉnh/Thành phố...");
        return locationDAO.findAllProvinces();
    }

    @Override
    public List<District> getAllDistricts() {
        // Gọi phương thức tương ứng của DAO
        System.out.println("LocationService: Lấy danh sách Quận/Huyện...");
        return locationDAO.findAllDistricts();
    }

    @Override
    public List<Ward> getAllWards() {
        // Gọi phương thức tương ứng của DAO
        System.out.println("LocationService: Lấy danh sách Phường/Xã...");
        return locationDAO.findAllWards();
    }
}
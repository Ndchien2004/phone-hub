package service.user;

import model.dao.user.DistrictDAO;
import model.dao.user.ProvinceDAO;
import model.dao.user.WardDAO;
import model.entity.District;
import model.entity.Province;
import model.entity.Ward;

import java.util.ArrayList;
import java.util.List;


public class LocationService {
    private final ProvinceDAO provinceDAO = new ProvinceDAO();
    private final DistrictDAO districtDAO = new DistrictDAO();
    private final WardDAO wardDAO = new WardDAO();

    public List<Province> getAllProvinces() {
        return provinceDAO.getAllProvinces();
    }

    public List<District> getDistrictsByProvince(String provinceCode) {
        if (provinceCode == null) return new ArrayList<>();
        return districtDAO.getDistrictsByProvinceCode(provinceCode);
    }

    public List<Ward> getWardsByDistrict(String districtCode) {
        if (districtCode == null) return new ArrayList<>();
        return wardDAO.getWardsByDistrictCode(districtCode);
    }


}

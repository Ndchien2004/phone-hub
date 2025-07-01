package service;
import model.entity.District;
import model.entity.Province;
import model.entity.Ward;
import java.util.List;

public interface LocationService {
    List<Province> getAllProvinces();
    List<District> getAllDistricts();
    List<Ward> getAllWards();
}
package model.dao;

import model.entity.District;
import model.entity.Province;
import model.entity.Ward;
import java.util.List;

public interface LocationDAO {
    List<Province> findAllProvinces();
    List<District> findAllDistricts();
    List<Ward> findAllWards();
}
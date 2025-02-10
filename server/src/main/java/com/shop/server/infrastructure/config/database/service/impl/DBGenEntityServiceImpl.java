package com.shop.server.infrastructure.config.database.service.impl;

import com.shop.server.entities.main.District;
import com.shop.server.entities.main.Province;
import com.shop.server.entities.main.Ward;
import com.shop.server.infrastructure.config.database.model.response.DBGenDistrictResponse;
import com.shop.server.infrastructure.config.database.model.response.DBGenProvinceResponse;
import com.shop.server.infrastructure.config.database.model.response.DBGenWardResponse;
import com.shop.server.infrastructure.config.database.repository.DBGenDistrictRepository;
import com.shop.server.infrastructure.config.database.repository.DBGenProvinceRepository;
import com.shop.server.infrastructure.config.database.repository.DBGenWardRepository;
import com.shop.server.infrastructure.config.database.service.DBGenEntityService;
import com.shop.server.infrastructure.config.database.service.DBGenWebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DBGenEntityServiceImpl implements DBGenEntityService {

    private final DBGenWebClientService dbGenWebClientService;

    private final DBGenProvinceRepository provinceRepository;

    private final DBGenDistrictRepository districtRepository;

    private final DBGenWardRepository wardRepository;

    public DBGenEntityServiceImpl(
            DBGenProvinceRepository provinceRepository,
            DBGenDistrictRepository districtRepository,
            DBGenWardRepository wardRepository, DBGenWebClientService webClientService) {
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.wardRepository = wardRepository;
        this.dbGenWebClientService = webClientService;
    }

    @Override
    public void synchronizationProvince() {
        if (provinceRepository.count() == 0) {
            List<DBGenProvinceResponse> responses = dbGenWebClientService.callApiGetProvince();
            List<Province> provinces = new ArrayList<>();
            responses.forEach(dto -> {
                Province province = new Province();
                province.setId(dto.getProvinceID());
                province.setName(dto.getProvinceName());
                province.setCode(dto.getCode());
                province.setIsEnabled(dto.getIsEnable());
                provinces.add(province);
                this.synchronizationDistrict(dto.getProvinceID());
            });
            provinceRepository.saveAll(provinces);
        } else {
            log.info("Dữ liệu province vẫn tồn tại không cần update thêm");
        }
    }

    @Override
    public void synchronizationDistrict(Long id) {
        List<DBGenDistrictResponse> responses = dbGenWebClientService.callApiGetDistrict(id);
        List<District> districts = new ArrayList<>();
        responses.forEach(dto -> {
            District district = new District();
            district.setId(dto.getDistrictID());
            district.setName(dto.getDistrictName());
            district.setProvinceId(dto.getProvinceID());
            districts.add(district);
            this.synchronizationWard(dto.getDistrictID());
        });
        districtRepository.saveAll(districts);
    }

    @Override
    public void synchronizationWard(Long id) {
        List<DBGenWardResponse> responses = dbGenWebClientService.callApiGetWard(id);
        if (responses == null) {
            return;
        }
        List<Ward> wards = new ArrayList<>();
        responses.forEach(dto -> {
            Ward ward = new Ward();
            ward.setName(dto.getWardName());
            ward.setCode(dto.getCode());
            ward.setDistrictId(dto.getDistrictID());
            wards.add(ward);
        });

        wardRepository.saveAll(wards);
    }

}

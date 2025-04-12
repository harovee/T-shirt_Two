package com.shop.server.core.admin.client.services.impl;

import com.shop.server.core.admin.client.models.requests.AdminClientAddressMoRequest;
import com.shop.server.core.admin.client.models.requests.AdminClientAddressRequest;
import com.shop.server.core.admin.client.models.requests.AdminClientRequest;
import com.shop.server.core.admin.client.models.requests.AdminFindClientRequest;
import com.shop.server.core.admin.client.repositories.AdminClientAddressRepository;
import com.shop.server.core.admin.client.repositories.AdminClientDistrictRepository;
import com.shop.server.core.admin.client.repositories.AdminClientProvinceRepository;
import com.shop.server.core.admin.client.repositories.AdminClientRepository;
import com.shop.server.core.admin.client.repositories.AdminClientWardRepository;
import com.shop.server.core.admin.client.services.AdminClientMailService;
import com.shop.server.core.admin.client.services.AdminClientService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.Address;
import com.shop.server.entities.main.District;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.Province;
import com.shop.server.entities.main.Ward;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.AESPasswordCryptoUtil;
import com.shop.server.utils.DateTimeUtil;
import com.shop.server.utils.DefaultImageUtil;
import com.shop.server.utils.Helper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AdminClientServiceImpl implements AdminClientService {

    private final AdminClientRepository adminClientRepository;

    private final AdminClientAddressRepository adminClientAddressRepository;

    private final AdminClientProvinceRepository adminClientProvinceRepository;

    private final AdminClientDistrictRepository adminClientDistrictRepository;

    private final AdminClientWardRepository adminClientWardRepository;

    private final AdminClientMailService emailService;

    public AdminClientServiceImpl(
            AdminClientRepository adminClientRepository,
            AdminClientMailService emailService,
            AdminClientAddressRepository adminClientAddressRepository,
            AdminClientProvinceRepository adminClientProvinceRepository,
            AdminClientDistrictRepository adminClientDistrictRepository,
            AdminClientWardRepository adminClientWardRepository
    ) {
        this.adminClientRepository = adminClientRepository;
        this.emailService = emailService;
        this.adminClientAddressRepository = adminClientAddressRepository;
        this.adminClientProvinceRepository = adminClientProvinceRepository;
        this.adminClientDistrictRepository = adminClientDistrictRepository;
        this.adminClientWardRepository = adminClientWardRepository;
    }

    @Override
    public ResponseObject<?> getClients(AdminFindClientRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminClientRepository.getClientByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getClientById(String id) {
        return new ResponseObject<>(
                adminClientRepository.getClientDetail(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createClient(AdminClientRequest request) {
        if (adminClientRepository.existsClientByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        if (adminClientRepository.existsClientByPhoneNumber(request.getPhoneNumber())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", số điện thoại"
            );
        }
        KhachHang client = new KhachHang();
        String pass = AESPasswordCryptoUtil.genPassword(8L);
        client.setPassword(pass);
        client.setFullName(request.getName());
        client.setEmail(request.getEmail());
        Long count = adminClientRepository.count() + 1;
        String formattedCode = String.format("%09d", count);
        client.setCode(formattedCode);
        client.setPhoneNumber(request.getPhoneNumber());
        client.setProfilePicture(DefaultImageUtil.IMAGE);
        client.setDeleted(false);
        KhachHang newClient = adminClientRepository.save(client);
        emailService.sendMailCreateClient(newClient);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createClientMo(AdminClientAddressMoRequest request) {
        if (adminClientRepository.existsClientByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        if (adminClientRepository.existsClientByPhoneNumber(request.getPhoneNumber())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", số điện thoại"
            );
        }
        Optional<Province> provinceOptional = adminClientProvinceRepository.findById(request.getProvince());
        if (provinceOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", tỉnh"
            );
        }
        Optional<District> districtOptional = adminClientDistrictRepository.findById(request.getDistrict());
        if (districtOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", thành phố/quận/huyện"
            );
        }
        Optional<Ward> wardOptional = adminClientWardRepository.findById(request.getWard());
        if (wardOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", phường/xã"
            );
        }
        KhachHang client = new KhachHang();
        String pass = AESPasswordCryptoUtil.genPassword(8L);
        client.setPassword(pass);
        client.setFullName(request.getName());
        client.setEmail(request.getEmail());
        Long count = adminClientRepository.count() + 1;
        String formattedCode = String.format("%09d", count);
        client.setCode(formattedCode);
        client.setPhoneNumber(request.getPhoneNumber());
        client.setProfilePicture(request.getPicture());
        client.setDeleted(false);
        client.setBirthday(DateTimeUtil.convertStringToTimeStampSecond(request.getBirthday()));
        client.setGender(request.getGender());
        KhachHang newClient = adminClientRepository.save(client);
        emailService.sendMailCreateClient(newClient);
        Address address = new Address();
        address.setClientId(newClient);
        address.setPhoneNumber(request.getPhoneNumber());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setLine(request.getLine());
        address.setName(request.getName());
        address.setDeleted(false);
        address.setIsDefault(false);
        address.setIsDefault(true);
        adminClientAddressRepository.save(address);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateClient(String id, AdminClientRequest request) {
        Optional<KhachHang> clientOptional = adminClientRepository.findById(id);
        if (clientOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", khách hàng"
            );
        }
        if (adminClientRepository.existsClientByEmailAndIdNotEquals(request.getEmail(), id) == 1) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        if (adminClientRepository.existsClientByPhoneNumberAndIdNotEquals(request.getPhoneNumber(), id) == 1) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", số điện thoại"
            );
        }
        try {
            KhachHang client = clientOptional.get();
            client.setPassword(request.getPassword());
            client.setFullName(request.getName());
            client.setEmail(request.getEmail());
            client.setBirthday(DateTimeUtil.convertStringToTimeStampSecond(request.getBirthday()));
            client.setGender(request.getGender());
            client.setPhoneNumber(request.getPhoneNumber());
            adminClientRepository.save(client);
            emailService.sendMailCreateClient(client);
        } catch (Exception e) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> changeStatusClient(String id) {
        Optional<KhachHang> khachHangOptional = adminClientRepository.findById(id);
        if (khachHangOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", khách hàng"
            );
        }
        KhachHang khachHang = khachHangOptional.get();
        khachHang.setDeleted(!khachHang.getDeleted());
        adminClientRepository.save(khachHang);
        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateClientAvatar(String id, AdminClientRequest request) {
        Optional<KhachHang> clientOptional = adminClientRepository.findById(id);
        if (clientOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", nhân viên"
            );
        }
        KhachHang client = clientOptional.get();
        client.setProfilePicture(request.getPicture());
        adminClientRepository.save(client);
        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getProvinces() {
        return new ResponseObject<>(
                adminClientRepository.getProvinces(),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getDistrictsByProvinceId(Long provinceId) {
        return new ResponseObject<>(
                adminClientRepository.getDistrictsByProvinceId(provinceId),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getWardsByDistrictId(Long districtId) {
        return new ResponseObject<>(
                adminClientRepository.getWardsByDistrictsId(districtId),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createClientAddress(AdminClientAddressRequest request) {
        Optional<KhachHang> clientOptional = adminClientRepository.findById(request.getClientId());
        if (clientOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", nhân viên"
            );
        }
        Optional<Province> provinceOptional = adminClientProvinceRepository.findById(request.getProvince());
        if (provinceOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", tỉnh"
            );
        }
        Optional<District> districtOptional = adminClientDistrictRepository.findById(request.getDistrict());
        if (districtOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", thành phố/quận/huyện"
            );
        }
        Optional<Ward> wardOptional = adminClientWardRepository.findById(request.getWard());
        if (wardOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", phường/xã"
            );
        }
        Address address = new Address();
        address.setClientId(clientOptional.get());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setLine(request.getLine());
        address.setName(request.getName());
        address.setDeleted(false);
        address.setIsDefault(false);
        if (adminClientAddressRepository.existsAddressByClientId(request.getClientId()) == 0) {
            address.setIsDefault(true);
        }
        adminClientAddressRepository.save(address);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateClientAddress(String id, AdminClientAddressRequest request) {
        Optional<Address> addressOptional = adminClientAddressRepository.findById(id);
        if (addressOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", địa chỉ"
            );
        }
        Optional<Province> provinceOptional = adminClientProvinceRepository.findById(request.getProvince());
        if (provinceOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", tỉnh"
            );
        }
        Optional<District> districtOptional = adminClientDistrictRepository.findById(request.getDistrict());
        if (districtOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", thành phố/quận/huyện"
            );
        }
        Optional<Ward> wardOptional = adminClientWardRepository.findById(request.getWard());
        if (wardOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", phường/xã"
            );
        }
        Address address = addressOptional.get();
        address.setPhoneNumber(request.getPhoneNumber());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setLine(request.getLine());
        address.setName(request.getName());
        if (adminClientAddressRepository.existsAddressByClientId(address.getClientId().getId()) == 0) {
            address.setIsDefault(true);
        }
        adminClientAddressRepository.save(address);
        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    @Transactional
    public ResponseObject<?> changeDefaultClientAddress(String id) {
        Optional<Address> addressOptional = adminClientAddressRepository.findById(id);
        if (addressOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", địa chỉ"
            );
        }
        log.info("Changing default address for clientId: {}", addressOptional.get().getClientId().getId());
        adminClientRepository.disableIsDefaultAddressByClientId(addressOptional.get().getClientId().getId());
        log.info("Setting address with id: {} as default", id);
        adminClientRepository.enableIsDefaultAddressById(id);

        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getAddressesByClientId(String id) {
        return new ResponseObject<>(
                adminClientRepository.getAddressByClientId(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getClientListInChat() {
        return new ResponseObject<>(
                adminClientRepository.getClientListInChat(),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

}

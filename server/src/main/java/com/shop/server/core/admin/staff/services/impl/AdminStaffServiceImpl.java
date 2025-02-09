package com.shop.server.core.admin.staff.services.impl;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.requests.AdminStaffRequest;
import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.admin.staff.repositories.AdminStaffRepository;
import com.shop.server.core.admin.staff.services.AdminStaffExcelService;
import com.shop.server.core.admin.staff.services.AdminStaffMailService;
import com.shop.server.core.admin.staff.services.AdminStaffService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.utils.AESPasswordCryptoUtil;
import com.shop.server.utils.DateTimeUtil;
import com.shop.server.utils.DefaultImageUtil;
import com.shop.server.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AdminStaffServiceImpl implements AdminStaffService {

    private final AdminStaffRepository adminStaffRepository;

    private final AdminStaffMailService emailService;

    private final AdminStaffExcelService staffExcelService;

    public AdminStaffServiceImpl(
            AdminStaffRepository adminStaffRepository,
            AdminStaffMailService emailService, AdminStaffExcelService staffExcelService) {
        this.adminStaffRepository = adminStaffRepository;
        this.emailService = emailService;
        this.staffExcelService = staffExcelService;
    }

    @Override
    public ResponseObject<?> getStaffs(AdminFindStaffRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminStaffRepository.getStaffByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getStaffById(String id) {
        return new ResponseObject<>(
                adminStaffRepository.getStaffDetail(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createStaff(AdminStaffRequest request) {
        if (adminStaffRepository.existsStaffByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        if (adminStaffRepository.existsStaffByIdentity(request.getIdentity())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", mã định danh cá nhân"
            );
        }
        if (adminStaffRepository.existsStaffByPhoneNumber(request.getPhoneNumber())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", số điện thoại"
            );
        }
        try {
            NhanVien staff = new NhanVien();
            String pass = AESPasswordCryptoUtil.genPassword(8L);
            staff.setPassword(pass);
            staff.setFullName(request.getName());
            staff.setSubCode(Helper.getSubCodeFromName(request.getName()));
            staff.setEmail(request.getEmail());
            Long count = adminStaffRepository.countNhanVienByRole(Role.USER) + 1;
            staff.setCode(String.valueOf(count));
            staff.setIdentity(request.getIdentity());
            staff.setBirthday(DateTimeUtil.convertStringToTimeStampSecond(request.getBirthday()));
            staff.setGender(request.getGender());
            staff.setPhoneNumber(request.getPhoneNumber());
            staff.setRole(Role.USER);
            staff.setStatus(Status.ACTIVE);
            staff.setDeleted(false);
            if (request.getPicture().isEmpty()) {
                staff.setProfilePicture(DefaultImageUtil.IMAGE);
            } else {
                staff.setProfilePicture(request.getPicture());
            }
            NhanVien newStaff = adminStaffRepository.save(staff);
            emailService.sendMailCreateStaff(newStaff);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateStaff(String id, AdminStaffRequest request) {
        Optional<NhanVien> staffOptional = adminStaffRepository.findById(id);
        if (staffOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", nhân viên"
            );
        }
        if (adminStaffRepository.existsStaffByEmailAndIdNotEquals(request.getEmail(), id) == 1L) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        if (adminStaffRepository.existsStaffByIdentityAndIdNotEquals(request.getIdentity(), id) == 1L) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", mã định danh cá nhân"
            );
        }
        if (adminStaffRepository.existsStaffByPhoneNumberAndIdNotEquals(request.getPhoneNumber(), id) == 1L) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", số điện thoại"
            );
        }
        try {
            NhanVien staff = staffOptional.get();
            staff.setPassword(request.getPassword());
            staff.setFullName(request.getName());
            staff.setSubCode(Helper.getSubCodeFromName(request.getName()));
            staff.setEmail(request.getEmail());
            staff.setIdentity(request.getIdentity());
            staff.setBirthday(DateTimeUtil.convertStringToTimeStampSecond(request.getBirthday()));
            staff.setGender(request.getGender());
            staff.setPhoneNumber(request.getPhoneNumber());
            adminStaffRepository.save(staff);
            emailService.sendMailUpdateStaff(staff);
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
    public ResponseObject<?> changeStatusStaff(String id) {
        Optional<NhanVien> nhanVienOptional = adminStaffRepository.findById(id);
        if (nhanVienOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", nhân viên"
            );
        }
        NhanVien nhanVien = nhanVienOptional.get();
        nhanVien.setDeleted(!nhanVien.getDeleted());
        adminStaffRepository.save(nhanVien);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateStaffAvatar(String id, AdminStaffRequest request) {
        Optional<NhanVien> staffOptional = adminStaffRepository.findById(id);
        if (staffOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", nhân viên"
            );
        }
        NhanVien staff = staffOptional.get();
        staff.setProfilePicture(request.getPicture());
        adminStaffRepository.save(staff);
        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public List<AdminStaffExcelResponse> getStaffsExcel() {
        return adminStaffRepository.getStaffsExcel();
    }

}

package com.shop.server.core.admin.bill.controller;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminSaveBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminUpdateBillRequest;
import com.shop.server.core.admin.bill.service.AdminBillService;
import com.shop.server.core.admin.bill.service.impl.AdminBillServiceImpl;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping(MappingConstant.API_ADMIN_BILL)
@RestController
public class AdminBillController {
    private final AdminBillService adminBillService;
    private final AdminBillServiceImpl adminBillServiceImpl;

    public AdminBillController(AdminBillService adminBillService, AdminBillServiceImpl adminBillServiceImpl) {
        this.adminBillService = adminBillService;
        this.adminBillServiceImpl = adminBillServiceImpl;
    }

    @GetMapping()
    public ResponseEntity<?> getBills(final AdminFindBillRequest request) {
        return Helper.createResponseEntity(adminBillService.getBills(request));
    }

    @GetMapping("/bill-wait")
    public ResponseEntity<?> getBillsWait() {
        return Helper.createResponseEntity(adminBillService.getBillsWait());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBill(@PathVariable String id) {
        return Helper.createResponseEntity(adminBillService.getDetailBillById(id));
    }

    @PostMapping()
    public ResponseEntity<?> createBill(@Valid @RequestBody final AdminSaveBillRequest request, BindingResult result) {
        return Helper.createResponseEntity(adminBillService.createBill(request, result));
    }

    @PostMapping("create-bill")
    public ResponseEntity<?> createBillCart(@RequestBody final AdminSaveBillRequest request) {
        return Helper.createResponseEntity(adminBillService.createBill(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateBillStatus(@PathVariable String id,
                                              @Valid @RequestBody final AdminUpdateBillRequest request,
                                              BindingResult result) {
        return Helper.createResponseEntity(adminBillService.updateBill(id, request, result));
    }

    @PutMapping("/status-bill/{id}")
    public ResponseEntity<?> changeBillStatus(@PathVariable String id,
                                              @Valid @RequestBody final AdminUpdateBillRequest request) {
        return Helper.createResponseEntity(adminBillService.changeStatusBill(id, request));
    }

    @GetMapping("/count-by-status")
    public Map<String, Integer> getBillCountsByStatus() {
        return adminBillServiceImpl.getBillCountsByStatus();
    }

    @DeleteMapping()
    public ResponseEntity<?> removeBillsWait(@RequestParam String id) {
        return Helper.createResponseEntity(adminBillService.removeBillWait(id));
    }
}

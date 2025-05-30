package com.shop.server.core.admin.point_of_sale.controller;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSAddProductsToCartRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSFindProductRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSInvoicePdfRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSUpdateCartRequest;
import com.shop.server.core.admin.point_of_sale.repository.PointOfSaleRepository;
import com.shop.server.core.admin.point_of_sale.service.InvoicePdfService;
import com.shop.server.core.admin.point_of_sale.service.PdfStorageService;
import com.shop.server.core.admin.point_of_sale.service.PointOfSaleServiceIml;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_ADMIN_POINT_SALE)
@CrossOrigin("*")
public class PointOfSaleController {

    private final PointOfSaleServiceIml pointOfSaleService;

    private final InvoicePdfService invoicePdfService;

    private final PdfStorageService pdfStorageService;

    private final PointOfSaleRepository pointOfSaleRepository;

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(final AdPOSFindProductRequest adFindProductRequest) {
        return Helper.createResponseEntity(pointOfSaleService.getProductsInPOS(adFindProductRequest));
    }

    @GetMapping("/sub/price-rank")
    public Object getPriceRank() {
        return pointOfSaleService.getPriceRank();
    }

    @GetMapping("/products-in-order/{id}")
    public ResponseEntity<?> getProductsInOrder(@PathVariable("id") String idOrder) {
        return Helper.createResponseEntity(pointOfSaleService.getProductsInPendingOrder(idOrder));
    }

    @PostMapping("/products-in-order")
    public ResponseEntity<?> addProductsToOrder(@RequestBody AdPOSAddProductsToCartRequest request) {
        return Helper.createResponseEntity(pointOfSaleService.addProductsToCart(request));
    }

    @PutMapping("/products-in-order")
    public ResponseEntity<?> updateQuantityProductsToOrder(@RequestBody AdPOSUpdateCartRequest request) {
        return Helper.createResponseEntity(pointOfSaleService.updateQuantityProductInCart(request));
    }

    @DeleteMapping("/products-in-order/{idHDCT}")
    public ResponseEntity<?> addProductsToOrder(@PathVariable("idHDCT") String idHDCT) {
        return Helper.createResponseEntity(pointOfSaleService.deleteOrderDetail(idHDCT));
    }

    @GetMapping("/total-amount/{idHoaDon}")
    public ResponseEntity<?> getTotalAmount(@PathVariable("idHoaDon") String idHoaDon) {
        return Helper.createResponseEntity(pointOfSaleService.getTotalAmount(idHoaDon));
    }

    @GetMapping("/get-all/{idHoaDon}")
    public ResponseEntity<?> getAllProductsByIdHoaDon(@PathVariable("idHoaDon") String idHoaDon) {
        return Helper.createResponseEntity(pointOfSaleService.getListProducts(idHoaDon));
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadInvoice(@RequestBody AdPOSInvoicePdfRequest request ) {
        String maHoaDon = pointOfSaleRepository.getInvoiceCodeById(request.getIdHoaDon());
        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .builder("attachment")
                .filename( maHoaDon + ".pdf", StandardCharsets.UTF_8)
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveInvoice(@RequestBody AdPOSInvoicePdfRequest request) {
        try {
            byte[] pdfBytes = invoicePdfService.generateInvoicePdf(request);

            String maHoaDon = pointOfSaleRepository.getInvoiceCodeById(request.getIdHoaDon());
            String filePath = PdfStorageService.savePdfToServer(pdfBytes, maHoaDon + ".pdf");

            if (filePath != null) {
                return ResponseEntity.ok("File PDF đã lưu tại: " + filePath);
            } else {
                return ResponseEntity.status(500).body("Lỗi khi lưu file PDF.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi tạo PDF: " + e.getMessage());
        }
    }

    @PostMapping("/pdf/{id}")
    public ResponseEntity<String> saveInvoiceId(@PathVariable("id") String id, @RequestBody AdPOSInvoicePdfRequest request) {
        try {
            byte[] pdfBytes = invoicePdfService.generateInvoicePdfWithId(id, request);

            String maHoaDon = pointOfSaleRepository.getInvoiceCodeById(id);
            String filePath = PdfStorageService.savePdfToServer(pdfBytes, maHoaDon + ".pdf");

            if (filePath != null) {
                return ResponseEntity.ok("File PDF đã lưu tại: " + filePath);
            } else {
                return ResponseEntity.status(500).body("Lỗi khi lưu file PDF.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi tạo PDF: " + e.getMessage());
        }
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<Resource> viewInvoice(@PathVariable String invoiceId) throws IOException {
        String maHoaDon = pointOfSaleRepository.getInvoiceCodeById(invoiceId);
        File file = new File("C:/TShirtTwo/hoa_don/" + maHoaDon + ".pdf");

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + maHoaDon)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private byte[] generateInvoicePdf(AdPOSInvoicePdfRequest request) {
        try {
            return new byte[0];
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF", e);
        }
    }


}

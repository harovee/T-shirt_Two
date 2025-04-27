package com.shop.server.core.admin.point_of_sale.service;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSInvoicePdfRequest;
import com.shop.server.core.admin.point_of_sale.repository.PointOfSaleRepository;
import com.shop.server.repositories.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class PdfStorageService {

    private final PointOfSaleRepository pointOfSaleRepository;

    public static String savePdfToServer(byte[] pdfBytes, String filename) {
        try {
            String folderPath = "D:/DATN/TShirtShop/hoa-don/";
            File file = new File(folderPath + filename);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
            }

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

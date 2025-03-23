package com.shop.server.core.admin.point_of_sale.service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfStorageService {
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

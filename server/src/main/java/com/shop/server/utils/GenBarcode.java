package com.shop.server.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class GenBarcode {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/barcodes/";  // Thư mục tĩnh

    public static String generateBarcodeImage(String barcodeText) throws WriterException, IOException {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs(); // Tạo thư mục nếu chưa có
        }

        String fileName = barcodeText + ".png"; // Tên file ảnh dựa vào mã voucher
        String filePath = UPLOAD_DIR + fileName;

        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 300, 100);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(image, "png", new File(filePath));

        return "/uploads/barcodes/" + fileName; // Trả về đường dẫn tương đối
    }
    public static void main(String[] args) {
//        try {
//            byte[] barcode = generateBarcode("PGG12345");
//            System.out.println("Barcode generated successfully!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}

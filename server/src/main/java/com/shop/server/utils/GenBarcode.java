package com.shop.server.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class GenBarcode {

    private final CloudinaryService cloudinaryService;

    public GenBarcode(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public String generateBarcodeImage(String barcodeText) throws WriterException, IOException {
        // Tạo barcode
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 300, 100);
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Chuyển đổi sang byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(barcodeImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // Upload lên Cloudinary và trả về đường dẫn
        return cloudinaryService.uploadImage(imageBytes, "barcode_" + barcodeText);
    }
}


package com.shop.server.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class GenBarcode {

    public static byte[] generateBarcode(String barcodeText) throws Exception {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 300, 100);
        // Chuyển thành BufferedImage
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        // Chuyển thành byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
    public static void main(String[] args) {
        try {
            byte[] barcode = generateBarcode("PGG12345");
            System.out.println("Barcode generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

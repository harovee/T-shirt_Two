package com.shop.server.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(byte[] imageBytes, String publicId) throws IOException {
        try {
            // Tạo file tạm thời từ byte array
            File tempFile = File.createTempFile("temp", ".png");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(imageBytes);
            fos.close();

            // Upload file lên Cloudinary
            Map<String, Object> params = ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(tempFile, params);

            // Xóa file tạm sau khi upload
            tempFile.delete();

            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new IOException("Error uploading image to Cloudinary", e);
        }
    }
}


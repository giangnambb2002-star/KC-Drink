package com.example.datn.san_pham.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class SanPhamImageStorageService {

    public static final String IMAGE_URL_PREFIX =
            "/api/san-pham/hinh-anh/";

    private static final long MAX_FILE_SIZE =
            5L * 1024 * 1024;

    private final Path uploadDirectory;

    public SanPhamImageStorageService(
            @Value("${app.upload.product-dir:uploads/products}")
                    String uploadDirectory
    ) {
        this.uploadDirectory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public String save(MultipartFile file) {
        validate(file);

        String extension = detectExtension(file);
        String fileName = UUID.randomUUID() + extension;
        Path target = resolveSafe(fileName);

        try {
            Files.createDirectories(uploadDirectory);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                        inputStream,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return IMAGE_URL_PREFIX + fileName;
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Không thể lưu ảnh sản phẩm",
                    exception
            );
        }
    }

    public Resource load(String fileName) {
        Path file = resolveSafe(fileName);

        try {
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ảnh sản phẩm"
                );
            }

            return resource;
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy ảnh sản phẩm"
            );
        }
    }

    public void deleteByUrl(String imageUrl) {
        if (imageUrl == null
                || !imageUrl.startsWith(IMAGE_URL_PREFIX)) {
            return;
        }

        String fileName =
                imageUrl.substring(IMAGE_URL_PREFIX.length());

        try {
            Files.deleteIfExists(resolveSafe(fileName));
        } catch (Exception exception) {
            log.warn(
                    "Không thể xóa ảnh sản phẩm: {}",
                    imageUrl,
                    exception
            );
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Vui lòng chọn ảnh sản phẩm"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException(
                    "Ảnh sản phẩm không được vượt quá 5 MB"
            );
        }
    }

    private String detectExtension(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = inputStream.readNBytes(12);

            if (matches(
                    header,
                    0x89, 0x50, 0x4E, 0x47,
                    0x0D, 0x0A, 0x1A, 0x0A
            )) {
                return ".png";
            }

            if (matches(header, 0xFF, 0xD8, 0xFF)) {
                return ".jpg";
            }

            if (header.length >= 12
                    && header[0] == 'R'
                    && header[1] == 'I'
                    && header[2] == 'F'
                    && header[3] == 'F'
                    && header[8] == 'W'
                    && header[9] == 'E'
                    && header[10] == 'B'
                    && header[11] == 'P') {
                return ".webp";
            }

            throw new RuntimeException(
                    "Ảnh chỉ hỗ trợ định dạng JPG, PNG hoặc WebP"
            );
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Không thể đọc ảnh sản phẩm",
                    exception
            );
        }
    }

    private boolean matches(
            byte[] data,
            int... expected
    ) {
        if (data.length < expected.length) {
            return false;
        }

        for (int index = 0; index < expected.length; index++) {
            if ((data[index] & 0xFF) != expected[index]) {
                return false;
            }
        }

        return true;
    }

    private Path resolveSafe(String fileName) {
        if (fileName == null
                || fileName.isBlank()
                || fileName.contains("/")
                || fileName.contains("\\")
                || fileName.contains("..")) {
            throw new RuntimeException(
                    "Tên file ảnh không hợp lệ"
            );
        }

        Path file = uploadDirectory
                .resolve(fileName)
                .normalize();

        if (!file.startsWith(uploadDirectory)) {
            throw new RuntimeException(
                    "Đường dẫn ảnh không hợp lệ"
            );
        }

        return file;
    }
}
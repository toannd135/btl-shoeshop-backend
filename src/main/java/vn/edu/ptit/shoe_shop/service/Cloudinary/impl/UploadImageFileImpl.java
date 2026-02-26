package vn.edu.ptit.shoe_shop.service.Cloudinary.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptit.shoe_shop.service.Cloudinary.UploadImageFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadImageFileImpl implements UploadImageFile {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_EXT = List.of(
            "jpg", "jpeg", "png", "webp"
    );

    @Override
    public String uploadImage(MultipartFile file, String folder ,UUID id) throws IOException {

        String baseName = getBaseName(file);
        String publicId = UUID.randomUUID() + "_" + baseName;

        Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                         "folder", "shoe_shop/" + folder + "/" + id,
                        "public_id", publicId,
                        "resource_type", "image",
                        "overwrite", true
                )
        );

        return (String) result.get("secure_url");
    }

    private static String getBaseName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String ext = originalName.substring(originalName.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Invalid image type");
        }

        return originalName.substring(0, originalName.lastIndexOf('.'));
    }
}

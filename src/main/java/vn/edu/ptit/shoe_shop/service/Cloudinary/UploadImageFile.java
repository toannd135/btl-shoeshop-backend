package vn.edu.ptit.shoe_shop.service.Cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UploadImageFile {
    String uploadImage(MultipartFile file,String folder, UUID id) throws IOException;
}
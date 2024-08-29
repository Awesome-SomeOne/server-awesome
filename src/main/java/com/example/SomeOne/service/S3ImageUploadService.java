package com.example.SomeOne.service;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.SomeOne.exception.ImageStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ImageUploadService {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveImage(MultipartFile image) {
        try {
            String fileName = generateFileName(image);
            String fileUrl = uploadToS3(image, fileName);
            return fileUrl;
        } catch (IOException e) {
            throw new ImageStorageException("Failed to store file in S3", e);
        }
    }

    private String generateFileName(MultipartFile image) {
        return UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
    }

    private String uploadToS3(MultipartFile image, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}
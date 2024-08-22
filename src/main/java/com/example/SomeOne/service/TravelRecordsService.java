package com.example.SomeOne.service;

import com.example.SomeOne.domain.*;
import com.example.SomeOne.dto.TravelRecords.Request.CreateTravelRecordRequest;
import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
import com.example.SomeOne.exception.ImageStorageException;
import com.example.SomeOne.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelRecordsService {

    private final TravelRecordsRepository travelRecordsRepository;
    private final TravelPlansRepository travelPlansRepository;
    private final UserRepository userRepository;
    private final RecordImagesRepository recordImagesRepository;

    @Transactional
    public TravelRecordResponse create(List<MultipartFile> images, CreateTravelRecordRequest request) {
        TravelPlans plan = travelPlansRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + request.getPlanId()));

        Users user = plan.getUser();

        TravelRecords record = TravelRecords.builder()
                .user(user)
                .plan(plan)
                .recordTitle(request.getOneLineReview())
                .recordContent(request.getOverallReview())
                .publicPrivate(request.isPublicPrivate())
                .build();

        // 저장된 이미지 URL 리스트를 저장할 변수
        List<String> imageUrls = new ArrayList<>();

        // 각 이미지 파일을 저장하고 TravelRecords에 추가
        for (MultipartFile image : images) {
            String imageUrl = saveImage(image);
            imageUrls.add(imageUrl);

            RecordImages recordImage = RecordImages.builder()
                    .image_url(imageUrl)
                    .build();

            // TravelRecords 엔티티에 RecordImages 추가
            record.addRecordImage(recordImage);
        }

        TravelRecords savedRecord = travelRecordsRepository.save(record);

        // 첫 번째 이미지 URL을 반환하도록 설정
        return new TravelRecordResponse(savedRecord, imageUrls.isEmpty() ? null : imageUrls.get(0));
    }

    // 이미지 저장 메서드
    private final String imageUploadDir = "D:/SomeOne/static/images";

    private String saveImage(MultipartFile image) {
        try {
            String filePath = imageUploadDir + "/" + image.getOriginalFilename();
            File dest = new File(filePath);
            image.transferTo(dest);
            return "http://localhost:8080/images/" + image.getOriginalFilename();
        } catch (IOException e) {
            throw new ImageStorageException("Failed to store file", e);
        }
    }

    // 여행 기록 수정 메서드
    @Transactional
    public TravelRecordResponse update(Long recordId, CreateTravelRecordRequest request, List<MultipartFile> newImages) {
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        // 여행 기록 필드 업데이트
        record.setRecordTitle(request.getOneLineReview());
        record.setRecordContent(request.getOverallReview());
        record.setPublicPrivate(request.isPublicPrivate());

        // 기존 이미지 처리 (필요시 삭제 또는 유지)
        record.getRecordImages().clear();
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                String imageUrl = saveImage(image);
                RecordImages recordImage = RecordImages.builder()
                        .image_url(imageUrl)
                        .build();
                record.addRecordImage(recordImage);
            }
        }

        // 업데이트된 기록 저장
        TravelRecords updatedRecord = travelRecordsRepository.save(record);
        return new TravelRecordResponse(updatedRecord, updatedRecord.getRecordImages().isEmpty() ? null : updatedRecord.getRecordImages().get(0).getImage_url());
    }

    // 기존 여행 기록 삭제 메서드
    @Transactional
    public void delete(Long recordId) {
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        // 삭제 전에 관련 이미지 삭제 (선택적)
        for (RecordImages image : record.getRecordImages()) {
            deleteImageFromStorage(image.getImage_url());  // 실제 이미지 삭제 로직
        }

        travelRecordsRepository.delete(record);
    }

    private void deleteImageFromStorage(String imageUrl) {
        // 실제 파일 삭제 로직
        File file = new File(imageUrl);
        if (file.exists()) {
            file.delete();
        }
    }

    // 링크 공유
    public TravelRecordResponse getRecordById(Long recordId) {
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));
        List<String> imageUrls = record.getRecordImages().stream()
                .map(RecordImages::getImage_url)
                .collect(Collectors.toList());
        return new TravelRecordResponse(record, imageUrls.isEmpty() ? null : imageUrls.get(0));
    }

    // 사용자별 여행 기록 조회
    public List<TravelRecordResponse> getRecordsByUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        List<TravelRecords> records = travelRecordsRepository.findByUserWithImagesOrderByRecordIdDesc(user);
        return records.stream()
                .map(record -> new TravelRecordResponse(record,
                        record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url()))
                .collect(Collectors.toList());
    }

    // 여행 계획별 여행 기록 조회
    public List<TravelRecordResponse> getRecordsByPlan(Long planId) {
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + planId));
        List<TravelRecords> records = travelRecordsRepository.findByPlanWithImagesOrderByRecordIdDesc(plan);
        return records.stream()
                .map(record -> new TravelRecordResponse(record,
                        record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url()))
                .collect(Collectors.toList());
    }

    // 사용자별 여행 기록 조회 - publicPrivate:true only
    public List<TravelRecordResponse> getRecordsByUserTrue(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        List<TravelRecords> records = travelRecordsRepository.findByUserAndPublicPrivateOrderByRecordIdDesc(user, true);
        return records.stream()
                .map(record -> new TravelRecordResponse(record,
                        record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url()))
                .collect(Collectors.toList());
    }

    // 여행 계획별 여행 기록 조회 - publicPrivate:true only
    public List<TravelRecordResponse> getRecordsByPlanTrue(Long planId) {
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + planId));
        List<TravelRecords> records = travelRecordsRepository.findByPlanAndPublicPrivateOrderByRecordIdDesc(plan, true);
        return records.stream()
                .map(record -> new TravelRecordResponse(record,
                        record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url()))
                .collect(Collectors.toList());
    }


}




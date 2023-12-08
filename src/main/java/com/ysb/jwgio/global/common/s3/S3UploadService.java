package com.ysb.jwgio.global.common.s3;

import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;
    private final MemberRepository memberRepository;
    @Value("${aws.s3.bucket}")
    private String bucket;

    public String saveFile(MultipartFile file) throws IOException {
//        String originalFilename = multipartFile.getOriginalFilename();

//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(multipartFile.getSize());
//        metadata.setContentType(multipartFile.getContentType());
        SecurityContext context = SecurityContextHolder.getContext();
        String member_id = context.getAuthentication().getName();
        String filePath = "profile_img/"+member_id+"/";
        String contentType = file.getContentType();

////        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String fileName = member_id + "-" + "jwgio-profile-img-" + UUID.randomUUID() + "." + getExtension(file);
//        String uploadLoc = filePath+fileName+".jpg";
//        System.out.println(uploadLoc);
//        beforeUploadCheckFile(filePath, member_id);
//        amazonS3.putObject(bucket, uploadLoc, multipartFile.getInputStream(), metadata);
//        return amazonS3.getUrl(bucket, uploadLoc).toString();
        beforeUploadCheckFile(filePath, member_id);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath+fileName)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(filePath+fileName)
                .build();
        URL url = s3Client.utilities().getUrl(request);
        return url.toString();
    }

    public void beforeUploadCheckFile(String filePath, String sMember_id) {
        Long member_id = Long.parseLong(sMember_id);
        Member member = memberRepository.findById(member_id).get();
        String imgUrl = member.getProfileImg();
        System.out.println(imgUrl);
        if(imgUrl != null) {
            System.out.println(imgUrl);
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/")+1);
            ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
            toDelete.add(ObjectIdentifier.builder()
                    .key(filePath+fileName)
                    .build());
            try {
                DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder()
                                .objects(toDelete).build())
                        .build();

                s3Client.deleteObjects(dor);

            } catch (S3Exception e) {
                log.error(e.toString());
            }
        }
    }
    public String getExtension(MultipartFile file) {
        return StringUtils.getFilenameExtension(file.getOriginalFilename());
    }
}

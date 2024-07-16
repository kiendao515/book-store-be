package box.bookstorebe.service.file;

import box.bookstorebe.exception.BizException;
import box.bookstorebe.service.common.AmazonProperty;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.Resource;
import org.apache.commons.io.FilenameUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    @Autowired
    private AmazonProperty amazonProperties;
    private final AmazonS3 s3Client;

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(multipartFile.getBytes());
        }
        String fileName = generateFileName(multipartFile);
        PutObjectRequest request = new PutObjectRequest(amazonProperties.getBucketName(), fileName, file);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(request);
        return fileName;
    }

    @Override
    public Object downloadFile(String fileName) throws IOException, BizException {
        if (bucketIsEmpty()) {
            throw new BizException("Requested bucket does not exist or is empty");
        }
        S3Object object = s3Client.getObject(amazonProperties.getBucketName(), fileName);
        try (S3ObjectInputStream s3is = object.getObjectContent()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                byte[] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fileOutputStream.write(read_buf, 0, read_len);
                }
            }
            Path pathObject = Paths.get(fileName);
            Resource resource = new UrlResource(pathObject.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new BizException("Could not find the file!");
            }
        }
    }

    @Override
    public boolean delete(String fileName) {
        File file = Paths.get(fileName).toFile();
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    private boolean bucketIsEmpty() {
        ListObjectsV2Result result = s3Client.listObjectsV2(amazonProperties.getBucketName());
        if (result == null){
            return false;
        }
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects.isEmpty();
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}

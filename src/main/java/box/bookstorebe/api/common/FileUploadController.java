package box.bookstorebe.api.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.file.APIResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.repository.common.image.ImageRepository;
import box.bookstorebe.service.common.AmazonProperty;
import box.bookstorebe.service.file.FileService;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sun.jdi.event.ExceptionEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/api/v1/file")
@Validated
@AllArgsConstructor
public class FileUploadController {
    private final FileService fileService;
    private final ImageRepository imageRepository;
    private AmazonProperty amazonProperty;

    @PostMapping("/upload")
    public BaseResponse<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException, BizException {
        if (multipartFile.isEmpty()){
            throw new BizException("File is empty. Cannot save an empty file");
        }
        boolean isValidFile = isValidFile(multipartFile);
        List<String> allowedFileExtensions = new ArrayList<>(Arrays.asList("pdf", "txt", "epub", "csv", "png", "jpg", "jpeg", "srt"));

        if (isValidFile && allowedFileExtensions.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()))){
            String fileName = fileService.uploadFile(multipartFile);
            String link = amazonProperty.getEndpointUrl()+"/"+fileName;
            ImageDocument imageDocument = ImageDocument.builder()
                    .link(amazonProperty.getEndpointUrl()+"/"+fileName)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now())
                    .build();
            ImageDocument image = imageRepository.save(imageDocument);
            APIResponse apiResponse = APIResponse.builder()
                    .message("file "+ fileName+ " uploaded successfully")
                    .url(link)
                    .id(image.getId())
                    .build();
            return new BaseResponse<>(Const.ResultCode.SUCCESS, apiResponse);
        } else throw new BizException("Invalid File. File extension or File name is not supported");
    }


    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam("fileName")  @NotBlank @NotNull String fileName) throws BizException, IOException {
        Object response = fileService.downloadFile(fileName);
        if (response != null){
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(response);
        } else {
            APIResponse apiResponse = APIResponse.builder()
                    .message("File could not be downloaded")
                    .build();
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("fileName") @NotBlank @NotNull String fileName) throws BizException {
        boolean isDeleted = fileService.delete(fileName);
        if (isDeleted){
            APIResponse apiResponse = APIResponse.builder().message("file deleted!").build();
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else throw new BizException("file not existed");
    }

    private boolean isValidFile(MultipartFile multipartFile){
        if (Objects.isNull(multipartFile.getOriginalFilename())){
            return false;
        }
        return !multipartFile.getOriginalFilename().trim().equals("");
    }
}

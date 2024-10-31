package box.bookstorebe.service.file;
import box.bookstorebe.dto.file.FileDto;
import box.bookstorebe.exception.BizException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    FileDto uploadFile(MultipartFile multipartFile) throws IOException;

    Object downloadFile(String fileName) throws IOException, BizException;

    boolean delete(String fileName);
}
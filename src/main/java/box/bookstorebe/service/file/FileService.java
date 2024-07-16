package box.bookstorebe.service.file;
import box.bookstorebe.exception.BizException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile multipartFile) throws IOException;

    Object downloadFile(String fileName) throws IOException, BizException;

    boolean delete(String fileName);
}
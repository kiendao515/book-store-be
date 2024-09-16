package box.bookstorebe.service.common;

import box.bookstorebe.document.common.SystemConfigDocument;
import box.bookstorebe.dto.common.SystemConfigDto;
import box.bookstorebe.model.common.SystemConfigModel;
import box.bookstorebe.repository.common.systemconfig.SystemConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public SystemConfigDto getSystemConfig(String key) {
        SystemConfigDocument systemConfigDocument = systemConfigRepository.findByKey(key);
        return SystemConfigDto.builder()
                .id(systemConfigDocument.getId())
                .key(systemConfigDocument.getKey())
                .value(systemConfigDocument.getValue())
                .dataType(systemConfigDocument.getDataType())
                .build();
    }


    public void updateSystemConfig(SystemConfigModel systemConfigModel) {
        SystemConfigDocument systemConfigDocument = systemConfigRepository.findByKey(systemConfigModel.getKey());
        if (systemConfigDocument == null) {
            SystemConfigDocument newSystemConfigDocument = new SystemConfigDocument();
            newSystemConfigDocument.setKey(systemConfigModel.getKey());
            newSystemConfigDocument.setValue(systemConfigModel.getValue());
            newSystemConfigDocument.setDataType(systemConfigModel.getDataType());
            systemConfigRepository.save(newSystemConfigDocument);
        } else {
            systemConfigDocument.setValue(systemConfigModel.getValue());
            systemConfigDocument.setDataType(systemConfigModel.getDataType());
            systemConfigRepository.save(systemConfigDocument);
        }
    }

}

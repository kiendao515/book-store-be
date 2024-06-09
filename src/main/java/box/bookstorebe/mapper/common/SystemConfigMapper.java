package box.bookstorebe.mapper.common;

import box.bookstorebe.document.common.SystemConfigDocument;
import box.bookstorebe.dto.common.SystemConfigDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfigDocument, SystemConfigDto> {
    SystemConfigMapper INSTANCE = Mappers.getMapper(SystemConfigMapper.class);
}

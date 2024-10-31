package box.bookstorebe.mapper.common;

import box.bookstorebe.document.common.CommonEntity;
import box.bookstorebe.dto.common.CommonEntityDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommonEntityMapper extends BaseMapper<CommonEntity, CommonEntityDto> {
    CommonEntityMapper INSTANCE = Mappers.getMapper(CommonEntityMapper.class);
}
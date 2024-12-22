package box.bookstorebe.mapper.common;

import box.bookstorebe.document.common.WebContentDocument;
import box.bookstorebe.dto.common.WebContentDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WebContentMapper extends BaseMapper<WebContentDocument, WebContentDto> {
    WebContentMapper INSTANCE = Mappers.getMapper(WebContentMapper.class);
}

package box.bookstorebe.mapper.common;

import box.bookstorebe.document.common.PersonDocument;
import box.bookstorebe.dto.common.PersonDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper extends BaseMapper<PersonDocument, PersonDto> {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
}

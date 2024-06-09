package box.bookstorebe.mapper.user;

import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.dto.user.UserDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper extends BaseMapper<UserDocument, UserDto> {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}

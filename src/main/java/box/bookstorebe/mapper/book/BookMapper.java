package box.bookstorebe.mapper.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.user.UserDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper extends BaseMapper<BookDocument, BookDto> {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
}

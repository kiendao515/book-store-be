package box.bookstorebe.mapper.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRelatedPersonDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookRelatedPersonMapper extends BaseMapper<BookRelatedPersonDocument, BookRelatedPersonDto> {
    BookRelatedPersonMapper INSTANCE = Mappers.getMapper(BookRelatedPersonMapper.class);
}

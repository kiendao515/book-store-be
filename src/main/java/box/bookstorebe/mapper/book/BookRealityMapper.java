package box.bookstorebe.mapper.book;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookRealityMapper extends BaseMapper<BookRealityDocument, BookRealityDto> {
    BookRealityMapper INSTANCE = Mappers.getMapper(BookRealityMapper.class);
}

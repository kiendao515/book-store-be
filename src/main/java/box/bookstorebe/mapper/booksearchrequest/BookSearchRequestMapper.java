package box.bookstorebe.mapper.booksearchrequest;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import box.bookstorebe.dto.booksearchrequest.BookSearchRequestDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookSearchRequestMapper extends BaseMapper<BookSearchRequestDocument, BookSearchRequestDto> {
    BookSearchRequestMapper INSTANCE = Mappers.getMapper(BookSearchRequestMapper.class);
}

package box.bookstorebe.mapper.bookstore;

import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookStoreMapper extends BaseMapper<StoreDocument, BookStoreDto> {
    BookStoreMapper INSTANCE = Mappers.getMapper(BookStoreMapper.class);
}

package box.bookstorebe.mapper.book;

import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.dto.book.BookInventoryDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookInventoryMapper extends BaseMapper<BookInventory, BookInventoryDto> {
    BookInventoryMapper INSTANCE = Mappers.getMapper(BookInventoryMapper.class);
}

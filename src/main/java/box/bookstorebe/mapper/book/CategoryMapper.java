package box.bookstorebe.mapper.book;

import box.bookstorebe.document.book.BookRelatedPersonDocument;
import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.dto.book.BookRelatedPersonDto;
import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper extends BaseMapper<CategoryDocument, CategoryDto> {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
}

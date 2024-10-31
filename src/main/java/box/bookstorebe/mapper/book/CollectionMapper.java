package box.bookstorebe.mapper.book;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.book.CollectionDocument;
import box.bookstorebe.dto.book.CategoryDto;
import box.bookstorebe.dto.book.CollectionDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CollectionMapper extends BaseMapper<CollectionDocument, CollectionDto> {
    CollectionMapper INSTANCE = Mappers.getMapper(CollectionMapper.class);
}

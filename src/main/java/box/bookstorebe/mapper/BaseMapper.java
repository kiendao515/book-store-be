package box.bookstorebe.mapper;

import java.util.List;

public interface BaseMapper<E, DTO> {
    DTO entityToDto(E e);

    List<DTO> entityToDto(List<E> listEntity);

    E dtoToEntity(DTO dto);

    List<E> dtoToEntity(List<DTO> listDto);
}

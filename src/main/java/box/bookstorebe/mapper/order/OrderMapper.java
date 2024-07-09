package box.bookstorebe.mapper.order;

import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper extends BaseMapper<OrderDocument, OrderDto> {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
}

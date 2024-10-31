package box.bookstorebe.mapper.account;


import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper extends BaseMapper<AccountDocument, AccountDto> {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);
}

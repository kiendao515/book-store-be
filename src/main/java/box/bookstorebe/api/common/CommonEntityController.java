package box.bookstorebe.api.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.CommonEntity;
import box.bookstorebe.dto.book.BookRelatedPersonDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.common.CommonEntityDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.book.CreateBookModel;
import box.bookstorebe.model.book.bookrelatedperson.UpdateBookRelatedPersonModel;
import box.bookstorebe.model.book.common.CommonEntityModel;
import box.bookstorebe.model.book.common.UpdateCommonEntity;
import box.bookstorebe.service.book.CommonEntityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/entity/common")
@AllArgsConstructor
public class CommonEntityController {
    private final CommonEntityService commonEntityService;
    @GetMapping()
    public BasePagingResponse<CommonEntityDto> getBookRelatedPeople(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(commonEntityService.getEntity(type, page, size));
    }
    @PostMapping
    public BaseResponse<String> createEntity(@RequestBody @Valid CommonEntityModel commonEntityModel) throws BizException {
        commonEntityService.createEntity(commonEntityModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new common entity successfully");
    }
    @PutMapping("{id}")
    public BaseResponse<String> updateEntity(@PathVariable String id, @RequestBody @Valid UpdateCommonEntity entity) throws BizException {
        commonEntityService.updateEntity(id, entity);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update entity successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteEntity(@PathVariable String id) throws BizException {
        commonEntityService.deleteEntity(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete entity successfully");
    }
}

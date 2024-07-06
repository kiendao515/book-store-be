package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.book.BookRelatedPersonDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookrelatedperson.CreateBookRelatedPersonModel;
import box.bookstorebe.model.book.bookrelatedperson.UpdateBookRelatedPersonModel;
import box.bookstorebe.service.book.BookRelatedPersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-related-people")
@RequiredArgsConstructor
public class BookRelatedPersonController {
    private final BookRelatedPersonService bookRelatedPersonService;

    @GetMapping()
    public BasePagingResponse<BookRelatedPersonDto> getBookRelatedPeople(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(bookRelatedPersonService.getBookRelatedPersons(name, type, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<BookRelatedPersonDto> getBookRelatedPersonDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookRelatedPersonService.findById(id));
    }

    @PostMapping
    public BaseResponse<String> createNewBookRelatedPerson(@RequestBody @Valid CreateBookRelatedPersonModel bookRelatedPersonModel) throws BizException {
        bookRelatedPersonService.createNewBookRelatedPerson(bookRelatedPersonModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new book related person successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateBookRelatedPerson(@PathVariable String id, @RequestBody @Valid UpdateBookRelatedPersonModel bookRelatedPersonModel) throws BizException {
        bookRelatedPersonService.updateBookRelatedPerson(id, bookRelatedPersonModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book related person successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBookRelatedPerson(@PathVariable String id) throws BizException {
        bookRelatedPersonService.deleteBookRelatedPerson(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete book related person successfully");
    }
}

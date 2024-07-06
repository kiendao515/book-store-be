package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.book.BookRealityDto;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.service.book.BookRealityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-realities")
@RequiredArgsConstructor
public class BookRealityController {
    private final BookRealityService bookRealityService;

    @GetMapping("{id}")
    public BaseResponse<BookRealityDto> getBookRealityDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookRealityService.findById(id));
    }

    @PostMapping
    public BaseResponse<String> createNewBookReality(@RequestBody @Valid CreateBookRealityModel bookModel) throws BizException {
        bookRealityService.createBookReality(bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new reality book successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateBook(@PathVariable String id, @RequestBody @Valid UpdateBookRealityModel bookModel) throws BizException {
        bookRealityService.updateBookReality(id, bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update reality book successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBook(@PathVariable String id) throws BizException {
        bookRealityService.deleteBookReality(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete reality book successfully");
    }
}

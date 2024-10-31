package box.bookstorebe.api.booksearchrequest;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.booksearchrequest.BookSearchRequestDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.booksearchrequest.booksearchrequest.CreateBookSearchRequestModel;
import box.bookstorebe.model.booksearchrequest.booksearchrequest.UpdateBookSearchRequestModel;
import box.bookstorebe.service.booksearchrequest.BookSearchRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book-search-requests")
@RequiredArgsConstructor
public class BookSearchRequestController {
    private final BookSearchRequestService bookSearchRequestService;

    @GetMapping()
    public BasePagingResponse<BookSearchRequestDto> getBookSearchRequests(
            @RequestParam(name = "user_id", required = false) String userId,
            @RequestParam(name = "full_name", required = false) String fullName,
            @RequestParam(name = "phone_number", required = false) String phoneNumber,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(bookSearchRequestService.getBookSearchRequests(userId, fullName, phoneNumber, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<BookSearchRequestDto> getBookSearchRequest(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookSearchRequestService.findById(id));
    }

    @PostMapping
    public BaseResponse<String> createNewBookSearchRequest(@RequestBody @Valid CreateBookSearchRequestModel bookSearchRequestModel) throws BizException {
        bookSearchRequestService.createBookSearchRequest(bookSearchRequestModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new book search request successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateBookSearchRequest(@PathVariable String id, @RequestBody @Valid UpdateBookSearchRequestModel bookSearchRequestModel) throws BizException {
        bookSearchRequestService.updateBookSearchRequest(id, bookSearchRequestModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book search request successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBookSearchRequest(@PathVariable String id) throws BizException {
        bookSearchRequestService.deleteBookSearchRequest(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete book search request successfully");
    }
}

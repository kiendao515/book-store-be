package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookFavoriteDto;
import box.bookstorebe.dto.book.BookSettingDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.book.CreateBookModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.model.book.book.UpdateMultipleBookRealityModel;
import box.bookstorebe.model.book.common.BookSettingModel;
import box.bookstorebe.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("favorite")
    public BaseResponse<BookFavoriteDto> getBookFavorite() throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookService.getBookFavorite());
    }

    @PutMapping("favorite/{id}")
    public BaseResponse<String> updateBookFavorite(@PathVariable String id) throws BizException {
        bookService.updateBookFavorite(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book favorite successfully");
    }

    @GetMapping("setting")
    public BaseResponse<BookSettingDto> getBookSetting() throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookService.getBookSetting());
    }

    @PostMapping("setting")
    public BaseResponse<String> updateBookSetting(@RequestBody BookSettingModel bookSetting) throws BizException {
        bookService.createBookSetting(bookSetting);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book setting successfully");
    }

    @GetMapping()
    public BasePagingResponse<BookDto> getBooks(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category_id", required = false) String categoryId,
            @RequestParam(name = "author_id", required = false) String authorId,
            @RequestParam(name = "store_id", required = false) String storeId,
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endAt,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(bookService.getBooks(name, categoryId, authorId, storeId, startAt, endAt, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<BookDto> getBookDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookService.findById(id));
    }

    @PostMapping
    public BaseResponse<String> createNewBook(@RequestBody @Valid CreateBookModel bookModel) throws BizException {
        bookService.createNewBook(bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new book successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateBook(@PathVariable String id, @RequestBody @Valid UpdateBookModel bookModel) throws BizException {
        bookService.updateBook(id, bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book successfully");
    }

    @PutMapping("{id}/reality-books")
    public BaseResponse<String> updateMultipleRealityBook(@PathVariable String id, @RequestBody @Valid UpdateMultipleBookRealityModel multipleBookRealityModel) throws BizException {
        bookService.updateMultipleBookReality(id, multipleBookRealityModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book reality successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBook(@PathVariable String id) throws BizException {
        bookService.deleteBook(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete book successfully");
    }
}

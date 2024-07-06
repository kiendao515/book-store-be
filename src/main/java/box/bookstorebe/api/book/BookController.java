package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.book.CreateBookModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping()
    public BasePagingResponse<BookDto> getBooks(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category_ids", required = false) List<String> categoryIds,
            @RequestParam(name = "collection_ids", required = false) List<String> collectionIds,
            @RequestParam(name = "related_person_ids", required = false) List<String> relatedPersonIds,
            @RequestParam(name = "store_id", required = false) String storeId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) throws BizException {
        return new BasePagingResponse<>(bookService.getBooks(name, categoryIds, collectionIds, relatedPersonIds, storeId, page, size));
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

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBook(@PathVariable String id) throws BizException {
        bookService.deleteBook(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete book successfully");
    }
}

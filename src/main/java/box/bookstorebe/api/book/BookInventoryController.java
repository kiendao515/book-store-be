package box.bookstorebe.api.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.book.bookreality.CreateBookAndInventory;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.book.bookreality.UpdateBookRealityModel;
import box.bookstorebe.service.book.BookInventoryService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class BookInventoryController {
    private final BookInventoryService bookInventoryService;

    @GetMapping()
    public BaseResponse<List<BookInventory>> getBookInventoryDetail(@RequestParam String bookId, @RequestParam String storeId) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookInventoryService.getDetailBookInventory(bookId, storeId));
    }

    @PostMapping
    public BaseResponse<String> createNewBookReality(@RequestBody @Valid CreateBookRealityModel bookModel) throws BizException {
        bookInventoryService.createBookInventory(bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new reality book successfully");
    }

    @PostMapping("/create")
    public BaseResponse<String> createNewBookAndUpdateInventory(@RequestBody @Valid CreateBookAndInventory bookModel) throws BizException {
        bookInventoryService.createBookAndInventory(bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new reality book successfully");
    }

    @PutMapping()
    public BaseResponse<String> updateBook(@RequestBody @Valid UpdateBookRealityModel bookModel) throws BizException, MessagingException {
        bookInventoryService.updateBookInventory(bookModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update reality book successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteBook(@PathVariable String id) throws BizException {
        bookInventoryService.deleteBookReality(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete reality book successfully");
    }
}

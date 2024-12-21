package box.bookstorebe.api.bookstore;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.account.DeleteAccountDto;
import box.bookstorebe.dto.bookstore.BookStoreDto;
import box.bookstorebe.dto.bookstore.DetailBookRevenue;
import box.bookstorebe.dto.bookstore.StoreRevenueDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.bookstore.CreateBookStoreModel;
import box.bookstorebe.model.bookstore.CreateBookstoreAndAccount;
import box.bookstorebe.model.bookstore.UpdateBookStoreModel;
import box.bookstorebe.service.bookstore.BookStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/book-stores")
@RequiredArgsConstructor
public class BookStoreController {
    private final BookStoreService bookStoreService;

    @GetMapping()
    public BasePagingResponse<BookStoreDto> getBookStores(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(bookStoreService.getBookStores(name, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<BookStoreDto> getBookStoreDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookStoreService.findById(id));
    }

    @GetMapping("/statistic")
    public BaseResponse<List<StoreRevenueDto>> getBookStoreStatistic(
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to,
            @RequestParam(name = "store_id", required = false) String storeId
    ) throws BizException {
        ZonedDateTime fromDate = null;
        ZonedDateTime toDate =null;
        if (!from.isEmpty() && !to.isEmpty()) {
            fromDate = ZonedDateTime.parse(from);
            toDate = ZonedDateTime.parse(to);
        }
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookStoreService.getStoreRevenue(storeId, fromDate, toDate));
    }

    @GetMapping("/statistic/detail")
    public BaseResponse<DetailBookRevenue> getDetailBookRevenue(
            @RequestParam(name = "store_id", required = false) String storeId,
            @RequestParam(name = "book_id", required = false) String bookId) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, bookStoreService.getDetailBookRevenue(bookId, storeId));
    }

    @PostMapping("/statistic/confirm")
    public BaseResponse<String> confirmBookRevenue(
            @RequestParam(name = "store_id", required = false) String storeId,
            @RequestBody List<String> ids) throws BizException {
        bookStoreService.confirmBookRevenue(ids ,storeId);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Thanh cong");
    }

//    @PostMapping
//    public BaseResponse<String> createNewBookStore(@RequestBody @Valid CreateBookStoreModel bookStoreModel) throws BizException {
//        bookStoreService.createNewBookStore(bookStoreModel);
//        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new book store successfully");
//    }

    @PostMapping()
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> createNewBookStoreAndAccount(@RequestBody @Valid CreateBookstoreAndAccount bookStoreModel) throws BizException {
        bookStoreService.createNewBookStoreAndAccount(bookStoreModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new book store and account successfully");
    }


    @PutMapping("{id}")
    public BaseResponse<String> updateBookStore(@PathVariable String id, @RequestBody @Valid UpdateBookStoreModel bookStoreModel) throws BizException {
        bookStoreService.updateBookStore(id, bookStoreModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book store successfully");
    }

    @DeleteMapping()
    public BaseResponse<String> deleteBook(@RequestBody DeleteAccountDto deleteAccountDto) throws BizException {
        bookStoreService.deleteBookStore(deleteAccountDto);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete book store successfully");
    }
}

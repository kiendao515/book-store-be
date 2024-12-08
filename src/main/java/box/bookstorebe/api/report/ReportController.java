package box.bookstorebe.api.report;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.book.CategorySalesStat;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.dto.report.BookStockDto;
import box.bookstorebe.dto.report.OrderReportDto;
import box.bookstorebe.dto.report.RevenueStatDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.service.report.ReportService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping()
    public BaseResponse<OrderReportDto> getOrderStatisticByMonth( @RequestParam("from") String fromDate,
                                                                  @RequestParam("to") String toDate) throws BizException {
        ZonedDateTime fromDateTime = ZonedDateTime.parse(fromDate);
        ZonedDateTime toDateTime = ZonedDateTime.parse(toDate);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, reportService.getOrderStatisticByMonth(fromDateTime, toDateTime));
    }

    @GetMapping("/category")
    public BaseResponse<List<CategorySalesStat>> getTopSellingCategories(
            @RequestParam("from") String fromDate,
            @RequestParam("to") String toDate) {
        ZonedDateTime fromDateTime = ZonedDateTime.parse(fromDate);
        ZonedDateTime toDateTime = ZonedDateTime.parse(toDate);

        List<CategorySalesStat> result = reportService.getTopSellingCategories(fromDateTime, toDateTime);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, result);
    }

    @GetMapping("/buyer")
    public BaseResponse<List<AccountDto>> getBuyerStatisticByMonth(
            @RequestParam("from") String startDate,
            @RequestParam("to") String endDate) {
        Instant fromInstant = Instant.parse(startDate);
        Instant toInstant = Instant.parse(endDate);

        LocalDateTime fromDateTime = LocalDateTime.ofInstant(fromInstant, ZoneId.systemDefault());
        LocalDateTime toDateTime = LocalDateTime.ofInstant(toInstant, ZoneId.systemDefault());

        return new BaseResponse<>(Const.ResultCode.SUCCESS, reportService.getTopBuyer(fromDateTime, toDateTime));
    }

    @GetMapping("/revenue")
    public BaseResponse<List<RevenueStatDto>> getRevenueByDateRange(
            @RequestParam("from") String fromDate,
            @RequestParam("to") String toDate) {
        ZonedDateTime fromDateTime = ZonedDateTime.parse(fromDate);
        ZonedDateTime toDateTime = ZonedDateTime.parse(toDate);

        List<RevenueStatDto> revenueStats = reportService.getRevenueByDateRange(fromDateTime, toDateTime);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, revenueStats);
    }


    @GetMapping("/stocks")
    public BaseResponse<List<BookStockDto>> getStockBookByStore(@RequestParam("store_id") String storeId) {

        List<BookStockDto> revenueStats = reportService.getBookStock(storeId);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, revenueStats);
    }

}

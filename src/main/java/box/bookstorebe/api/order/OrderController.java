package box.bookstorebe.api.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.WebContentDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.order.CombinedOrderDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.model.order.UpdateOrderModel;
import box.bookstorebe.repository.common.webcontent.WebContentRepository;
import box.bookstorebe.service.auth.AuthService;
import box.bookstorebe.service.order.OrderService;
import box.bookstorebe.service.order.PaymentService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final WebContentRepository webContentRepository;
    @Value("${app.client.url}")
    private String checkoutUrl;
    @Value("${app.domain.url}")
    private String serverUrl;

    @PostMapping
    public BaseResponse<?> createOrder(@RequestBody @Valid CreateOrderModel orderModel, HttpServletRequest request) throws BizException, MessagingException {
        Object result = orderService.createOrder(request, orderModel, serverUrl);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, result);
    }

    @PostMapping("/combine")
    public BaseResponse<?> createOrderV2(@RequestBody @Valid CreateOrderModel orderModel, HttpServletRequest request) throws BizException, MessagingException {
        WebContentDocument webContentDocument = webContentRepository.findByKey(Const.BackendDomain);
        Object result = orderService.createOrderV2(request, orderModel, webContentDocument.getValue());
        return new BaseResponse<>(Const.ResultCode.SUCCESS, result);
    }

    @GetMapping("/combine")
    public BaseResponse<List<CombinedOrderDto>> getListCombinedOder() throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.getListCombinedOrder());
    }

    @PostMapping("/combine/fee")
    public BaseResponse<BigDecimal> calculateCombinedOrderFee(@RequestBody @Valid CreateOrderModel orderModel) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.calculateCombinedOrderFee(orderModel));
    }

    @GetMapping()
    public BasePagingResponse<OrderDto> getOrders(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "payment_type", required = false) String paymentType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "start_at", required = false) String startAt,
            @RequestParam(name = "end_at", required = false) String endAt,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) throws BizException {
        return new BasePagingResponse<>(orderService.getOrders(type, q, id, paymentType, status, startAt, endAt, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<OrderDto> getDetailOrder(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.findById(id));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse<OrderDto> searchOrderResult(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.findByOrderCode(id));
    }


    @GetMapping("/repayment/{id}")
    public BaseResponse<String> retryPayment(@PathVariable String id, HttpServletRequest request) throws BizException, MessagingException {
        String url = orderService.retryPayment(id, serverUrl, request);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, url);
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateOrder(@PathVariable String id, @RequestBody @Valid UpdateOrderModel order) throws BizException {
        orderService.updateOrder(id, order);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book successfully");
    }

    @PostMapping("/payment")
    public String createPayment(@RequestParam("amount") int orderTotal,
                                @RequestParam("orderInfo") String orderInfo,
                                HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = paymentService.createOrder(request, new BigDecimal(orderTotal), orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/payment")
    public RedirectView getPayment(HttpServletRequest request, Model model) throws BizException, MessagingException {
        int paymentStatus = paymentService.orderReturn(request);
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String transactionId = request.getParameter("vnp_TransactionNo");
//        String totalPrice = request.getParameter("vnp_Amount");
        if (paymentStatus == 1) {
            paymentService.createPayment(orderInfo, transactionId);
        }
        WebContentDocument webContentDocument = webContentRepository.findByKey(Const.UserDomain);
        if (webContentDocument == null) throw new BizException("missing config client domain");
        return new RedirectView(webContentDocument.getValue() + "/order-result?orderId=" + orderInfo);
    }

}

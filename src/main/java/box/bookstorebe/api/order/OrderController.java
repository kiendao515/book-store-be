package box.bookstorebe.api.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.book.book.UpdateBookModel;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.UpdateOrderModel;
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

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PaymentService paymentService;
    @Value("${app.client.url}")
    private String checkoutUrl;
    @Value("${app.domain.url}")
    private String serverUrl;

    @PostMapping
    public BaseResponse<String> createOrder(@RequestBody @Valid CreateOrderModel orderModel,HttpServletRequest request) throws BizException, MessagingException {
        String paymentUrl = orderService.createOrder(request,orderModel,serverUrl);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, paymentUrl);
    }

    @GetMapping()
    public BasePagingResponse<OrderDto> getOrders(
            @RequestParam(name = "customer_phone", required = false) String customerPhone,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "payment_type", required = false) String paymentType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endAt,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) throws BizException {
        return new BasePagingResponse<>(orderService.getOrders(customerPhone,id,paymentType,status,startAt,endAt,page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<OrderDto> getDetailOrder(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.findById(id));
    }

    @GetMapping("/detail/{id}")
    public BaseResponse<OrderDto> searchOrderResult(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.findById(id));
    }

    @GetMapping("/test")
    public BaseResponse<OrderDto> test() throws BizException, MessagingException {
        paymentService.createPayment(1000,"66a762fe6f1dde243174f560","2101902828922","abbdbđ");
        return new BaseResponse<>(Const.ResultCode.SUCCESS,"oke");
    }

    @GetMapping("/repayment/{id}")
    public BaseResponse<String> retryPayment(@PathVariable String id,HttpServletRequest request) throws BizException, MessagingException {
        String url = orderService.retryPayment(id,serverUrl,request);
        return new BaseResponse<>(Const.ResultCode.SUCCESS,url);
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateOrder(@PathVariable String id, @RequestBody @Valid UpdateOrderModel order) throws BizException {
        orderService.updateOrder(id, order);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update book successfully");
    }

    @PostMapping("/payment")
    public String createPayment(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request){
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = paymentService.createOrder(request,orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/payment")
    public RedirectView getPayment(HttpServletRequest request, Model model) throws BizException, MessagingException {
        int paymentStatus =paymentService.orderReturn(request);
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        if(paymentStatus == 1){
            paymentService.createPayment(Integer.parseInt(totalPrice),orderInfo,paymentTime,transactionId);
        }
        return new RedirectView(checkoutUrl+"/checkout?orderId="+orderInfo);
    }

}

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping
    public BaseResponse<String> createOrder(@RequestBody @Valid CreateOrderModel orderModel) throws BizException {
        orderService.createOrder(orderModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create order successfully");
    }

    @GetMapping()
    public BasePagingResponse<OrderDto> getOrders(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) throws BizException {
        return new BasePagingResponse<>(orderService.getOrders(page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<OrderDto> getDetailOrder(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, orderService.findById(id));
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
        String vnpayUrl = paymentService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/payment")
    public String getPayment(HttpServletRequest request, Model model){
        int paymentStatus =paymentService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus == 1 ? "Payment successfully!" : "Payment fail!";
    }

}

package box.bookstorebe.scheduler;

import box.bookstorebe.client.CommonClient;
import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.common.SystemConfigDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.dto.ghtk.GhtkOrderDetailDto;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.common.systemconfig.SystemConfigRepository;
import box.bookstorebe.repository.order.OrderItemRepository;
import box.bookstorebe.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Component
@AllArgsConstructor
@Slf4j
public class HandleOrderStateSchedule {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookInventoryRepository bookInventoryRepository;
    private final SystemConfigRepository systemConfigRepository;
    private static final int MAX_CONCURRENT_REQUESTS = 10; // Giới hạn số yêu cầu đồng thời
    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_REQUESTS);
    private final CommonClient commonClient;

    @Scheduled(fixedDelay = 60 * 1000L)
    @SchedulerLock(name = "handleOrderState", lockAtLeastFor = "1M", lockAtMostFor = "10M")
    @Transactional
    public void handleOrderState() {
        log.info("[Handle Order State] Job started...");
        try {
            int cancelOrderDuration = getCancelOrderDuration();
            ZonedDateTime now = ZonedDateTime.now();
            List<OrderDocument> orders = orderRepository.findAllByCreatedAtBetweenAndStatusIs(
                    now.minusHours(cancelOrderDuration + 24),
                    now.minusHours(cancelOrderDuration),
                    Const.OrderStatus.CREATED
            );

            if (orders.isEmpty()) {
                log.info("[Handle Order State] No orders to cancel.");
                return;
            }
            updateOrderAndInventory(orders);

            log.info("[Handle Order State] Successfully handled {} orders.", orders.size());
        } catch (Exception e) {
            log.error("[Handle Order State] An error occurred: {}", e.getMessage(), e);
        }
    }

    private int getCancelOrderDuration() {
        final int defaultDuration = 6;
        SystemConfigDocument cancelOrderDurationConfig = systemConfigRepository.findByKey(Const.SystemConfig.CANCEL_ORDER_DURATION);
        if (cancelOrderDurationConfig != null && cancelOrderDurationConfig.getValue() != null) {
            try {
                return Integer.parseInt(cancelOrderDurationConfig.getValue());
            } catch (NumberFormatException e) {
                log.warn("[Handle Order State] Invalid CANCEL_ORDER_DURATION value, using default: {} hours", defaultDuration);
            }
        }
        return defaultDuration;
    }

    private void updateOrderAndInventory(List<OrderDocument> orders) {
        for (OrderDocument order : orders) {
            List<OrderItemDocument> orderItems = orderItemRepository.findAllByOrderId(order.getId());

            for (OrderItemDocument orderItem : orderItems) {
                BookInventory bookInventory = bookInventoryRepository.findById(orderItem.getBookInventoryId())
                        .orElseThrow(() -> new IllegalStateException("BookInventory not found: " + orderItem.getBookInventoryId()));

                bookInventory.setQuantity(bookInventory.getQuantity() + orderItem.getQuantity());
                bookInventoryRepository.save(bookInventory);
            }
            order.setStatus(Const.OrderStatus.CANCEL);
            orderRepository.save(order);
        }
    }
    @Scheduled(fixedRate = 30 * 60 * 1000L)
    @SchedulerLock(name = "updateShippingStatus", lockAtLeastFor = "1M", lockAtMostFor = "10M")
    @Transactional
    public void updateShippingStatus() {
        log.info("[Update Shipping Status] Job running ...");
        List<OrderDocument> orders = orderRepository.findAllByStatusIn(List.of(
                Const.OrderStatus.READY_TO_SHIP,
                Const.OrderStatus.SHIPPING
        ));

        if (orders.isEmpty()) {
            log.info("[Update Shipping Status] No orders to process.");
            return;
        }

        orders.forEach(order -> {
            try {
                semaphore.acquire();
                CompletableFuture.runAsync(() -> processOrder(order))
                        .whenComplete((result, error) -> semaphore.release());
            } catch (InterruptedException e) {
                log.error("Failed to acquire semaphore for order '{}': {}", order.getId(), e.getMessage());
            }
        });

        log.info("[Update Shipping Status] Job done");
    }

    private void processOrder(OrderDocument order) {
        try {
            GhtkOrderDetailDto orderDetailDto = commonClient.getOrderStatus(order.getShippingCode());
            if (orderDetailDto!= null) {
                updateOrderStatus(order, orderDetailDto.getOrder().getStatus());
            } else {
                log.warn("Failed to get status for order '{}'", order.getId());
            }
        } catch (Exception e) {
            log.error("Error while updating order '{}' status: {}", order.getId(), e.getMessage());
        }
    }

    private void updateOrderStatus(OrderDocument order, Integer newStatus) {
        switch (newStatus) {
            case -1:
                order.setStatus(Const.OrderStatus.CANCEL);
                break;
            case 1:
                order.setStatus(Const.OrderStatus.READY_TO_SHIP);
                break;
            case 2,3:
                order.setStatus(Const.OrderStatus.SHIPPING);
                break;
            case 5,6,11:
                order.setStatus(Const.OrderStatus.DONE);
                break;
            default:
                log.warn("Unknown status '{}' for order '{}'", newStatus, order.getId());
        }
        orderRepository.save(order);
    }
}


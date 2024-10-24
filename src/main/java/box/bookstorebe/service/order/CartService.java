package box.bookstorebe.service.order;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.order.CartDocument;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateCartModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.order.CartRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.book.BookInventoryService;
import box.bookstorebe.service.book.BookService;
import box.bookstorebe.service.common.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
@Service
@Slf4j
public class CartService extends BaseService {
    private final CartRepository cartRepository;
    private final AccountRepository accountRepository;
    private final BookInventoryRepository bookRealityRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final BookInventoryService bookRealityService;

    public void saveCart(CreateCartModel cartModel) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument user = accountRepository.findById(currentUser.getUserId()).orElseThrow(() -> new BizException("Invalid user"));
        CartDocument cartDocument = new CartDocument();
        cartDocument.setQuantity(cartModel.getQuantity());
        cartDocument.setBookInventoryId(cartModel.getBookInventoryId());
        cartDocument.setUserId(user.getId());
        cartRepository.save(cartDocument);
    }

}

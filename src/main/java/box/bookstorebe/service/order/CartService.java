package box.bookstorebe.service.order;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.order.CartDocument;
import box.bookstorebe.dto.order.CartDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateCartModel;
import box.bookstorebe.repository.book.BookInventoryRepository;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.order.CartRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.book.BookInventoryService;
import box.bookstorebe.service.book.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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

    public void saveCart(CreateCartModel cartModel) throws BizException{
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid account id"));
        BookInventory bookInventory = bookRealityRepository.findById(cartModel.getBookInventoryId()).orElseThrow(() -> new BizException("Invalid book inventory id"));
        CartDocument cartDocument = cartRepository.findByAccountIdAndBookInventoryId(user.getId(), bookInventory.getId());
        if(cartDocument == null) {throw new BizException("Invalid cart");}
        if(cartModel.isDelete()){
            cartRepository.delete(cartDocument);
            return;
        }
        if(bookInventory.getQuantity() < (cartDocument.getQuantity() + cartModel.getQuantity())) {
            throw new BizException("Số lượng vượt quá tồn kho");
        }
        cartDocument.setQuantity(cartDocument.getQuantity() + cartModel.getQuantity());
        cartRepository.save(cartDocument);
    }

    public void addToCart(CreateCartModel cartModel) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }

        AccountDocument user = accountRepository.findById(currentUser.getAccountId())
                .orElseThrow(() -> new BizException("Invalid account id"));

        BookInventory bookInventory = bookRealityRepository.findById(cartModel.getBookInventoryId())
                .orElseThrow(() -> new BizException("Invalid book inventory id"));

        List<BookInventory> relatedInventories = bookRealityRepository.findAllByRelatedBookId(bookInventory.getId());

        relatedInventories.add(bookInventory);

        int totalQuantity = relatedInventories.stream()
                .mapToInt(BookInventory::getQuantity)
                .sum();

        if (totalQuantity < cartModel.getQuantity()) {
            throw new BizException("Số lượng vượt quá tồn kho");
        }

        CartDocument cartDocument = cartRepository.findByAccountIdAndBookInventoryId(currentUser.getAccountId(), bookInventory.getId());
        if (cartDocument == null) {
            cartDocument = new CartDocument();
            cartDocument.setQuantity(cartModel.getQuantity());
        } else {
            cartDocument.setQuantity(cartDocument.getQuantity() + cartModel.getQuantity());
        }

        cartDocument.setBookInventoryId(cartModel.getBookInventoryId());
        cartDocument.setAccountId(user.getId());
        cartRepository.save(cartDocument);
    }


    public List<CartDto> getCarts() throws BizException {
        List<CartDto> cartDtoList = new ArrayList<>();
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument acc = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid account id"));
        List<CartDocument> cartDocument = cartRepository.findAllByAccountId(acc.getId());
        if (cartDocument == null) {
            return cartDtoList;
        }
        cartDocument.forEach(cart -> {
            BookInventory bookInventory;
            try {
                bookInventory = bookRealityRepository.findById(cart.getBookInventoryId()).orElseThrow(() -> new BizException("Invalid book inventory id"));
                BookDocument bookDocument = bookRepository.findById(bookInventory.getBookId()).orElseThrow(() -> new BizException("Invalid book id"));
                CartDto cartDto = new CartDto();
                cartDto.setQuantity(cart.getQuantity());
                cartDto.setBook(bookDocument);
                cartDto.setId(cart.getId());
                cartDto.setType(bookInventory.getType());
                cartDto.setPrice(bookInventory.getPrice());
                cartDto.setBookInventoryId(bookInventory.getId());
                cartDtoList.add(cartDto);
            } catch (BizException e) {
                throw new RuntimeException(e);
            }
        });

        return cartDtoList;
    }

}

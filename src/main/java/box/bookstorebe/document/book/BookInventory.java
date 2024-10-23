package box.bookstorebe.document.book;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("book_inventory")
public class BookInventory {
    @Id
    private String id;

    @Field("book_information_id")
    @Indexed
    private String bookId;

    @Field("price")
    private BigDecimal price;

    @Field("location")
    private String location; // vị trí sách ở Hộp

    @Field("quantity")
    private Integer quantity;

    @Field("type")
    private BookType type; // NEW, LIKE NEW, OLD

    @Field("store_id")
    private String storeId;

    @Field("is_own_store")
    private boolean isOwnStore; // xac định xem sách đuoc lưu trữ ở hộp

    @Field("cover_image_id")
    private String coverImageId;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;
}

package box.bookstorebe.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BasePagingResponse<T> {
    private boolean success;
    private List<T> data;
    private int page;

    private int size;

    @Field("total_elements")
    private long totalElements;

    private int totalPages;

    public BasePagingResponse(Page<T> pageTemp) {
        if (pageTemp != null) {
            this.success = true;
            this.data = pageTemp.getContent();
            this.page = pageTemp.getNumber();
            this.size = pageTemp.getSize();
            this.totalElements = pageTemp.getTotalElements();
            this.totalPages = pageTemp.getTotalPages();
        }
    }
}

package box.bookstorebe.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDecode {
    private String accountId;
    private String role;
    private String email;
}

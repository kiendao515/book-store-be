package box.bookstorebe.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ClientProperty {
    @Value("${app.client.admin}")
    private String address;
}

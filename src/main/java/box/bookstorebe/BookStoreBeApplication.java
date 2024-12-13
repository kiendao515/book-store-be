package box.bookstorebe;

import jakarta.annotation.PostConstruct;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableSchedulerLock(defaultLockAtMostFor = "PT2M")
@EnableScheduling
public class BookStoreBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreBeApplication.class, args);
    }
    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        System.out.println("Múi giờ hiện tại: " + TimeZone.getDefault());
    }

}

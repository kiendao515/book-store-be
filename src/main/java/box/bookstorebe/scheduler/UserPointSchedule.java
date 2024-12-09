package box.bookstorebe.scheduler;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.user.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class UserPointSchedule {


    private final CustomerRepository userRepository;
    private final AccountRepository accountRepository;

    @Scheduled(cron = "0 0 8 * * ?", zone = "UTC")
    @SchedulerLock(name = "calculateUserPoint", lockAtLeastFor = "10M", lockAtMostFor = "20M")
    @Transactional
    public void calculateUserPoint() {
        log.info("[Calculate User Point] Job running ...");
        int MONTH_TO_REWARD = 3;
        int ADDITIONAL_POINT = 50;
        ZonedDateTime now = ZonedDateTime.now();
        List<AccountDocument> accountDocuments = accountRepository.findAllByCreatedAtBeforeAndEnabledIs(now.minusMonths(MONTH_TO_REWARD), 1);
        List<String> listAccIds = accountDocuments.stream().map(AccountDocument::getId).toList();
        List<CustomerDocument> users = userRepository.findAllByReceivedAwardIsNotNullAndPhoneNumberIsNotNullAndAccountIdIn(listAccIds);
        for(CustomerDocument user : users) {
            user.setPoint(user.getPoint() + ADDITIONAL_POINT);
            user.setReceivedAward(1);
            user.setUpdatedAt(ZonedDateTime.now());
        }
        userRepository.saveAll(users);
        log.info("[Calculate User Point] Job done");
    }
}

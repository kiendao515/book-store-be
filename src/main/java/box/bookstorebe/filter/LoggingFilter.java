package box.bookstorebe.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LoggingFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}

package box.bookstorebe.configuration;

import box.bookstorebe.common.Const;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeFormatConfiguration implements WebMvcConfigurer {
    public static class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

        private final DateTimeFormatter formatter;

        public ZonedDateTimeConverter(ZoneId zoneId) {
            // set the zone in the formatter
            this.formatter = DateTimeFormatter.ofPattern(Const.DateTime.DATETIME_FORMAT).withZone(zoneId);
        }

        @Override
        public ZonedDateTime convert(String source) {
            // now the formatter has a zone set, so I can parse directly to ZonedDateTime
            return ZonedDateTime.parse(source, this.formatter);
        }
    }

    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        registry.addConverter(new ZonedDateTimeConverter(ZoneId.of(Const.DateTime.TIME_ZONE)));
    }

}

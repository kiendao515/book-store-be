package box.bookstorebe.util;


import box.bookstorebe.common.Const;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.util.Pair;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {
    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZoneId.of(Const.DateTime.TIME_ZONE));
    }

    public static ZonedDateTime parseDateTime(String dateTime, String format) {
        LocalDateTime ldt = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
        return ldt.atZone(ZoneId.of(Const.DateTime.TIME_ZONE));
    }

    public static ZonedDateTime parseDate(String date, String format) {
        LocalDate ldt = LocalDate.parse(date, DateTimeFormatter.ofPattern(format));
        return ldt.atStartOfDay(ZoneId.of(Const.DateTime.TIME_ZONE));
    }

    public static ZonedDateTime parseTimestamp(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);
        return ZonedDateTime.of(localDateTime, ZoneId.of(Const.DateTime.TIME_ZONE));
    }

    public static ZonedDateTime parseTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(Const.DateTime.TIME_ZONE));
        return localDateTime.atZone(ZoneId.of(Const.DateTime.TIME_ZONE));
    }

    public static String parseTimeToString(ZonedDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    public static ZonedDateTime startOfDay(ZonedDateTime time) {
        return time.truncatedTo(ChronoUnit.DAYS);
    }

    public static ZonedDateTime endOfDay(ZonedDateTime time) {
        return time.truncatedTo(ChronoUnit.DAYS).plusDays(1).minusSeconds(1);
    }

    public static Date startOfMonth(LocalDate today) {
        LocalDate startDateOfMonth = today.withDayOfMonth(1);
        return startOfDay(startDateOfMonth);
    }

    public static Date endOfMonth(LocalDate today) {
        LocalDate endDateOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        return endOfDay(endDateOfMonth);
    }

    public static long secondDiffTime(ZonedDateTime from, ZonedDateTime to) {
        return Duration.between(from, to).getSeconds();
    }

    public static Date startOfDay(LocalDate localDate) {
        ZonedDateTime startOfDay = localDate.atStartOfDay(ZoneId.of(Const.DateTime.TIME_ZONE));

        return Date.from(startOfDay.toInstant());
    }

    public static Date endOfDay(LocalDate localDate) {
        ZonedDateTime endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.of(Const.DateTime.TIME_ZONE));

        return Date.from(endOfDay.toInstant());
    }

    public static boolean isDateValid(String date, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String diffTimeToString(ZonedDateTime time1, ZonedDateTime time2) {
        if (time1 == null || time2 == null) return "";
        Duration duration = Duration.between(time1, time2);
        StringBuilder durationAsStringBuilder = new StringBuilder();
        durationAsStringBuilder.append(' ');
        if (duration.toDays() > 0) {
            durationAsStringBuilder.append(duration.toDays());
            durationAsStringBuilder.append(" ngày");
        }

        duration = duration.minusDays(duration.toDays());
        long hours = duration.toHours();
        if (hours > 0) {
            String prefix = durationAsStringBuilder.isEmpty() ? "" : ", ";
            durationAsStringBuilder.append(prefix);
            durationAsStringBuilder.append(hours);
            durationAsStringBuilder.append(" giờ");
        }

        duration = duration.minusHours(duration.toHours());
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            String prefix = durationAsStringBuilder.isEmpty() ? "" : ", ";
            durationAsStringBuilder.append(prefix);
            durationAsStringBuilder.append(minutes);
            durationAsStringBuilder.append(" phút");
        }
        if (!durationAsStringBuilder.isEmpty()) {
            durationAsStringBuilder.append(" trước");
        }
        return durationAsStringBuilder.toString();

    }

    public static String shortDiffTimeToString(ZonedDateTime time1, ZonedDateTime time2) {
        if (time1 == null || time2 == null) return "";
        Duration duration = Duration.between(time1, time2);
        StringBuilder durationAsStringBuilder = new StringBuilder();
        if (duration.toDays() > 0) {
            durationAsStringBuilder.append(duration.toDays());
            durationAsStringBuilder.append('d');
        }

        duration = duration.minusDays(duration.toDays());
        long hours = duration.toHours();
        if (hours > 0) {
            durationAsStringBuilder.append(hours);
            durationAsStringBuilder.append('h');
        }

        duration = duration.minusHours(duration.toHours());
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            durationAsStringBuilder.append(minutes);
            durationAsStringBuilder.append('m');
        }
        return durationAsStringBuilder.toString();

    }

    public static long convertTimeToSecond(String timeUnitString, int timeValue) {
        timeUnitString = timeUnitString + "S";
        if (timeUnitString.endsWith("SS")) timeUnitString = timeUnitString.replaceAll("SS", "S");
        TimeUnit timeUnit = TimeUnit.of(ChronoUnit.valueOf(timeUnitString));
        return timeUnit.toSeconds(timeValue);
    }

    public static Date convertTimestampToDate(long timestamp) {
        Timestamp stamp = new Timestamp(timestamp);
        return new Date(stamp.getTime());
    }

    public static ZonedDateTime convertObjectIdToZonedDateTime(ObjectId objectId) {
        return Instant.ofEpochSecond(objectId.getTimestamp()).atZone(ZoneId.of("UTC"));
    }

    public static long getSyncLogTimestamp(ZonedDateTime time) {
        return time.withZoneSameInstant(ZoneId.of("UTC")).toEpochSecond();
    }

    public static Pair<ZonedDateTime, ZonedDateTime> getStartEndTime(String start, String end) throws ParseException {
        return getStartEndTime(start, end, "dd/MM/yyyy HH:mm:ss");
    }

    public static Pair<ZonedDateTime, ZonedDateTime> getStartEndTime(String start, String end, String format) throws ParseException {
        if (StringUtils.isEmpty(start) || StringUtils.isEmpty(end)) {
            return Pair.of(ZonedDateTime.now().minusDays(1), ZonedDateTime.now());
        }
        Date formDate = new SimpleDateFormat(format).parse(start);
        Date toDate = new SimpleDateFormat(format).parse(end);
        return Pair.of(
                ZonedDateTime.ofInstant(formDate.toInstant(),
                        ZoneId.systemDefault()),
                ZonedDateTime.ofInstant(toDate.toInstant(),
                        ZoneId.systemDefault())
        );
    }

}

package cn.kanejin.webop.core.def;

import cn.kanejin.commons.util.NumberUtils;

import java.util.concurrent.TimeUnit;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
 * @author Kane Jin
 */
public class CacheExpiryDef {
    private final String type;
    private final TimeUnit unit;
    private final Long time;

    public CacheExpiryDef(String type, String timeString) {
        this.type = type;
        this.unit = parseTimeUnit(timeString);
        this.time = parseTime(timeString);
    }

    public String getType() {
        return type;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public Long getTime() {
        return time;
    }

    private Long parseTime(String timeString) {
        String time = timeString;
        if (isNotBlank(timeString)) {
            if (timeString.endsWith("ms")) {
                time = timeString.substring(0, timeString.length()-2);
            } else if (timeString.endsWith("s")) {
                time = timeString.substring(0, timeString.length()-1);
            } else if (timeString.endsWith("m")) {
                time = timeString.substring(0, timeString.length()-1);
            } else if (timeString.endsWith("h")) {
                time = timeString.substring(0, timeString.length()-1);
            } else if (timeString.endsWith("d")) {
                time = timeString.substring(0, timeString.length()-1);
            }
        }

        return NumberUtils.toLong(time, 5L);
    }

    private TimeUnit parseTimeUnit(String timeString) {
        if (isNotBlank(timeString)) {
            if (timeString.endsWith("ms")) {
                return TimeUnit.MILLISECONDS;
            } else if (timeString.endsWith("s")) {
                return TimeUnit.SECONDS;
            } else if (timeString.endsWith("m")) {
                return TimeUnit.MINUTES;
            } else if (timeString.endsWith("h")) {
                return TimeUnit.HOURS;
            } else if (timeString.endsWith("d")) {
                return TimeUnit.DAYS;
            }
        }

        return TimeUnit.SECONDS;
    }
}

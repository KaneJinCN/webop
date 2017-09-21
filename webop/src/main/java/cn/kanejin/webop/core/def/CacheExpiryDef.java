package cn.kanejin.webop.core.def;

import java.util.concurrent.TimeUnit;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
 * @author Kane Jin
 */
public class CacheExpiryDef {
    private final String type;
    private final TimeUnit unit;
    private final Long time;

    public CacheExpiryDef(String type, String unitName, Long time) {
        this.type = type;
        this.unit = parseTimeUnit(unitName);
        this.time = time;
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

    private TimeUnit parseTimeUnit(String unitName) {
        if (isNotBlank(unitName)) {
            if (unitName.equals("millis")) {
                return TimeUnit.MILLISECONDS;
            } else if (unitName.equals("seconds")) {
                return TimeUnit.SECONDS;
            } else if (unitName.equals("minutes")) {
                return TimeUnit.MINUTES;
            } else if (unitName.equals("hours")) {
                return TimeUnit.HOURS;
            } else if (unitName.equals("days")) {
                return TimeUnit.DAYS;
            }
        }

        return TimeUnit.SECONDS;
    }
}

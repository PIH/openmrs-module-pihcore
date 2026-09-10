package org.openmrs.module.pihcore.rest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The parts the audit endpoints in this package have in common: reading a date bound off a request
 * parameter, and shaping an error the same way. Kept together so the two endpoints cannot drift
 * apart on what `endDate=2026-09-30` means.
 */
final class AuditRestSupport {

    private AuditRestSupport() {
    }

    /**
     * Reads one end of a date range, in any of the formats the REST API accepts elsewhere.
     *
     * <p>A bare date names the whole of that day: `endDate=2026-09-30` means through the end of the
     * 30th, not its first instant, since a range given in dates is asking about days. Give a time
     * to bound the range to the second instead.
     *
     * @param value the parameter as it arrived, or null or blank for no bound
     * @param isUpperBound whether a date without a time should be stretched to the end of the day
     * @return the bound, or null if none was given
     */
    static Date parseBound(String value, boolean isUpperBound) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Date date = (Date) ConversionUtil.convert(value.trim(), Date.class);
        if (!isUpperBound || !isDateOnly(value.trim())) {
            return date;
        }

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.setTime(date);
        endOfDay.set(Calendar.HOUR_OF_DAY, 23);
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);
        return endOfDay.getTime();
    }

    private static boolean isDateOnly(String value) {
        return value.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    static String dateFormatMessage() {
        return "startDate and endDate must be ISO 8601, e.g. 2026-09-01 or 2026-09-01T13:45:00.000+0000";
    }

    static ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errorMessages", Collections.singletonList(message));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    static ResponseEntity<Map<String, Object>> errorResponse(Throwable t) {
        Map<String, Object> data = new LinkedHashMap<>();
        List<String> errorMessages = new ArrayList<>();
        while (t != null && !errorMessages.contains(t.getMessage())) {
            errorMessages.add(t.getMessage());
            t = t.getCause();
        }
        data.put("errorMessages", errorMessages);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }
}

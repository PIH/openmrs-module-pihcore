package org.openmrs.module.pihcore.status;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class StatusDataFunctions {

    private static VelocityEngine velocityEngine;

    public static synchronized VelocityEngine getVelocityEngine() {
        if (velocityEngine == null) {
            velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.CommonsLogLogChute");
            velocityEngine.setProperty(CommonsLogLogChute.LOGCHUTE_COMMONS_LOG_NAME, "pihcore_velocity");
            velocityEngine.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, "org.apache.velocity.util.introspection.SecureUberspector");
            try {
                velocityEngine.init();
            } catch (Exception e) {
                throw new RuntimeException("An error occurred initializing the Velocity Engine", e);
            }
        }
        return velocityEngine;
    }

    public static VelocityContext getVelocityContext() {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("fn", new StatusDataFunctions());
        return velocityContext;
    }

    public static VelocityContext getVelocityContext(Map<String, Object> dataValues) {
        VelocityContext velocityContext = getVelocityContext();
        for (Map.Entry<String, Object> col : dataValues.entrySet()) {
            if (velocityContext.containsKey(col.getKey())) {
                throw new IllegalArgumentException(col.getKey() + " is a reserved key in the velocity context");
            }
            velocityContext.put(col.getKey(), col.getValue());
        }
        return velocityContext;
    }

    public static String evaluateExpression(VelocityContext context, String expression) {
        StringWriter writer = new StringWriter();
        try {
            getVelocityEngine().evaluate(context, writer, StatusDataFunctions.class.getName(), expression);
            return writer.toString();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to evaluate velocity expression: " + expression);
        }
    }

    public static String evaluateExpression(Map<String, Object> dataValues, String expression) {
        return evaluateExpression(getVelocityContext(dataValues), expression);
    }

    public StatusDataFunctions() {}

    public Locale locale() { return Context.getLocale(); }

    public String translate(String code) {
        return Context.getMessageSourceService().getMessage(code);
    }

    public ObjectFormatter formatter() {
        return new ObjectFormatter();
    }

    public String format(Object obj) {
        return ObjectUtil.format(obj);
    }

    public SimpleDateFormat dateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public String formatDate(Date date, String format) {
        return (date == null ? "" : dateFormat(format).format(date));
    }

    public String formatDate(Date date) {
        return formatDate(date, "dd MMM yyyy");
    }

    public Date now() {
        return new Date();
    }

    public DateTime startOfToday() {
        return new DateTime().withTimeAtStartOfDay();
    }

    public DateTime startOfDay(Date date) {
        return new DateTime(date).withTimeAtStartOfDay();
    }

    public int yearsSince(Date date) {
        return Years.yearsBetween(startOfDay(date), startOfToday()).getYears();
    }

    public int monthsSince(Date date) {
        return Months.monthsBetween(startOfDay(date), startOfToday()).getMonths();
    }

    public int daysSince(Date date) {
        return Days.daysBetween(startOfDay(date), startOfToday()).getDays();
    }

    public int monthsUntil(Date date) {
        return Months.monthsBetween(startOfToday(), startOfDay(date)).getMonths();
    }

    public int daysUntil(Date date) {
        return Days.daysBetween(startOfToday(), startOfDay(date)).getDays();
    }

    public Concept concept(Number conceptId) {
        if (conceptId == null) {
            return null;
        }
        return Context.getConceptService().getConcept(conceptId.intValue());
    }

}

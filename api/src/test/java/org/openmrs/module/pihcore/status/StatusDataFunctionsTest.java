package org.openmrs.module.pihcore.status;

import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StatusDataFunctionsTest {

    @Test
    public void testVelocityExpressions() {
        Map<String, Object> dataValues = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String today = df.format(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 3);
        dataValues.put("in3Years", cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 52);
        dataValues.put("in52Days", cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        dataValues.put("yesterday", cal.getTime());
        String yesterday = df.format(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        dataValues.put("lastWeek", cal.getTime());
        String lastWeek = df.format(cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        dataValues.put("lastMonth", cal.getTime());

        cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        dataValues.put("lastYear", cal.getTime());

        testExpression(dataValues, yesterday, "$fn.dateFormat('yyyy-MM-dd').format($yesterday)");
        testExpression(dataValues, lastWeek, "$fn.formatDate($lastWeek, 'yyyy-MM-dd')");
        testExpression(dataValues, today, "$fn.formatDate($fn.now(), 'yyyy-MM-dd')");
        testExpression(dataValues, "0", "$fn.monthsSince($lastWeek)");
        testExpression(dataValues, "1", "$fn.monthsSince($lastMonth)");
        testExpression(dataValues, "12", "$fn.monthsSince($lastYear)");
        testExpression(dataValues, "0", "$fn.daysSince($fn.now())");
        testExpression(dataValues, "1", "$fn.daysSince($yesterday)");
        testExpression(dataValues, "36", "$fn.monthsUntil($in3Years)");
        testExpression(dataValues, "52", "$fn.daysUntil($in52Days)");

        dataValues.put("cd4", 300.0);
        testExpression(dataValues, "lowCd4", "#if($cd4 < 350)lowCd4#{else}#end");
    }

    public void testExpression(Map<String, Object> dataValues, String expected, String expression) {
        String actual = StatusDataFunctions.evaluateExpression(dataValues, expression);
        assertThat(expected, is(actual));
    }
}

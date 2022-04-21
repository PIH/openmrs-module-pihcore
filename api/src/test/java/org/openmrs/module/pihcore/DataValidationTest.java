package org.openmrs.module.pihcore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.pihcore.encounter.PihEncounterValidator;
import org.openmrs.test.SkipBaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This is an integration test that connects to an existing database and runs all validators against it.
 *
 * To execute this against an appropriate database, ensure you have a properties file with the following settings:
 *  -  connection.url, connection.username, connection.password properties that point to a valid DB connection
 *  -  junit.username and junit.password properties that point to a valid OpenMRS account
 *  -  batchSize if you wish to alter from the default of 1000
 *  -  maxToCheck if you wish to limit the maximum number of records to check for a given Validator/Object type
 *  -  errorLog and summaryLog that point to file locations where you want to output the log files
 *
 *  Configure a system property named "validation.properties" with the location of this properties file.
 *  If you are running this test from the command line, that can be done with a -D argument like this:
 *
 *  mvn test -Dtest=DataValidationTest -Dvalidation.properties=/tmp/validation.properties
 */
@SkipBaseSetup
@Ignore
public class DataValidationTest extends PihCoreContextSensitiveTest {

    static Properties props = null;
    static int BATCH_SIZE = 1000;
    static int MAX_TO_CHECK = -1;
    static String OBJECTS_TO_INCLUDE = "";
    static String OBJECTS_TO_SKIP = "";

    static {
        String propFile = System.getProperty("validation.properties");
        if (propFile != null) {
            props = new Properties();
            try {
                props.load(new FileInputStream(propFile));
            }
            catch (Exception e) {
                System.out.println("Error loading properties from " + propFile + ": " + e.getMessage());
            }
        }

        if (props != null && !props.isEmpty()) {
            System.setProperty("databaseUrl", props.getProperty("connection.url"));
            System.setProperty("databaseUsername", props.getProperty("connection.username"));
            System.setProperty("databasePassword", props.getProperty("connection.password"));
            System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
            System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
            System.setProperty("useInMemoryDatabase", "false");
            BATCH_SIZE = Integer.parseInt(props.getProperty("batchSize", "1000"));
            MAX_TO_CHECK = Integer.parseInt(props.getProperty("maxToCheck", "-1"));
            OBJECTS_TO_INCLUDE = props.getProperty("objectsToInclude", "");
            OBJECTS_TO_SKIP = props.getProperty("objectsToSkip", "");
        }
    }

    @Autowired
    private List<Validator> validators;

    @Autowired
    DbSessionFactory sessionFactory;

    @Override
    public Properties getRuntimeProperties() {
        Properties p = new Properties(props);
        p.putAll(super.getRuntimeProperties());
        return p;
    }

    @Test
    public void validateData() {

        // Only run this test if it is being run alone.  This ensures this test will not run in normal build.
        if (props == null) {
            System.out.println("Validation Test is not enabled");
            return;
        }

        if (!Context.isSessionOpen()) {
            Context.openSession();
        }
        authenticate();

        System.out.println("Found " + validators.size() + " validators");

        long totalValidationTime = 0;
        int totalValidationErrors = 0;
        long fullValidationTimeExpected = 0;

        String datetime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        File errorLog = new File(props.getProperty("errorLog", "validation_errors_" + datetime + ".log"));
        File summaryLog = new File(props.getProperty("summaryLog", "validation_summary_" + datetime + ".csv"));

        for (Validator v : validators) {
            System.out.println("------------------------");
            System.out.println("Found validator: " + v.getClass());
            Handler handlerAnnotation = v.getClass().getAnnotation(Handler.class);
            if (handlerAnnotation == null) {
                System.out.println("No handler annotation found");
            }
            else if (v instanceof PihEncounterValidator) {
                System.out.println(v.getClass() + " is not a real validator, skipping");
            }
            else {
                for (Class c : handlerAnnotation.supports()) {
                    System.out.println("Validator supports: " + c);
                    if (OpenmrsObject.class.isAssignableFrom(c)) {
                        if (OBJECTS_TO_SKIP.contains("[" + c.getName() + "]")) {
                            System.out.println(c.getName() + " configured to be skipped");
                        }
                        else if (StringUtils.isNotBlank(OBJECTS_TO_INCLUDE) && !OBJECTS_TO_INCLUDE.contains("[" + c.getName() + "]")) {
                            System.out.println(c.getName() + " not configured to be included");
                        }
                        else {
                            List rowCounts = sessionFactory.getCurrentSession().createCriteria(c).setProjection(Projections.rowCount()).list();
                            if (rowCounts == null || rowCounts.size() == 0) {
                                System.out.println("No mapped objects found");
                            } else if (rowCounts.size() > 1) {
                                System.out.println("Multiple objects match criteria, assuming there are more specific validators, skipping");
                            } else {
                                Long numRows = (Long) rowCounts.get(0);

                                System.out.println("OpenMRS Object. Count: " + numRows);

                                int numChecked = 0;
                                int numErrors = 0;
                                long totalTime = 0;
                                while (numChecked < numRows && (MAX_TO_CHECK < 0 || numChecked < MAX_TO_CHECK)) {
                                    long startMillis = System.currentTimeMillis();
                                    Criteria criteria = sessionFactory.getCurrentSession().createCriteria(c);
                                    criteria.setFirstResult(numChecked).setMaxResults(BATCH_SIZE);
                                    List l = criteria.list();
                                    for (Object o : l) {
                                        Errors errors = new BindException(o, "");
                                        boolean errorFound = false;
                                        try {
                                            v.validate(o, errors);
                                            if (errors.hasErrors()) {
                                                errorFound = true;
                                                logValidationError(o, errors, errorLog);
                                            }

                                        } catch (Exception e) {
                                            errorFound = true;
                                            logValidationError(o, e.getMessage(), errorLog);
                                        }
                                        numChecked++;
                                        if (errorFound) {
                                            numErrors++;
                                        }
                                    }
                                    Context.flushSession();
                                    Context.clearSession();
                                    long endMillis = System.currentTimeMillis();
                                    long duration = endMillis - startMillis;
                                    totalTime += duration;
                                    System.out.println(numChecked + "/" + numRows + ". Num Errors: " + numErrors + ". Duration: " + duration + ". Total Time: " + formatTime(totalTime));
                                }

                                long projectedTimeToValidateAll = numChecked > 0 ? (totalTime * numRows / numChecked) : 0L;
                                fullValidationTimeExpected += projectedTimeToValidateAll;

                                System.out.println("****************** " + c.getSimpleName());
                                System.out.println("****************** " + numChecked + " / " + numRows + " validated");
                                System.out.println("****************** " + numErrors + " / " + numChecked + " errors");
                                System.out.println("****************** " + formatTime(totalTime));
                                System.out.println("****************** " + formatTime(projectedTimeToValidateAll) + " to validate all");

                                System.out.println("Writing summary data to file: " + summaryLog);
                                try {
                                    StringBuilder summaryData = new StringBuilder();
                                    summaryData.append(v.getClass().getName()).append(",");
                                    summaryData.append(c.getName()).append(",");
                                    summaryData.append(numRows).append(",");
                                    summaryData.append(numChecked).append(",");
                                    summaryData.append(numErrors).append(",");
                                    summaryData.append(formatTime(totalTime)).append(",");
                                    summaryData.append(formatTime(projectedTimeToValidateAll)).append(",");
                                    summaryData.append(System.getProperty("line.separator"));
                                    FileUtils.writeStringToFile(summaryLog, summaryData.toString(), "UTF-8", true);
                                } catch (Exception e) {
                                }

                                totalValidationTime += totalTime;
                                totalValidationErrors += numErrors;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("****************** VALIDATION CHECKS COMPLETE");
        System.out.println("****************** " + totalValidationErrors + " errors found");
        System.out.println("****************** " + formatTime(totalValidationTime) + " elapsed time");
        System.out.println("****************** " + formatTime(fullValidationTimeExpected) + " projected time for a full validation");
    }

    String formatTime(long millis) {
        if (millis < 1000*60) {
            return (millis/1000) + " seconds";
        }
        else if (millis < 3600000) {
            int mins = (int)(millis/60000);
            long secs = (millis/1000) - (mins*60);
            return mins + "m " + secs + "s";
        }
        else {
            int hrs = (int)(millis/3600000);
            long mins = (millis/60000) - (hrs*60);
            return hrs + "h " + mins + "m";
        }
    }

    void logValidationError(Object o, Errors errors, File errorLog) {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError e : errors.getAllErrors()) {
            errorMessage.append(System.getProperty("line.separator")).append(e);
        }
        logValidationError(o, errorMessage.toString(), errorLog);
    }

    void logValidationError(Object o, String errorMessage, File errorLog) {
        try {
            System.out.println("Writing validation error to file: " + errorLog);
            FileUtils.writeStringToFile(errorLog, o.toString() + ",", "UTF-8", true);
            FileUtils.writeStringToFile(errorLog, errorMessage, "UTF-8", true);
            FileUtils.writeStringToFile(errorLog, System.getProperty("line.separator"), "UTF-8", true);
        }
        catch (Exception e) {}
        System.out.println("!!!!!!!!!!!!!!!!" + o + ": " + errorMessage);
    }
}

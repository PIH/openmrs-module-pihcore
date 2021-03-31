package org.openmrs.module.pihcore;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.junit.Test;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Properties;

/**
 * This is an integration test that connects to an existing database and runs all validators against it.
 * To execute this against an appropriate database:
 *  - Ensure you have a file at .OpenMRS/validation-runtime.properties
 *  - Ensure this has connection.url, connection.username, connection.password properties
 *  - Ensure this has junit.username and junit.password properties that point to a valid OpenMRS account
 */
@SkipBaseSetup
public class DataValidationTest extends BaseModuleContextSensitiveTest {

    static Properties props = OpenmrsUtil.getRuntimeProperties("validation");

    static int BATCH_SIZE = 1000;
    static int MAX_TO_CHECK = 2000;

    static {
        if (props != null) {
            System.setProperty("databaseUrl", props.getProperty("connection.url"));
            System.setProperty("databaseUsername", props.getProperty("connection.username"));
            System.setProperty("databasePassword", props.getProperty("connection.password"));
            System.setProperty("databaseDriver", props.getProperty("connection.driver_class"));
            System.setProperty("databaseDialect", "org.hibernate.dialect.MySQLDialect");
            System.setProperty("useInMemoryDatabase", "false");
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
    public void testThatActivatorDoesAllSetupForMirebalais() {

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


        for (Validator v : validators) {
            System.out.println("------------------------");
            System.out.println("Found validator: " + v.getClass());
            Handler handlerAnnotation = v.getClass().getAnnotation(Handler.class);
            if (handlerAnnotation == null) {
                System.out.println("No handler annotation found");
            }
            else {
                for (Class c : handlerAnnotation.supports()) {
                    System.out.println("Validator supports: " + c);
                    if (OpenmrsObject.class.isAssignableFrom(c)) {
                        List rowCounts = sessionFactory.getCurrentSession().createCriteria(c).setProjection(Projections.rowCount()).list();
                        if (rowCounts == null || rowCounts.size() == 0) {
                            System.out.println("No mapped objects found");
                        }
                        else if (rowCounts.size() > 1) {
                            System.out.println("Multiple objects match criteria, assuming there are more specific validators, skipping");
                        }
                        else {
                            Long numRows = (Long)rowCounts.get(0);
                            System.out.println("OpenMRS Object. Count: " + numRows);
                            int numChecked = 0;
                            int numErrors = 0;
                            long totalTime = 0;
                            while (numChecked < numRows && numChecked < MAX_TO_CHECK) {
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
                                            System.out.println("!!!!!!!!!!!!! ERROR FOUND on " + o);
                                            for (ObjectError e : errors.getAllErrors()) {
                                                System.out.println("!!!!!!!!!!!!! - " + e);
                                            }
                                        }

                                    } catch (Exception e) {
                                        System.out.println("Error: " + e);
                                        errorFound = true;
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

                            long projectedTimeToValidateAll = numChecked > 0 ?(totalTime * numRows / numChecked) : 0L;
                            fullValidationTimeExpected += projectedTimeToValidateAll;

                            System.out.println("****************** " + c.getSimpleName());
                            System.out.println("****************** " + numChecked + " / " + numRows + " validated");
                            System.out.println("****************** " + numErrors + " / " + numChecked + " errors");
                            System.out.println("****************** " + formatTime(totalTime));
                            System.out.println("****************** " + formatTime(projectedTimeToValidateAll) + " to validate all");

                            totalValidationTime += totalTime;
                            totalValidationErrors += numErrors;
                        }
                    }
                }
            }
        }
        System.out.println("****************** VALIDATION CHECKS COMPLETE");
        System.out.println("****************** " + formatTime(totalValidationErrors) + " errors found");
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
}

package org.openmrs.module.pihcore.event;

import org.junit.jupiter.api.Test;

public class PatientEventConsumerTest {

    /**
     * TODO: Fix the below, likely to use some sort of mocking or test framework that mimics mysql events
     */
    @Test
    public void shouldTrackPatientChanges() throws Exception {
        /*
        try (Mysql mysql = Mysql.open()) {
            TestEventContext ctx = new TestEventContext(mysql);
            Database db = ctx.getDatabase();
            db.executeUpdate("create table dbevent_patient (patient_id int primary key, last_updated datetime, deleted boolean)");
            DbEventSourceConfig config = new DbEventSourceConfig(100002, "EventLogger", ctx);
            config.configureTablesToInclude(ctx.getDatabase().getMetadata().getPatientTableNames());
            DbEventSource eventSource = new DbEventSource(config);
            eventSource.setEventConsumer(new PatientUpdateEventConsumer(config));
            try {
                eventSource.start();
                List<Map<String, Object>> results = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    results = db.executeQuery("select * from dbevent_patient", new MapListHandler());
                    if (results.isEmpty()) {
                        Thread.sleep(1000);
                    } else {
                        i = 10;
                    }
                }
                assertFalse(results.isEmpty());
                assertThat(results.get(0).get("patient_id"), equalTo(3));
                assertNotNull(results.get(0).get("last_updated"));
                assertFalse((Boolean) results.get(0).get("deleted"));
            } finally {
                db.executeUpdate("drop table dbevent_patient");
                eventSource.stop();
            }
        }

         */
    }
}

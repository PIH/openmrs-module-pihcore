package org.openmrs.module.pihcore.event;

import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openmrs.module.dbevent.Database;
import org.openmrs.module.dbevent.DbEventSource;
import org.openmrs.module.dbevent.DbEventSourceConfig;
import org.openmrs.module.dbevent.EventContext;
import org.openmrs.module.dbevent.test.MysqlExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MysqlExtension.class)
public class PatientEventConsumerTest {

    @Test
    public void shouldTrackPatientChanges() throws Exception {
        EventContext ctx = MysqlExtension.getEventContext();
        Database db = ctx.getDatabase();
        try {
            db.executeUpdate("drop table if exists dbevent_patient");
            db.executeUpdate("create table dbevent_patient (patient_id int primary key, last_updated datetime, deleted boolean)");
            DbEventSourceConfig config = new DbEventSourceConfig(100002, "EventLogger", ctx);
            config.configureTablesToInclude(ctx.getDatabase().getMetadata().getPatientTableNames());
            DbEventSource eventSource = new DbEventSource(config);
            PatientUpdateEventConsumer consumer = new PatientUpdateEventConsumer(config);
            eventSource.setEventConsumer(consumer);
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
                eventSource.stop();
            }
        }
        finally {
            db.executeUpdate("drop table if exists dbevent_patient");
        }
    }
}

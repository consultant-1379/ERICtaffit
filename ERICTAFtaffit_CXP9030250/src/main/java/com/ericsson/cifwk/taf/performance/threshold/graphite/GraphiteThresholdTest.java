package com.ericsson.cifwk.taf.performance.threshold.graphite;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.performance.metric.graphite.AmqpPublisher;
//import com.ericsson.cifwk.taf.performance.threshold.;
//import com.ericsson.cifwk.taf.performance.threshold.ThresholdHandler;
import com.ericsson.cifwk.taf.performance.threshold.DataWatcher;
import com.ericsson.cifwk.taf.performance.threshold.ViolationListener;
import com.ericsson.cifwk.taf.performance.threshold.ThresholdRule;
import com.ericsson.cifwk.taf.performance.threshold.MetricSlice;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertThat;


/**
 *
 */
public class GraphiteThresholdTest {

    private static Logger log = LoggerFactory.getLogger(GraphiteThresholdTest.class);

    private static final String AMQP_HOST = "rabbitmq";
    private static final String GRAPHITE_HOST = "graphite";
    private static final String EXCHANGE_NAME = "eiffel.poc.graphite";

    private static Host amqpHost;
    private static Host graphiteHost;
    private static AmqpPublisher amqpPublisher;


    @BeforeClass
    public static void init() throws Exception {
        graphiteHost = DataHandler.getHostByName(GRAPHITE_HOST);
        amqpHost = DataHandler.getHostByName(AMQP_HOST);
        amqpPublisher = new AmqpPublisher(amqpHost, EXCHANGE_NAME);
        amqpPublisher.connect();

    }

    @AfterClass
    public static void destroy() throws Exception {
        amqpPublisher.shutdown();
    }

    //TODO - Revisit and assess if needed
    @Ignore
    @Test
    public void shouldWriteAndReadValue() throws Exception {
        long timestamp = now();

        amqpPublisher.send("test.x.y.z", "24", timestamp);

        Thread.sleep(100);

        GraphiteClient client = new GraphiteClient(graphiteHost);
        assertMetricExists(client, "test.x.y.z", 24.0);
    }

    //TODO - Revisit and assess if this test is needed. Code is commented out as there is no need for Mockito in Taffit tests.
    @Ignore
    @Test
    public void shouldReactOnThresholdViolations() throws Exception {
/*      ThresholdHandler service = new ThresholdHandler();
        ViolationListener listener = mock(ViolationListener.class);
        MaxCap thresholdA = new MaxCap(100);
        MaxCap thresholdB = new MaxCap(200);
        MaxCap thresholdC = new MaxCap(300);
        service.monitor("test.a", DataWatcher.builder().listener(listener)
                .threshold(thresholdA).build());
        service.monitor("test.b", DataWatcher.builder().listener(listener)
                .threshold(thresholdB).build());
        service.monitor("test.c", DataWatcher.builder().listener(listener)
                .threshold(thresholdC).build());

        service.start(graphiteHost, 3);

        long timestamp = now();
        amqpPublisher.send("test.a", "101", timestamp);
        amqpPublisher.send("test.b", "201", timestamp);
        amqpPublisher.send("test.c", "299", timestamp);

        System.out.println("Waiting for the events...");
        Thread.sleep(5 * 1000);

        service.stop();

        verify(listener).onViolate(eq(thresholdA), anyLong(), anyDouble());
        verify(listener).onViolate(eq(thresholdB), anyLong(), anyDouble());
        verifyNoMoreInteractions(listener);*/
    }

    //TODO - Revisit and assess if needed
    @Ignore
    @Test
    public void should() throws Exception {
   /*     ThresholdHandler service = new ThresholdHandler();
        service.monitor("tor.atclvm794.os.CpuUser", DataWatcher.builder()
                .listener(new ViolationListener() {
                    @Override
                    public void onViolate(ThresholdRule threshold,
                            long timestamp, double update) {
                        System.out.println(">>>" + update);
                    }
                }).max(10d).build());

        service.start(graphiteHost, 10);
        service.listen(30);
        service.stop(); */
    }

    private void assertMetricExists(GraphiteClient graphiteClient,
            String target, double value) throws IOException {
        String raw = graphiteClient.get("raw", target, "-10s", "0");
        RawFormatParser parser = new RawFormatParser();
        MetricSlice data = parser.parse(raw);

        assertThat(data.getData(), hasItemInArray(closeTo(value, 0.1)));
    }

    private long now() {
        return System.currentTimeMillis() / 1000;
    }

}

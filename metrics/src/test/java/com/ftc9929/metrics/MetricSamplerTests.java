/*
 Copyright (c) 2020 The Tech Ninja Team (https://ftc9929.com)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package com.ftc9929.metrics;

import com.ftc9929.testing.fakes.util.FakeHardwareMapFactory;
import com.google.common.base.Stopwatch;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MetricSamplerTests {
    @Test
    public void firstDoNoHarm() {
        HardwareMap hardwareMap = new HardwareMap(null); // bad design, won't fail until used!
        MetricsReporter r = StatsdMetricsReporter.builder().metricsServerHost("").tags(new String[] {"abcd"}).build();
        RobotMetricsSampler s = RobotMetricsSampler.builder().metricsReporter(r).hardwareMap(hardwareMap).build();
        s.doSamples();

        r = StatsdMetricsReporter.builder().metricsServerHost("").build();
        s = RobotMetricsSampler.builder().metricsReporter(r).hardwareMap(hardwareMap).build();
        s.doSamples();
    }

    @Test
    public void happyPath() throws IOException {
        final String[] messagePrefixes = new String[] {
                "dcm_pow_motor1:0|g|#_ts:",
                "dcm_vel_motor1:0|g|#_ts:",
                "dcm_curr_motor1:0|g|#_ts:",
                "sensor_TestDigitalDevice:1|g|#_ts:",
                "servo_servo1:0|g|#_ts:",
                "metric_sample_time_ms:2|g|#_ts:"
        };

        try (TestStatsdServer statsDServer = new TestStatsdServer()) {
            int port = statsDServer.getPortNumber();

            HardwareMap hardwareMap = FakeHardwareMapFactory.getFakeHardwareMap("sample_hardware_map.xml");
            MetricsReporter r = StatsdMetricsReporter.builder().metricsServerHost("127.0.0.1").metricsServerPortNumber(port).tags(new String[]{"abcd"}).build();
            RobotMetricsSampler s = RobotMetricsSampler.builder().metricsReporter(r).hardwareMap(hardwareMap).build();
            s.doSamples();

            List<String> messagesReceived = null;

            Stopwatch stopwatch = Stopwatch.createStarted();

            while (true) {
                if (stopwatch.elapsed(TimeUnit.SECONDS) > 60) {
                    Assertions.fail("Timed out waiting for statsd messages");
                }

                messagesReceived = statsDServer.messagesReceived();

                if (messagesReceived.size() >= 6) {
                    break;
                }
            }

            // We have all of the messages, let's assert at least some of their contents

            for (String message : messagesReceived) {
                assertContains(message, "#_ts:");
                assertContains(message, "|g|");

                assertContains(message, ",abcd");
            }
        }
    }

    private void assertContains(String lookIn, String lookFor) {
        if (!lookIn.contains(lookFor)) {
            Assertions.fail(String.format("Failed to find '%s' in '%s'", lookFor, lookIn));
        }
    }

}

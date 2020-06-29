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

import android.util.Log;

import com.timgroup.statsd.NonBlockingStatsDClient;

import java.util.Set;

import lombok.Builder;
import lombok.NonNull;

import static com.ftc9929.corelib.Constants.LOG_TAG;

/**
 * A metrics reporter that sends collected metrics to a statsd implementation
 * that is listening for UDP packets.
 */
public class StatsdMetricsReporter implements MetricsReporter {
    public static final int DEFAULT_METRICS_SERVER_PORT_NUMBER = 8126;

    private final NonBlockingStatsDClient statsDClient;

    /**
     * Create a new statsd metrics reporter configured to send metrics to the
     * statsd server listening on the provided hostname/port
     *
     * @param metricsServerHost hostname of the statsd server
     * @param metricsServerPortNumber port number of the statsd server - default of 8126 if not given
     * @param prefix prefix all metrics sent with this string
     */
    @Builder
    private StatsdMetricsReporter(@NonNull String metricsServerHost,
                                  int metricsServerPortNumber,
                                  String prefix) {

        if (metricsServerPortNumber == 0) {
            metricsServerPortNumber = DEFAULT_METRICS_SERVER_PORT_NUMBER;
        }

        try {
            Log.d(LOG_TAG, String.format("Setting up statsd client connection to %s:%d",
                    metricsServerHost, metricsServerPortNumber));

            statsDClient = new NonBlockingStatsDClient(prefix, metricsServerHost, metricsServerPortNumber);
        } catch (Exception ex) {
            throw new RuntimeException("Can't open statsd client", ex);
        }
    }

    @Override
    public void reportMetrics(Set<GaugeMetricSource> gaugeSources) {
        if (statsDClient == null) {
            return;
        }

        try {
            long beginSamplingTimeMs = System.currentTimeMillis();

            // Don't recompute the sample time for every sample, "close enough" is "good enough"
            String timestampTag = new StringBuilder("ts-"
                    + Long.toString(beginSamplingTimeMs)).toString();

            String[] metricTags = new String[] { timestampTag };

            for (GaugeMetricSource gaugeSource : gaugeSources) {
                final double value = gaugeSource.getValue();

                if (value != MetricsSampler.NO_REPORT_VALUE) {
                    statsDClient.gauge(gaugeSource.getSampleName(), value, metricTags);
                }
            }

            long endSamplingTimeMs = System.currentTimeMillis();

            statsDClient.gauge("metric_sample_time_ms",
                    (endSamplingTimeMs - beginSamplingTimeMs), timestampTag);
        } catch (Throwable t) {
            // metrics should do no harm
            Log.e(LOG_TAG, "Caught exception while sampling for metrics", t);
        }
    }
}

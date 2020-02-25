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

package com.ftc9929.corelib.control;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;

import java.util.concurrent.TimeUnit;

/**
 * Detects lack of change in an observed value over a time window. Call
 * isStalled() with the value from a motor encoder or other sensor. If
 * the difference between the previous value supplied to isStalled()
 * and the current does not create a delta larger than the tolerance
 * given in the constructor, a timer is started.
 *
 * If later values passed to isStalled() also do not create a delta larger
 * than the tolerance, the timer continues to run. If the elapsed time where
 * deltas have not exceeded the tolerance is larger than the time window
 * provided in the constructor, isStalled() will return true.
 *
 * Values passed to isStalled() that result in a delta larger than the tolerance
 * will stop and reset the timer.
 *
 */
public class StallDetector {
    private final double tolerance;

    private final long timeWindowMillis;

    private final Stopwatch timer;

    private double lastObservedValue;

    private boolean firstObservation = true;

    public StallDetector(Ticker ticker, double tolerance, long timeWindowMillis) {
        this.tolerance = tolerance;
        this.timeWindowMillis = timeWindowMillis;
        timer = Stopwatch.createUnstarted(ticker);
    }

    public boolean isStalled(double observedValue) {
        if (firstObservation) {
            firstObservation = false;
            lastObservedValue = observedValue;

            return false;
        }

        double absoluteDelta = Math.abs(lastObservedValue - observedValue);

        lastObservedValue = observedValue;

        if (absoluteDelta < tolerance) {
            if (!timer.isRunning()) {
                timer.start();
            }
        } else {
            if (timer.isRunning()) {
                timer.reset();
            }
        }

        if (timer.isRunning()) {
            long elapsedMillis = timer.elapsed(TimeUnit.MILLISECONDS);

            if (elapsedMillis > timeWindowMillis) {
                return true; // Stalled!
            }
        }

        return false;
    }
}

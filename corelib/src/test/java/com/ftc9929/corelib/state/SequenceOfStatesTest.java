/*
 Copyright (c) 2026 The Tech Ninja Team (https://ftc9929.com)

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
package com.ftc9929.corelib.state;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.ftc9929.testing.fakes.FakeTelemetry;
import com.google.common.base.Ticker;
import com.google.common.testing.FakeTicker;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.TimeUnit;


public class SequenceOfStatesTest {
    private FakeTelemetry telemetry;

    private final Ticker fakeTicker = new FakeTicker();

    @BeforeEach
    protected void setUp() throws Exception {
        telemetry = new FakeTelemetry();
    }

    @Test
    public void equality() {
        final SequenceOfStates stepsOne = new SequenceOfStates(fakeTicker, telemetry);
        final SequenceOfStates stepsTwo = new SequenceOfStates(fakeTicker, telemetry);

        assertEquals(stepsOne, stepsTwo);

        final Runnable r = () -> {

        };

        stepsOne.addRunnableStep("test", r);

        assertNotEquals(stepsOne, stepsTwo);

        stepsTwo.addRunnableStep("test", r);

        assertEquals(stepsOne, stepsTwo);

        stepsOne.addWaitStep("wait", 5, TimeUnit.MILLISECONDS);

        assertNotEquals(stepsOne, stepsTwo);

        stepsTwo.addWaitStep("wait", 5, TimeUnit.MILLISECONDS);

        assertEquals(stepsOne, stepsTwo);
    }
}

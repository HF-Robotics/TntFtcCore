/*
 Copyright (c) 2021 The Tech Ninja Team (https://ftc9929.com)

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

import com.ftc9929.testing.fakes.FakeTelemetry;
import com.google.common.testing.FakeTicker;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StopwatchDelayStateTest {
    @Test
    public void reEntrancy() {
        FakeTicker ticker = new FakeTicker();
        StopwatchDelayState state = new StopwatchDelayState("", new FakeTelemetry(), ticker,
                100, TimeUnit.MILLISECONDS);
        state.setNextState(null);

        assertNormalBehavior(ticker, state);

        // Now that timeout has been reached, running through the state again, should reset timer
        // and behave the same way again...
        assertNormalBehavior(ticker, state);
    }

    private void assertNormalBehavior(FakeTicker ticker, StopwatchDelayState state) {
        assertEquals(state, state.doStuffAndGetNextState());
        ticker.advance(50, TimeUnit.MILLISECONDS);
        assertEquals(state, state.doStuffAndGetNextState());
        ticker.advance(75, TimeUnit.MILLISECONDS);
        assertEquals(null, state.doStuffAndGetNextState());
    }
}
/*
 Copyright (c) 2022 The Tech Ninja Team (https://ftc9929.com)

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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StopwatchDelayStateTest {
    private FakeTelemetry testTelemetry;

    private FakeTicker fakeTicker;

    private StateMachine stateMachine;

    @BeforeEach
    protected void setUp() {
        testTelemetry = new FakeTelemetry();
        stateMachine = new StateMachine(testTelemetry);
        fakeTicker = new FakeTicker();
    }

    @Test
    public void reEntrancy() {
        StopwatchDelayState state = new StopwatchDelayState("", testTelemetry, fakeTicker,
                100, TimeUnit.MILLISECONDS);
        state.setNextState(null);

        assertNormalBehavior(fakeTicker, state);

        // Now that timeout has been reached, running through the state again, should reset timer
        // and behave the same way again...
        assertNormalBehavior(fakeTicker, state);
    }

    @Test
    public void withSequences() {
        SequenceOfStates steps = new SequenceOfStates(fakeTicker, testTelemetry);
        steps.addWaitStep("wait for me", 100, TimeUnit.MILLISECONDS);
        steps.addRunnableStep("done", () -> {});

        stateMachine.addSequence(steps);

        stateMachine.doOneStateLoop();
        assertEquals("wait for me", stateMachine.getCurrentStateName());

        fakeTicker.advance(50, TimeUnit.MILLISECONDS);

        stateMachine.doOneStateLoop();
        assertEquals("wait for me", stateMachine.getCurrentStateName());

        fakeTicker.advance(75, TimeUnit.MILLISECONDS);
        stateMachine.doOneStateLoop();

        assertEquals("done", stateMachine.getCurrentStateName());
    }

    private void assertNormalBehavior(FakeTicker ticker, StopwatchDelayState state) {
        assertEquals(state, state.doStuffAndGetNextState());
        ticker.advance(50, TimeUnit.MILLISECONDS);
        assertEquals(state, state.doStuffAndGetNextState());
        ticker.advance(75, TimeUnit.MILLISECONDS);
        assertEquals(null, state.doStuffAndGetNextState());
    }
}
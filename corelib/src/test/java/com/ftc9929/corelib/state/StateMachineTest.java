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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StateMachineTest {

    StateMachine stateMachine;
    FakeTelemetry testTelemetry;
    FakeTicker fakeTicker;

    @BeforeEach
    protected void setUp() {
        testTelemetry = new FakeTelemetry();
        stateMachine = new StateMachine(testTelemetry);
        fakeTicker = new FakeTicker();
    }

    @Test
    public void testNormalOperation() {
        TestState startState = new TestState("start", null);
        TestState state2 = new TestState("state2", null);
        startState.setNextState(state2);
        TestState state3 = new TestState("state3", null);
        state2.setNextState(state3);

        stateMachine.setFirstState(startState);

        assertEquals("start", stateMachine.getCurrentStateName());

        stateMachine.doOneStateLoop();
        testTelemetry.update();
        assertEquals("00 : > state state2\n", testTelemetry.getCurrentTelemetryData());
        assertEquals("state2", stateMachine.getCurrentStateName());

        stateMachine.doOneStateLoop();
        testTelemetry.update();
        assertEquals("00 : > state state3\n", testTelemetry.getCurrentTelemetryData());
        assertEquals("state3", stateMachine.getCurrentStateName());
    }

    @Test
    public void testDelayState() {
        FakeTicker fakeTicker = new FakeTicker();

        TestState startState = new TestState("start", null);
        TestState state2 = new TestState("state2", null);

        stateMachine.addSequential(startState);
        stateMachine.addSequential(state2);
        stateMachine.addStartDelay(2, fakeTicker);

        // Starting delayed
        assertEquals("Delayed start", stateMachine.getCurrentStateName());
        stateMachine.doOneStateLoop();
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        stateMachine.doOneStateLoop();
        assertEquals("Delayed start", stateMachine.getCurrentStateName());
        fakeTicker.advance(1700, TimeUnit.MILLISECONDS);

        // Delay has expired, test normal operation
        stateMachine.doOneStateLoop();
        assertEquals("start", stateMachine.getCurrentStateName());

        stateMachine.doOneStateLoop();
        assertEquals("state2", stateMachine.getCurrentStateName());
    }

    @Test
    public void immutableOnceStarted() {
        stateMachine.setFirstState(new TestState("abcdef", null));
        stateMachine.doOneStateLoop();

        final TestState testState2 = new TestState("hijklmnop", null);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                stateMachine.setFirstState(testState2);
            }
        });

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                stateMachine.addStartDelay(1, new FakeTicker());
            }
        });

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                stateMachine.addStartDelay(1, new FakeTicker());
            }
        });

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                stateMachine.addSequential(testState2);
            }
        });
    }

    @Test
    public void testDelayStateWithNewSequences() {
        TestState startState = new TestState("start", null);
        TestState state2 = new TestState("state2", null);
        SequenceOfStates sequenceOfStates = new SequenceOfStates(fakeTicker, testTelemetry);

        sequenceOfStates.addSequential(startState);
        sequenceOfStates.addSequential(state2);
        stateMachine.addSequence(sequenceOfStates);
        stateMachine.addStartDelay(2, fakeTicker);

        // Starting delayed
        assertEquals("Delayed start", stateMachine.getCurrentStateName());
        stateMachine.doOneStateLoop();
        fakeTicker.advance(500, TimeUnit.MILLISECONDS);
        stateMachine.doOneStateLoop();
        assertEquals("Delayed start", stateMachine.getCurrentStateName());
        fakeTicker.advance(1700, TimeUnit.MILLISECONDS);

        // Delay has expired, test normal operation
        stateMachine.doOneStateLoop();
        assertEquals("start", stateMachine.getCurrentStateName());

        stateMachine.doOneStateLoop();
        assertEquals("state2", stateMachine.getCurrentStateName());
    }

    @Test
    public void repeatingSteps() {
        SequenceOfStates steps = new SequenceOfStates(fakeTicker, testTelemetry);

        final int[] counter = new int[1];
        counter[0] = 0;

        steps.addRepeatingStep("Test repeat step", (myself) -> {
            counter[0]++;

            if (counter[0] > 4) {
                return false;
            }

            return true;
        });

        TestState testState = new TestState("test", testTelemetry);

        steps.addSequential(testState);

        stateMachine.addSequence(steps);

        while (stateMachine.getCurrentStateName() != testState.getName()) {
            stateMachine.doOneStateLoop();
        }

        assertEquals(5, counter[0]);
    }

    class TestState extends State {
        private int executionCount = 0;

        private int configuredCallCount = 0;

        private int configurableParameter = 0;

        public TestState(String name, Telemetry telemetry) {
            super(name, telemetry);
        }

        @Override
        public State doStuffAndGetNextState() {
            executionCount++;
            return nextState;
        }

        @Override
        public void resetToStart() {
            executionCount = 0;
        }
    }
}
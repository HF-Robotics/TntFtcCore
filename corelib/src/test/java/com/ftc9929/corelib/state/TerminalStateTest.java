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

package com.ftc9929.corelib.state;

import com.ftc9929.testing.fakes.FakeTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TerminalStateTest {

    @Test
    public void happyPath() {
        OurTerminalState state = new OurTerminalState(new FakeTelemetry());
        assertEquals(0, state.executionCount);
        assertEquals(state, state.doStuffAndGetNextState());
        assertEquals(1, state.executionCount);

        // Check that the terminal code only runs once!
        state.doStuffAndGetNextState();
        assertEquals(1, state.executionCount);
    }

    class OurTerminalState extends TerminalState {

        int executionCount = 0;

        public OurTerminalState(Telemetry telemetry) {
            super("Testing", telemetry);
        }

        @Override
        protected void runTerminalCode() {
            executionCount++;
        }
    }
}

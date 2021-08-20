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

import com.ftc9929.corelib.control.NinjaGamePad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import lombok.NonNull;

/**
 * A State that runs the code in runTerminalCode, and then continues to spin
 * returning itself as the next state to run until the OpMode completes.
 *
 * Typically used to place the robot in a "safe" state at the end of
 * autonomous, by setting all motor powers to 0, moving servos to safe positions, etc.
 */
public abstract class TerminalState extends State {
    private boolean hasRun = false;

    public TerminalState(@NonNull String name, Telemetry telemetry) {
        super(name, telemetry);
    }

    @Override
    public State doStuffAndGetNextState() {
        if (!hasRun) {
            runTerminalCode();
            hasRun = true;
        }

        return this;
    }

    @Override
    public void resetToStart() {
        hasRun = false;
    }

    protected abstract void runTerminalCode();
}

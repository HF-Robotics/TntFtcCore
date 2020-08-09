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


import android.util.Log;

import com.ftc9929.corelib.control.NinjaGamePad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import lombok.NonNull;

import static com.ftc9929.corelib.Constants.LOG_TAG;

/**
 * Base class for all state machine states
 */
public abstract class State {
    protected State nextState;
    protected final Telemetry telemetry;

    protected final String name;

    protected State(@NonNull final String name, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.name = name;
    }

    /**
     * Perform the logic for this state, return the next state which may be
     * this state if we are to remain in this state.
     */
    public abstract State doStuffAndGetNextState();

    public void setNextState(State state) {
        nextState = state;
    }

    public abstract void resetToStart();

    public abstract void liveConfigure(NinjaGamePad gamePad);

    public String getName() {
        return name;
    }

    protected void debugMsg(String message) {
        if (telemetry != null) {
            telemetry.addData("01-State", message);
        }

        Log.d(LOG_TAG, message);
    }
}

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

import com.ftc9929.corelib.control.DebouncedButton;
import com.ftc9929.corelib.control.NinjaGamePad;
import com.google.common.base.Ticker;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import lombok.NonNull;
import lombok.Setter;

public class ButtonPressWithTimeoutState extends StopwatchTimeoutSafetyState {
    private final DebouncedButton button;

    @Setter
    private  State whenTimedOutState;


    public ButtonPressWithTimeoutState(@NonNull String name,
                                       @NonNull DebouncedButton button,
                                       State whenTimedOutState,
                                       Telemetry telemetry,
                                       @NonNull Ticker ticker,
                                       long safetyTimeoutMillis) {
        super(name, telemetry, ticker, safetyTimeoutMillis);
        this.button = button;
        this.whenTimedOutState = whenTimedOutState;
    }

    @Override
    public State doStuffAndGetNextState() {

        if (isTimedOut()) {
            if (whenTimedOutState == null) {
                resetToStart();

                return this;
            }

            resetToStart();

            return whenTimedOutState;
        }

        if (button.getRise()) {
            return nextState;
        }

        return this;
    }
}

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

import org.firstinspires.ftc.robotcore.external.Telemetry;

import lombok.NonNull;

/**
 * A state that will perform an action when a button press causes a toggling effect
 * (one press for "on", one press for "off").
 */
public abstract class ToggleState extends State {
    private boolean toggled = false;

    private final DebouncedButton button;

    public ToggleState(@NonNull final String name,
                       Telemetry telemetry,
                       @NonNull final DebouncedButton button) {
        super(name, telemetry);
        this.button = button;
        setNextState(this);
    }

    @Override
    public void resetToStart() {
        toggled = false;
    }

    @Override
    public void liveConfigure(NinjaGamePad configureGamePad) {

    }

    @Override
    public State doStuffAndGetNextState() {
        if (button.getRise()) {
            toggled = !toggled;

            if (toggled) {
                toggledOn();
            } else {
                toggledOff();
            }
        }
        return nextState;
    }

    /**
     * Called when the toggled state has gone from off to on
     */
    protected abstract void toggledOn();

    /**
     * Called when the toggled state has gone from on to off
     */
    protected abstract void toggledOff();
}

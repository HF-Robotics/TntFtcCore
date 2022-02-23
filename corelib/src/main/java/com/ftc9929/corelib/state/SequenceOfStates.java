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

import android.util.Log;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.ftc9929.corelib.Constants.LOG_TAG;
import static com.ftc9929.corelib.state.StateMachine.stateClassAndName;

import com.google.common.base.Ticker;

import org.firstinspires.ftc.robotcore.external.Function;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.TimeUnit;

/**
 * A list of states to be executed in order by the StateMachine class. Automatically
 * sets up the linkages between states in a sequence.
 */
@RequiredArgsConstructor
public class SequenceOfStates {
    @Getter
    protected State firstState;

    @Getter
    protected State lastState;

    protected final Ticker ticker;

    protected final Telemetry telemetry;

    public void addSequential(@NonNull State state) {
        Log.d(LOG_TAG, String.format("addSequential(%s)", stateClassAndName(state)));

        if (firstState == null) {
            firstState = state;
        } else {
            lastState.setNextState(state);
        }

        lastState = state;
    }

    /**
     * Runs the given Function/Lambda as a state. Continues to run the code in the function as
     * long as it returns true, otherwise the next state in this sequence will be run.
     */
    public void addRepeatingStep(String name, Function<State, Boolean> step) {
        RepeatingLambdaState state = new RepeatingLambdaState(name, step, telemetry);

        addSequential(state);
    }

    /**
     * Runs the given Runnable/Lambda as a state.
     */
    public void addRunnableStep(@NonNull String name, @NonNull Runnable runnable) {
        RunnableState runnableState = new RunnableState(name, telemetry, runnable);

        addSequential(runnableState);
    }

    /**
     * Waits the given amount of time before running states that follow in the sequence.
     */
    public void addWaitStep(@NonNull String name, long amount, @NonNull TimeUnit unit) {
        StopwatchDelayState delayState = new StopwatchDelayState(name, telemetry, ticker, amount, unit);

        addSequential(delayState);
    }
}

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

import com.ftc9929.corelib.control.DebouncedButton;
import com.ftc9929.corelib.control.NinjaGamePad;
import com.google.common.base.Ticker;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import lombok.Setter;

import static com.ftc9929.corelib.Constants.LOG_TAG;

@SuppressWarnings("unused")
public class StateMachine {

    private Stack<State> executedStates;

    private Set<State> allStates;

    private State currentState = null;

    private State firstState;

    private State lastSequentialState;

    private boolean areWeDebugging = false;

    private boolean isStateMachinePaused = false;

    @Setter
    private DebouncedButton goButton;

    @Setter
    private DebouncedButton goBackButton;

    @Setter
    private DebouncedButton doOverButton;

    private NinjaGamePad configureGamePad;

    private Telemetry telemetry;

    public void addNewState(State newState) {
        Log.d(LOG_TAG, String.format("addNewState(%s)", stateClassAndName(newState)));
        allStates.add(newState);
    }

    public void startDebugging() {
        areWeDebugging = true;
    }

    public void stopDebugging() {
        areWeDebugging = false;
    }

    public void setConfigureGamepad(NinjaGamePad configureGamePad) {
        this.configureGamePad = configureGamePad;
    }

    public String getCurrentStateName() {
        return currentState.getName();
    }

    public StateMachine(Telemetry telemetry) {
        this.telemetry = telemetry;
        executedStates = new Stack<>();
        allStates = new HashSet<>();
    }

    /**
     * Adds the "next" state in a sequential state machine setting up the
     * transition to next state automatically. Also sets as first state if one
     * is not already defined.
     */
    public void addSequential(@NonNull State nextState) {
        Log.d(LOG_TAG, String.format("addSequential(%s)",
                stateClassAndName(nextState)));

        if (lastSequentialState != null) {
            Log.d(LOG_TAG, String.format("addSequential() %s - next -> %s",
                    stateClassAndName(lastSequentialState), stateClassAndName(nextState)));

            lastSequentialState.setNextState(nextState);
        }

        if (currentState == null) {
            Log.d(LOG_TAG, "addSequential() - no current first state, setting first state");
            setFirstState(nextState);
        } else {
            addNewState(nextState);
        }

        lastSequentialState = nextState;
    }

    /**
     * Sets the first State the state machine will use.
     *
     * If the state has not already been added w/ addNewState then it will added for you
     *
     * @param state The first state the state machine will execute
     * @throws  IllegalStateException if this method has already been called
     */
    public void setFirstState(@NonNull State state) {
        if (currentState != null) {
            throw new IllegalArgumentException("State machine already has the first state set");
        }

        Log.d(LOG_TAG, String.format("setFirstState(%s)",
                stateClassAndName(state)));

        executedStates.push(state);
        addNewState(state);
        currentState = state;
        firstState = state;
    }

    public void addStartDelay(long numberOfSeconds, @NonNull Ticker ticker) {
        State originalFirstState = executedStates.pop();
        StopwatchDelayState startDelay = new StopwatchDelayState("Delayed start",
                telemetry, ticker, numberOfSeconds, TimeUnit.SECONDS);
        executedStates.push(startDelay);
        startDelay.setNextState(originalFirstState);
        currentState = startDelay;
    }

    public void doOneStateLoop() {
        try {
            if (!isStateMachinePaused) {
                if (currentState == null) {
                    Log.w(LOG_TAG, "No state left to execute");
                }

                State possibleNextState = currentState.doStuffAndGetNextState();

                if (possibleNextState == null) {
                    //noinspection ConstantConditions
                    currentState = possibleNextState;
                } else if (!possibleNextState.equals(currentState)) {
                    // We've changed states, Yay time to party
                    Log.d(LOG_TAG, String.format("state %s -> %s",
                            stateClassAndName(currentState),
                            stateClassAndName(possibleNextState)));

                    executedStates.push(possibleNextState);
                    currentState = possibleNextState;

                    if (areWeDebugging) {
                        isStateMachinePaused = true;
                    }
                }
            } else {
                // we're paused, allowing live configuring and waiting for go or go back signals
                currentState.liveConfigure(configureGamePad);

                // check for un-pausing
                if (goButton.getRise()) {
                    isStateMachinePaused = false;
                } else if (goBackButton.getRise()) {
                    // we were paused - and haven't run the current step yet
                    currentState = executedStates.pop();

                    if (!executedStates.empty()) {
                        currentState = executedStates.pop(); // this is the one we really want
                    }

                    currentState.resetToStart();
                    isStateMachinePaused = true;
                } else if (doOverButton.getRise()) {
                    // reset all the states, set current to ??? and pause the state machine
                    for (State resetThisState : allStates) {
                        resetThisState.resetToStart();
                    }

                    executedStates.clear();

                    executedStates.push(firstState);
                    currentState = firstState;
                    isStateMachinePaused = true;
                }
            }

            if (telemetry != null) {
                telemetry.addData("00", String.format("%s%s state %s", areWeDebugging ? "[DEBUG]" : "",
                        isStateMachinePaused ? "||" : ">", currentState.getName()));
            }
        } catch (Throwable t) {
            // Better logging than the FTC SDK provides :(
            Log.e(LOG_TAG, "Exception during state machine", t);

            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }

            RuntimeException rte = new RuntimeException();
            rte.initCause(t);

            throw rte;
        }
    }

    private static String stateClassAndName(@NonNull State state) {
        return String.format("%s - '%s'", state.getClass().getSimpleName(), state.getName());
    }
}

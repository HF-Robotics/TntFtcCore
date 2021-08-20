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

import android.util.Log;

import com.google.common.base.Ticker;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.TimeUnit;

import lombok.NonNull;

import static com.ftc9929.corelib.Constants.LOG_TAG;

@SuppressWarnings("unused")
public class StateMachine {
    private State currentState = null;

    private boolean stateMachineRunning = false;

    private State firstState;

    private State lastSequentialState;

    private Telemetry telemetry;

    public String getCurrentStateName() {
        // This is a little bit of a hack, as the API to this is changing
        // until doOneStateLoop() has run, currentState will be null,
        // this change in behavior breaks callers, so honor the old contract, it's mostly
        // for testing, anyway
        if (!stateMachineRunning) {
            return firstState.getName();
        }

        return currentState.getName();
    }

    public StateMachine(@NonNull Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    /**
     * Sets up the first state executed by the state machine to be the first state of the
     * given sequence.
     * @param sequenceOfStates
     *
     * @throws IllegalArgumentException if the state machine is already running, or the first state
     * has already been set.
     */
    public void addSequence(@NonNull SequenceOfStates sequenceOfStates) {
        checkStateMachineNotRunning();
        checkFirstStateNotSet();

        final State sequenceFirstState = sequenceOfStates.getFirstState();

        Log.d(LOG_TAG, String.format("addSequential() - no current first state, setting first state to %s", stateClassAndName(sequenceFirstState)));

        setFirstState(sequenceFirstState);
    }

    /**
     * Adds the "next" state in a sequential state machine setting up the
     * transition to next state automatically. Also sets as first state if one
     * is not already defined.
     *
     * @deprecated Use SequentialStates and addSequence(), instead
     */
    public void addSequential(@NonNull State nextState) {
        checkStateMachineNotRunning();

        Log.d(LOG_TAG, String.format("addSequential(%s)",
                stateClassAndName(nextState)));

        if (lastSequentialState != null) {
            Log.d(LOG_TAG, String.format("addSequential() %s - next -> %s",
                    stateClassAndName(lastSequentialState), stateClassAndName(nextState)));

            lastSequentialState.setNextState(nextState);
        }

        if (firstState == null) {
            Log.d(LOG_TAG, "addSequential() - no current first state, setting first state");
            setFirstState(nextState);
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
        checkStateMachineNotRunning();

        checkFirstStateNotSet();

        Log.d(LOG_TAG, String.format("setFirstState(%s)",
                stateClassAndName(state)));

        firstState = state;
    }

    private void checkFirstStateNotSet() {
        if (firstState != null) {
            throw new IllegalArgumentException("State machine already has the first state set");
        }
    }

    public void addStartDelay(long numberOfSeconds, @NonNull Ticker ticker) {
        checkStateMachineNotRunning();

        StopwatchDelayState startDelay = new StopwatchDelayState("Delayed start",
                telemetry, ticker, numberOfSeconds, TimeUnit.SECONDS);
        startDelay.setNextState(firstState);
        firstState = startDelay;
    }

    public void doOneStateLoop() {
        try {
            if (currentState == null) {
                if (!stateMachineRunning) {
                    currentState = firstState;
                    stateMachineRunning = true;
                } else {
                    Log.w(LOG_TAG, "No state left to execute");
                }
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

                currentState = possibleNextState;
            }

            if (telemetry != null) {
                telemetry.addData("00", String.format("> state %s", currentState != null ? currentState.getName() : "null"));
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

    private void checkStateMachineNotRunning() {
        if (stateMachineRunning) {
            throw new IllegalArgumentException("State machine already running, cannot change configuration");
        }
    }

    static String stateClassAndName(@NonNull State state) {
        return String.format("%s - '%s'", state.getClass().getSimpleName(), state.getName());
    }
}

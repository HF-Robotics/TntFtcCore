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


import org.junit.Assert;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RunnableStateTest {

    StateMachine stateMachine;
    boolean[] flag = new boolean[] { false };

    @BeforeEach
    protected void setUp() throws Exception {
        stateMachine = new StateMachine(null);
    }

    @Test
    public void testNormalOperation() {
        Assert.assertFalse(flag[0]);

        RunnableState runnableState = new RunnableState("test", null,
                new Runnable() {
                    @Override
                    public void run() {
                        flag[0] = true;
                    }
                });
        stateMachine.setFirstState(runnableState);
        stateMachine.doOneStateLoop();

        Assert.assertTrue(flag[0]);
    }

    @Test
    public void testIllegalArguments() throws Exception {

        final Runnable doNothing = new Runnable() {
            @Override
            public void run() {
            }
        };

        Assert.assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                new RunnableState(null, null, doNothing);
            }
        });
    }
}
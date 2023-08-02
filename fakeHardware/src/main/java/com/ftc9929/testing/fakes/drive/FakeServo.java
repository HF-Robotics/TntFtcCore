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

package com.ftc9929.testing.fakes.drive;

import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.ServoImpl;

@SuppressWarnings("unused")
public class FakeServo extends ServoImpl {
    private double servoPosition;

    private int portNumber;

    public FakeServo() {
        this(0);
    }

    public FakeServo(final int portNumber) {
        super(null, portNumber);
        this.portNumber = portNumber;
        controller = new FakeServoController();
    }

    @Override
    public ServoController getController() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public Manufacturer getManufacturer() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public String getDeviceName() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public String getConnectionInfo() {
        throw new IllegalArgumentException("Not implemented");
    }

    class FakeServoController implements ServoController {
        @Override
        public void pwmEnable() {

        }

        @Override
        public void pwmDisable() {

        }

        @Override
        public PwmStatus getPwmStatus() {
            return null;
        }

        @Override
        public void setServoPosition(int servo, double position) {
            servoPosition = position;
        }

        @Override
        public double getServoPosition(int servo) {
            return servoPosition;
        }

        @Override
        public Manufacturer getManufacturer() {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override
        public String getDeviceName() {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override
        public String getConnectionInfo() {
            throw new IllegalArgumentException("Not implemented");
        }

        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public void resetDeviceConfigurationForOpMode() {

        }

        @Override
        public void close() {

        }
    }
}

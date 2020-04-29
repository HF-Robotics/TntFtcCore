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

package com.ftc9929.corelib.drive;

import com.qualcomm.robotcore.util.Range;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Kinematics that converts input powers in x, y axes and rotation around z
 * into motor powers at the four corners of a Mecanum drive.
 *
 * Inspired by code from Titan Robotics Club FTC#3543, and math from
 * https://www.chiefdelphi.com/uploads/default/original/3X/9/3/937da7cbd006480f2b47eb9ee7bd8567b8f22dd9.pdf
 *
 * Extend this class and override the abstract methods to set power levels
 * on the corresponding motors. For an example, see OpenLoopMecanumKinematicsTest.
 */
public abstract class OpenLoopMecanumKinematics {
    public void driveCartesian(double xPower,
                               double yPower,
                               double rotationPower,
                               boolean inverted)
    {
        xPower = Range.clip(xPower, -1.0, 1.0);
        yPower = Range.clip(yPower, -1.0, 1.0);
        rotationPower = Range.clip(rotationPower, -1.0, 1.0);

        if (inverted) {
            xPower = -xPower;
            yPower = -yPower;
        }

        WheelSpeeds wheelSpeeds = WheelSpeeds.builder()
                .leftFront(  xPower + yPower + rotationPower)
                .rightFront(-xPower + yPower - rotationPower)
                .leftRear(  -xPower + yPower + rotationPower)
                .rightRear(  xPower + yPower - rotationPower).build();

        normalizeAndSetMotorPowers(wheelSpeeds);
    }

    private final static double MAX_MOTOR_OUTPUT = 1.0;

    @Builder
    protected static class WheelSpeeds {
        @Getter
        private double leftFront;

        @Getter
        private double rightFront;

        @Getter
        private double leftRear;

        @Getter
        private double rightRear;

        void clipToMaxOutput() {
            leftFront = Range.clip(leftFront, -MAX_MOTOR_OUTPUT, MAX_MOTOR_OUTPUT);
            rightFront = Range.clip(rightFront, -MAX_MOTOR_OUTPUT, MAX_MOTOR_OUTPUT);
            leftRear = Range.clip(leftRear, -MAX_MOTOR_OUTPUT, MAX_MOTOR_OUTPUT);
            rightRear = Range.clip(rightRear, -MAX_MOTOR_OUTPUT, MAX_MOTOR_OUTPUT);
        }

        void normalize() {
            // (1) Find the maximum magnitude of all wheel power values regardless of sign

            // (2) If that value is more than MAX_MOTOR_OUTPUT, scale it so the maximum magnitude
            //     is MAX_MOTOR_OUTPUT and *all other values* are scaled relative to that

            double maxMagnitude = 0;

            if (Math.abs(leftFront) > 1.0) {
                maxMagnitude = Math.abs(leftFront);
            }

            if (Math.abs(rightFront) > 1.0) {
                maxMagnitude = Math.abs(rightFront);
            }

            if (Math.abs(leftRear) > 1.0) {
                maxMagnitude = Math.abs(leftRear);
            }

            if (Math.abs(rightRear) > 1.0) {
                maxMagnitude = Math.abs(rightRear);
            }

            if (maxMagnitude > 1.0) {
                leftFront /= maxMagnitude;
                rightFront /= maxMagnitude;
                leftRear /= maxMagnitude;
                rightRear /= maxMagnitude;
            }
        }

        public String toString() {
            return "Wheel speed: " + leftFront + ", " + rightFront + ", " + leftRear + ", " + rightRear;
        }
    }

    protected void normalizeAndSetMotorPowers(@NonNull WheelSpeeds wheelSpeeds) {
        wheelSpeeds.normalize();
        wheelSpeeds.clipToMaxOutput();
        setMotorPowers(wheelSpeeds);
    }

    protected abstract void setMotorPowers(@NonNull WheelSpeeds wheelSpeeds);
}

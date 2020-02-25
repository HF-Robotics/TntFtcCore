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

import com.ftc9929.testing.fakes.drive.FakeDcMotorEx;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OpenLoopMecanumKinematicsTest {
    private FakeDcMotorEx leftFrontDriveMotor;

    private FakeDcMotorEx leftRearDriveMotor;

    private FakeDcMotorEx rightFrontDriveMotor;

    private FakeDcMotorEx rightRearDriveMotor;

    private KinematicsAdapter mecanumKinematics;

    @Before
    public void setUp() {
        leftFrontDriveMotor = new FakeDcMotorEx();

        leftRearDriveMotor = new FakeDcMotorEx();

        rightFrontDriveMotor = new FakeDcMotorEx();

        rightRearDriveMotor = new FakeDcMotorEx();

        mecanumKinematics = new KinematicsAdapter();
    }

    @Test
    public void openLoopControl() {
        // drive straight forward in the y axis
        mecanumKinematics.driveCartesian(0, 1, 0, false);

        double leftFrontPower = leftFrontDriveMotor.getPower();
        double leftRearPower = leftRearDriveMotor.getPower();
        double rightFrontPower = rightFrontDriveMotor.getPower();
        double rightRearPower = rightRearDriveMotor.getPower();

        Assert.assertEquals(leftFrontPower, leftRearPower, .01);
        Assert.assertEquals(rightFrontPower, rightRearPower, .01);

        // stop all movement or standstill
        mecanumKinematics.driveCartesian(0, 0, 0, false);

        leftFrontPower = leftFrontDriveMotor.getPower();
        leftRearPower = leftRearDriveMotor.getPower();
        rightFrontPower = rightFrontDriveMotor.getPower();
        rightRearPower = rightRearDriveMotor.getPower();

        Assert.assertEquals(0, leftFrontPower, .02);
        Assert.assertEquals(0, leftRearPower, .02);
        Assert.assertEquals(0, rightFrontPower, .02);
        Assert.assertEquals(0, rightRearPower, .02);

        // Rotate clockwise
        mecanumKinematics.driveCartesian(0, 0, -1, false);

        leftFrontPower = leftFrontDriveMotor.getPower();
        leftRearPower = leftRearDriveMotor.getPower();
        rightFrontPower = rightFrontDriveMotor.getPower();
        rightRearPower = rightRearDriveMotor.getPower();

        Assert.assertTrue(leftFrontPower < 0);
        Assert.assertTrue(leftRearPower < 0);
        Assert.assertTrue(rightFrontPower > 0);
        Assert.assertTrue(rightRearPower > 0);

        // Strafe to the left
        mecanumKinematics.driveCartesian(-1, 0, 0, false);

        leftFrontPower = leftFrontDriveMotor.getPower();
        leftRearPower = leftRearDriveMotor.getPower();
        rightFrontPower = rightFrontDriveMotor.getPower();
        rightRearPower = rightRearDriveMotor.getPower();

        Assert.assertTrue(leftFrontPower < 0);
        Assert.assertTrue(leftRearPower > 0);
        Assert.assertTrue(rightFrontPower > 0);
        Assert.assertTrue(rightRearPower < 0);

        // Strafe to the right
        mecanumKinematics.driveCartesian(1, 0, 0, false);

        leftFrontPower = leftFrontDriveMotor.getPower();
        leftRearPower = leftRearDriveMotor.getPower();
        rightFrontPower = rightFrontDriveMotor.getPower();
        rightRearPower = rightRearDriveMotor.getPower();

        Assert.assertTrue(leftFrontPower > 0);
        Assert.assertTrue(leftRearPower < 0);
        Assert.assertTrue(rightFrontPower < 0);
        Assert.assertTrue(rightRearPower > 0);

    }

    class KinematicsAdapter extends OpenLoopMecanumKinematics {

        @Override
        protected void setMotorPowers(WheelSpeeds wheelSpeeds) {
            leftFrontDriveMotor.setPower(wheelSpeeds.leftFront);

            leftRearDriveMotor.setPower(wheelSpeeds.leftRear);

            rightFrontDriveMotor.setPower(wheelSpeeds.rightFront);

            rightRearDriveMotor.setPower(wheelSpeeds.rightRear);
        }
    }
}

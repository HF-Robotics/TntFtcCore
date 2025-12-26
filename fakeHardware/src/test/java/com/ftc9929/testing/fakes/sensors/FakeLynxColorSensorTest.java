/*
 Copyright (c) 2025 The Tech Ninja Team (https://ftc9929.com)

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

package com.ftc9929.testing.fakes.sensors;

import static junit.framework.Assert.assertEquals;

import com.ftc9929.testing.fakes.util.FakeHardwareMapFactory;
import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.junit.Test;

public class FakeLynxColorSensorTest {
    @Test
    public void simple() {
        HardwareMap fakeHwMap = FakeHardwareMapFactory.getFakeHardwareMap("sample_hardware_map.xml");

        LynxI2cColorRangeSensor colorSensor = fakeHwMap.get(LynxI2cColorRangeSensor.class, "TestColorSensor");

        ((FakeLynxI2cColorRangeSensor) colorSensor).setRed(32);
        assertEquals(32, colorSensor.red());

        ((FakeLynxI2cColorRangeSensor) colorSensor).setGreen(64);
        assertEquals(64, colorSensor.green());

        ((FakeLynxI2cColorRangeSensor) colorSensor).setBlue(128);
        assertEquals(128, colorSensor.blue());

        ((FakeLynxI2cColorRangeSensor) colorSensor).setAlpha(255);
        assertEquals(255, colorSensor.alpha());

        ((FakeLynxI2cColorRangeSensor) colorSensor).setDistanceCm(10);
        assertEquals(.1, colorSensor.getDistance(DistanceUnit.METER), 0.001);

    }
}

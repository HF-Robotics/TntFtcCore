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
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.junit.Test;

public class FakeAnalogInputTest {
    @Test
    public void simple() {
        HardwareMap fakeHwMap = FakeHardwareMapFactory.getFakeHardwareMap("sample_hardware_map.xml");

        AnalogInput analogInput = fakeHwMap.get(AnalogInput.class, "TestAnalogInput");

        ((FakeAnalogInput) analogInput).setVoltage(2.0);
        assertEquals(2.0, analogInput.getVoltage(), 0.001D);

        ((FakeAnalogInput) analogInput).setVoltage(0.0);
        assertEquals(0.0, analogInput.getVoltage(), 0.001D);
    }
}

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

package com.ftc9929.testing.fakes.util;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

public class FakeHardwareMapTest {
    @Test
    public void testLoadingExistingHardwareMap() throws Exception {

       HardwareMap fakeHwMap = FakeHardwareMapFactory.getFakeHardwareMap("sample_hardware_map.xml");
        List<DcMotorEx> motors = fakeHwMap.getAll(DcMotorEx.class);
        assertDevicesPresent(fakeHwMap, DcMotorEx.class, 1);
        assertDevicesPresent(fakeHwMap, TouchSensor.class, 1);
        assertDevicesPresent(fakeHwMap, DigitalChannel.class, 2); // because our fake TS is a DC
    }

    private void assertDevicesPresent(final HardwareMap fakeHwMap,
                                      final Class deviceClass,
                                      final int count) {
        List devices = fakeHwMap.getAll(deviceClass);
        Assertions.assertEquals(count, devices.size(), new Supplier<String>() {
                    @Override
                    public String get() {
                        return String.format("Expected a list of %d %s", count, deviceClass);
                    }
                });
    }
}

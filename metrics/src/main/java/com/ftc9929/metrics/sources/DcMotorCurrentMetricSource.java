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

package com.ftc9929.metrics.sources;

import com.ftc9929.corelib.util.NamedDeviceMap;
import com.ftc9929.metrics.GaugeMetricSource;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class DcMotorCurrentMetricSource implements GaugeMetricSource {
    private final DcMotorEx dcMotorEx;

    private final String name;

    public DcMotorCurrentMetricSource(NamedDeviceMap.NamedDevice<DcMotorEx> namedMotor) {
        this.dcMotorEx = namedMotor.getDevice();

        name = String.format("dcm_curr_%s", namedMotor.getName());

    }

    @Override
    public String getSampleName() {
        return name;
    }

    @Override
    public double getValue() {
        return dcMotorEx.getCurrent(CurrentUnit.AMPS);
    }
}

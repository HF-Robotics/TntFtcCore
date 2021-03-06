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

import com.ftc9929.metrics.GaugeMetricSource;
import com.qualcomm.hardware.lynx.LynxModule;

import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Voltage5VMetricSource implements GaugeMetricSource {

    private final LynxModule lynxModule;

    private final String name;

    public Voltage5VMetricSource(LynxModule lynxModule) {
        this.lynxModule = lynxModule;
        int moduleAddress = lynxModule.getModuleAddress();

        name = String.format("hub_%d_5V", moduleAddress);
    }

    @Override
    public String getSampleName() {
        return name;
    }

    @Override
    public double getValue() {
        return lynxModule.getAuxiliaryVoltage(VoltageUnit.VOLTS);
    }
}

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
import com.ftc9929.metrics.MetricsSampler;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

;

@EqualsAndHashCode
public class MotorVelocityMetricSource implements GaugeMetricSource {
    private final DcMotorEx motor;

    private final String sampleName;

    private int lastEncoderValue;

    private long lastValueTimestamp;

    public MotorVelocityMetricSource(@NonNull final NamedDeviceMap.NamedDevice<DcMotorEx> namedMotor) {
        this.motor = namedMotor.getDevice();

        sampleName = String.format("dcm_vel_%s", namedMotor.getName());
        lastEncoderValue = motor.getCurrentPosition();
        lastValueTimestamp = System.currentTimeMillis();
    }

    @Override
    public String getSampleName() {
        return sampleName;
    }

    @Override
    public double getValue() {
        int newEncoderValue = motor.getCurrentPosition();
        double deltaEncoderPosition = newEncoderValue - lastEncoderValue;
        lastEncoderValue = newEncoderValue;

        long now = System.currentTimeMillis();
        double deltaTimeMillis = now - lastValueTimestamp;
        lastValueTimestamp = now;

        if (deltaTimeMillis == 0) {
            return MetricsSampler.NO_REPORT_VALUE;
        }

        return (deltaEncoderPosition / deltaTimeMillis) * 1000;
    }
}

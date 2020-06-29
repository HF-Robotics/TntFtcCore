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

package com.ftc9929.metrics;

import android.util.Log;

import com.ftc9929.corelib.control.NinjaGamePad;
import com.ftc9929.corelib.control.OnOffButton;
import com.ftc9929.corelib.control.RangeInput;
import com.ftc9929.corelib.util.NamedDeviceMap;
import com.ftc9929.metrics.sources.DcMotorCurrentMetricSource;
import com.ftc9929.metrics.sources.DcMotorPowerMetricSource;
import com.ftc9929.metrics.sources.DigitalChannelMetricSource;
import com.ftc9929.metrics.sources.MotorVelocityMetricSource;
import com.ftc9929.metrics.sources.OnOffButtonMetricSource;
import com.ftc9929.metrics.sources.RangeInputMetricSource;
import com.ftc9929.metrics.sources.ServoMetricSource;
import com.ftc9929.metrics.sources.Voltage12VMetricSource;
import com.ftc9929.metrics.sources.Voltage5VMetricSource;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.NonNull;

import static com.ftc9929.corelib.Constants.LOG_TAG;

/**
 * A MetricsSampler that will read values from the gamepads (if used) and the hardware
 * found in the hardware map for an FTC Robot, and report them by the configured MetricsReporter
 */
public class RobotMetricsSampler implements MetricsSampler {

    private final Set<GaugeMetricSource> gaugeSources = new LinkedHashSet<>();

    private final HardwareMap hardwareMap;

    private final NamedDeviceMap namedDeviceMap;

    private final List<LynxModule> expansionHubs;

    private final MetricsReporter metricsReporter;

    @Builder
    private RobotMetricsSampler(@NonNull HardwareMap hardwareMap,
                                NinjaGamePad driverControls, NinjaGamePad operatorControls,
                                @NonNull MetricsReporter metricsReporter) {
        this.metricsReporter = metricsReporter;
        this.hardwareMap = hardwareMap;
        this.namedDeviceMap = new NamedDeviceMap(hardwareMap);

        expansionHubs = hardwareMap.getAll(LynxModule.class);

        addAllByHardwareMap();

        if (driverControls != null) {
            addGamepad("drv", driverControls);
        } else {
            Log.d(LOG_TAG, "no driver controls specified, no metrics will be sent for it");
        }

        if (operatorControls != null) {
            addGamepad("opr", operatorControls);
        } else {
            Log.d(LOG_TAG, "no operator controls specified, no metrics will be sent for it");
        }
    }

    @Override
    public void doSamples() {
        metricsReporter.reportMetrics(gaugeSources);
    }

    @Override
    public void addSource(GaugeMetricSource metricSource) {
        gaugeSources.add(metricSource);
    }

    private void addAllByHardwareMap() {
        addDcMotors();
        addVoltages();
        addDigitalChannels();
        addServos();
    }

    @Override
    public void addGamepad(@NonNull String name, @NonNull NinjaGamePad gamepad) {
        RangeInput leftStickX = gamepad.getLeftStickX();
        addSource(toInterestingValues(new RangeInputMetricSource(leftStickX, name, "l_x")));
        RangeInput leftStickY = gamepad.getLeftStickY();
        addSource(toInterestingValues(new RangeInputMetricSource (leftStickY, name, "l_y")));

        RangeInput rightStickX = gamepad.getRightStickX();
        addSource(toInterestingValues(new RangeInputMetricSource (rightStickX, name, "r_x")));
        RangeInput rightStickY = gamepad.getRightStickY();
        addSource(toInterestingValues(new RangeInputMetricSource (rightStickY, name, "r_y")));

        RangeInput leftTrigger = gamepad.getLeftTrigger();
        addSource(toInterestingValues(new RangeInputMetricSource (leftTrigger, name, "l_trigger")));
        RangeInput rightTrigger = gamepad.getRightTrigger();
        addSource(toInterestingValues(new RangeInputMetricSource (rightTrigger, name, "r_trigger")));

        OnOffButton leftBumper = gamepad.getLeftBumper();
        addSource(toInterestingValues(new OnOffButtonMetricSource(leftBumper, name, "l_bumper")));
        OnOffButton rightBumper = gamepad.getRightBumper();
        addSource(toInterestingValues(new OnOffButtonMetricSource (rightBumper, name, "r_bumper")));

        OnOffButton aButton = gamepad.getAButton();
        addSource(toInterestingValues(new OnOffButtonMetricSource (aButton , name, "a_btn")));
        OnOffButton bButton = gamepad.getBButton();
        addSource(toInterestingValues(new OnOffButtonMetricSource (bButton, name, "b_btn")));
        OnOffButton xButton = gamepad.getXButton();
        addSource(toInterestingValues(new OnOffButtonMetricSource (xButton , name, "x_btn")));
        OnOffButton yButton = gamepad.getYButton();
        addSource(toInterestingValues(new OnOffButtonMetricSource (yButton, name, "y_btn")));

        OnOffButton dpadUp = gamepad.getDpadUp();
        addSource(toInterestingValues(new OnOffButtonMetricSource (dpadUp , name, "dpad_up")));
        OnOffButton dpadDown = gamepad.getDpadDown();
        addSource(toInterestingValues(new OnOffButtonMetricSource (dpadDown , name, "dpad_down")));
        OnOffButton dpadLeft = gamepad.getDpadLeft();
        addSource(toInterestingValues(new OnOffButtonMetricSource (dpadLeft , name, "dpad_left")));
        OnOffButton dpadRight = gamepad.getDpadRight();
        addSource(toInterestingValues(new OnOffButtonMetricSource (dpadRight , name, "dpad_right")));
    }

    private void addDcMotors() {
        List<NamedDeviceMap.NamedDevice<DcMotorEx>> allMotors = namedDeviceMap.getAll(DcMotorEx.class);

        for (NamedDeviceMap.NamedDevice<DcMotorEx> namedMotor : allMotors) {
            GaugeMetricSource metricSource = new DcMotorPowerMetricSource(namedMotor);
            addSource(toInterestingValues(metricSource));

            metricSource = new MotorVelocityMetricSource(namedMotor);
            addSource(toInterestingValues(metricSource));

            metricSource = new DcMotorCurrentMetricSource(namedMotor);
            addSource(toInterestingValues(metricSource));
        }
    }

    private void addVoltages() {
        for (LynxModule hub : expansionHubs) {
            GaugeMetricSource metricSource = new Voltage5VMetricSource(hub);
            addSource(metricSource);

            metricSource = new Voltage12VMetricSource(hub);
            addSource(toInterestingValues(metricSource));
        }
    }

    private void addDigitalChannels() {
        List<NamedDeviceMap.NamedDevice<DigitalChannel>> allDigitalChannels = namedDeviceMap.getAll(DigitalChannel.class);

        for (NamedDeviceMap.NamedDevice<DigitalChannel> namedDigitalChannel : allDigitalChannels) {
            GaugeMetricSource metricSource = new DigitalChannelMetricSource(namedDigitalChannel);
            addSource(toInterestingValues(metricSource));
        }
    }

    private void addServos() {
        List<NamedDeviceMap.NamedDevice<Servo>> allServos = namedDeviceMap.getAll(Servo.class);

        for (NamedDeviceMap.NamedDevice<Servo> namedServo : allServos) {
            GaugeMetricSource metricSource = new ServoMetricSource(namedServo);
            addSource(toInterestingValues(metricSource));
        }
    }

    private GaugeMetricSource toInterestingValues(GaugeMetricSource originalSource) {
        return new InterestingValueMetricSource(originalSource, 5, 10, 0.001);
    }
}

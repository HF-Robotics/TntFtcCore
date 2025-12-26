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

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchSimple;
import com.qualcomm.robotcore.hardware.I2cWaitControl;
import com.qualcomm.robotcore.hardware.TimestampedData;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import lombok.Setter;

public class FakeLynxI2cColorRangeSensor extends LynxI2cColorRangeSensor {
    @Setter
    private int red;

    @Setter
    private int green;

    @Setter
    private int blue;

    @Setter
    private int alpha;

    @Setter
    private double distanceCm;

    public FakeLynxI2cColorRangeSensor() {
        super(new I2cDeviceSynchSimple() {
            @Override
            public I2cAddr getI2cAddress() {
                return null;
            }

            @Override
            public void setI2cAddress(I2cAddr newAddress) {

            }

            @Override
            public void setHealthStatus(HealthStatus status) {

            }

            @Override
            public HealthStatus getHealthStatus() {
                return null;
            }

            @Override
            public Manufacturer getManufacturer() {
                return null;
            }

            @Override
            public String getDeviceName() {
                return "";
            }

            @Override
            public String getConnectionInfo() {
                return "";
            }

            @Override
            public int getVersion() {
                return 0;
            }

            @Override
            public void resetDeviceConfigurationForOpMode() {

            }

            @Override
            public void close() {

            }

            @Override
            public void setUserConfiguredName(String name) {

            }

            @Override
            public String getUserConfiguredName() {
                return "";
            }

            @Override
            public byte read8() {
                return 0;
            }

            @Override
            public byte read8(int ireg) {
                return 0;
            }

            @Override
            public byte[] read(int creg) {
                return new byte[0];
            }

            @Override
            public byte[] read(int ireg, int creg) {
                return new byte[0];
            }

            @Override
            public TimestampedData readTimeStamped(int creg) {
                return null;
            }

            @Override
            public TimestampedData readTimeStamped(int ireg, int creg) {
                return null;
            }

            @Override
            public void write8(int bVal) {

            }

            @Override
            public void write8(int ireg, int bVal) {

            }

            @Override
            public void write(byte[] data) {

            }

            @Override
            public void write(int ireg, byte[] data) {

            }

            @Override
            public void write8(int bVal, I2cWaitControl waitControl) {

            }

            @Override
            public void write8(int ireg, int bVal, I2cWaitControl waitControl) {

            }

            @Override
            public void write(byte[] data, I2cWaitControl waitControl) {

            }

            @Override
            public void write(int ireg, byte[] data, I2cWaitControl waitControl) {

            }

            @Override
            public void waitForWriteCompletions(I2cWaitControl waitControl) {

            }

            @Override
            public void enableWriteCoalescing(boolean enable) {

            }

            @Override
            public boolean isWriteCoalescingEnabled() {
                return false;
            }

            @Override
            public boolean isArmed() {
                return false;
            }

            @Override
            public void setI2cAddr(I2cAddr i2cAddr) {

            }

            @Override
            public I2cAddr getI2cAddr() {
                return null;
            }

            @Override
            public void setLogging(boolean enabled) {

            }

            @Override
            public boolean getLogging() {
                return false;
            }

            @Override
            public void setLoggingTag(String loggingTag) {

            }

            @Override
            public String getLoggingTag() {
                return "";
            }
        }, false);
    }

    @Override
    public synchronized int red() {
        return red;
    }

    @Override
    public synchronized int green() {
        return green;
    }

    @Override
    public synchronized int blue() {
        return blue;
    }

    @Override
    public synchronized int alpha() {
        return alpha;
    }

    @Override
    public double getDistance(final DistanceUnit unit) {
        return unit.fromUnit(DistanceUnit.CM, distanceCm);
    }
}

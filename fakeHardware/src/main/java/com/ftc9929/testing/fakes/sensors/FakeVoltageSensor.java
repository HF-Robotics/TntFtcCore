package com.ftc9929.testing.fakes.sensors;

import com.qualcomm.robotcore.hardware.VoltageSensor;

public class FakeVoltageSensor implements VoltageSensor {

    private double voltage = 12;

    @Override
    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double newVoltage) {
        voltage = newVoltage;
    }

    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Other;
    }

    @Override
    public String getDeviceName() {
        return "TNT Fake Voltage Sensor";
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
}

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

package com.ftc9929.testing.fakes.util;

import com.ftc9929.testing.fakes.drive.FakeCRServo;
import com.ftc9929.testing.fakes.drive.FakeDcMotorEx;
import com.ftc9929.testing.fakes.drive.FakeRevBlinkinLedDriver;
import com.ftc9929.testing.fakes.drive.FakeServo;
import com.ftc9929.testing.fakes.sensors.FakeAnalogInput;
import com.ftc9929.testing.fakes.sensors.FakeDigitalChannel;
import com.ftc9929.testing.fakes.sensors.FakeDistanceSensor;
import com.ftc9929.testing.fakes.sensors.FakeLynxI2cColorRangeSensor;
import com.ftc9929.testing.fakes.sensors.FakeRevTouchSensor;
import com.ftc9929.testing.fakes.sensors.FakeVoltageSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lombok.NonNull;
import lombok.SneakyThrows;

public class FakeHardwareMapFactory {

    public static final String MOTOR_TAG_NAME = "Motor";
    public static final String SERVO_TAG_NAME = "Servo";
    public static final String LYNX_USB_DEVICE_TAG_NAME = "LynxUsbDevice";
    public static final String ROBOT_TAG_NAME = "Robot";
    public static final String LYNX_MODULE_TAG_NAME = "LynxModule";
    public static final String REV_DISTANCE_SENSOR_TAG_NAME = "REV_VL53L0X_RANGE_SENSOR";
    public static final String DIGITAL_DEVICE_TAG_NAME = "DigitalDevice";
    public static final String REV_TOUCH_SENSOR_TAG_NAME = "RevTouchSensor";
    public static final String REV_BLINKINLED_DRIVER_TAG_NAME = "RevBlinkinLedDriver";
    public static final String CR_SERVO_TAG_NAME = "ContinuousRotationServo";
    public static final String ANALOG_INPUT_TAG_NAME = "AnalogInput";
    public static final String LYNX_COLOR_SENSOR_TAG_NAME = "LynxColorSensor";

    /**
     * Loads the hardware map with the name &quot;hardwareMapName&quot; from the location used by
     * the FTC SDK when storing hardware map XML files alongside your robot sourcecode and provides
     * fake implementations for the devices found.
     *
     * @param hardwareMapName the name of the hardware map XML file
     * @return a HardwareMap that provides fake implementations for devices found in the file
     */
    @SneakyThrows
    public static HardwareMap getFakeHardwareMap(@NonNull final String hardwareMapName)  {
        String path = String.format("src/main/res/xml/%s", hardwareMapName);

        return getFakeHardwareMap(new File(path));
    }

    /**
     * Loads the hardware map from the given path
     */
    @SneakyThrows
    public static HardwareMap getFakeHardwareMap(@NonNull final File hardwareMapFile) {
        HardwareMapCreator hwMapCreator = new HardwareMapCreator();

        if (!hardwareMapFile.exists()) {
            throw new IOException("Hardware map file '" + hardwareMapFile.getAbsolutePath() + "' does not exist");
        }

        if (!hardwareMapFile.canRead()) {
            throw new IOException("Hardware map file '" + hardwareMapFile.getAbsolutePath() + "' is not readable");
        }

        hwMapCreator.parseUsingDocBuilder(new FileInputStream(hardwareMapFile));

        return hwMapCreator.hardwareMap;
    }

    private static class HardwareMapCreator {
        private Set<String> deviceNames = new HashSet<>();

        private HardwareMap hardwareMap = new HardwareMap(null, new OpModeManagerNotifier() {
            @Override
            public OpMode registerListener(Notifications listener) {
                return null;
            }

            @Override
            public void unregisterListener(Notifications listener) {

            }
        }) {

            @Nullable
            @Override
            public <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
                synchronized (lock) {
                    deviceName = deviceName.trim();
                    List<HardwareDevice> list = allDevicesMap.get(deviceName);
                    @Nullable T result = null;

                    if (list != null) {
                        for (HardwareDevice device : list) {
                            if (classOrInterface.isInstance(device)) {
                                result = classOrInterface.cast(device);
                                break;
                            }
                        }
                    }

                    return result;
                }
            }
        };

        @SneakyThrows
        private void parseUsingDocBuilder(InputStream fileInput) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(fileInput);

            // FIXME: Create LynxUsbModules by "lookback" from device nodes so that we can
            //        build fake motor controllers and the like eventually
            addAllMotors(doc);
            addAllServos(doc);

            addAllDigitalChannels(doc);
            addAllAnalogInputs(doc);
            addAllLynxColorSensors(doc);
            addAllRevTouchSensors(doc);
            addAllRevBlinkinledDrivers(doc);

            addAllDistanceSensors(doc);

            // FIXME: Add implementations for things we don't support, but need:
            // IMU, LynxColorSensor, RevColorSensorV3, AnalogInput

            hardwareMap.voltageSensor.put("Voltage Sensor", new FakeVoltageSensor());
        }

        private void addAllDistanceSensors(Document doc) {
            NodeList distanceSensors = doc.getElementsByTagName(REV_DISTANCE_SENSOR_TAG_NAME);

            addDevices(distanceSensors, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeDistanceSensor fakeDistanceSensor = new FakeDistanceSensor();

                    hardwareMap.put(name, fakeDistanceSensor);

                    // No DeviceMapping for the Rev2mDistanceSensor
                }
            });
        }

        private void addAllDigitalChannels(Document doc) {
            NodeList digitalChannels = doc.getElementsByTagName(DIGITAL_DEVICE_TAG_NAME);

            addDevices(digitalChannels, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeDigitalChannel fakeDigitalChannel = new FakeDigitalChannel();

                    hardwareMap.put(name, fakeDigitalChannel);

                    hardwareMap.digitalChannel.put(name, fakeDigitalChannel);
                }
            });
        }

        private void addAllAnalogInputs(Document doc) {
            NodeList analogInputs = doc.getElementsByTagName(ANALOG_INPUT_TAG_NAME);

            addDevices(analogInputs, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeAnalogInput fakeAnalogInput = new FakeAnalogInput();

                    hardwareMap.put(name, fakeAnalogInput);
                    hardwareMap.analogInput.put(name, fakeAnalogInput);
                }
            });
        }

        private void addAllLynxColorSensors(Document doc) {
            NodeList lynxColorSensors = doc.getElementsByTagName(LYNX_COLOR_SENSOR_TAG_NAME);

            addDevices(lynxColorSensors, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeLynxI2cColorRangeSensor fakeColorSensor = new FakeLynxI2cColorRangeSensor();

                    hardwareMap.put(name, fakeColorSensor);
                    hardwareMap.colorSensor.put(name, fakeColorSensor);
                }
            });
        }

        private void addAllRevTouchSensors(Document doc) {
            NodeList revTouchSensors = doc.getElementsByTagName(REV_TOUCH_SENSOR_TAG_NAME);

            addDevices(revTouchSensors, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeRevTouchSensor fakeRevTouchSensor = new FakeRevTouchSensor(portNumber);

                    hardwareMap.put(name, fakeRevTouchSensor);

                    hardwareMap.touchSensor.put(name, fakeRevTouchSensor);
                }
            });
        }

        private void addAllRevBlinkinledDrivers(Document doc) {
            NodeList blinkinLeds = doc.getElementsByTagName(REV_BLINKINLED_DRIVER_TAG_NAME);

            addDevices(blinkinLeds, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    hardwareMap.put(name, new FakeRevBlinkinLedDriver(portNumber));
                }
            });
        }

        private void addAllMotors(Document doc) {
            // FIXME: There are other motor tag names:
            // Bonus points - how do you add these without a lot of copy-and-paste code?
            //                NeveRest3.7v1Gearmotor
            //                NeveRest20Gearmotor
            //                NeveRest40Gearmotor
            //                NeveRest60Gearmotor
            //                Matrix12vMotor
            //                TetrixMotor
            //                goBILDA5201SeriesMotor name="[...]" port="[...]" />
            //                goBILDA5202SeriesMotor
            //                RevRobotics20HDHexMotor name="[...]" port="[...]" />
            //                RevRobotics40HDHexMotor name="[...]" port="[...]" />
            //                RevRoboticsCoreHexMotor name="[...]" port="[...]" />

            NodeList dcMotors = doc.getElementsByTagName(MOTOR_TAG_NAME);

            addDevices(dcMotors, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeDcMotorEx fakeDcMotorEx = new FakeDcMotorEx();

                    hardwareMap.put(name, fakeDcMotorEx);

                    hardwareMap.dcMotor.put(name, fakeDcMotorEx);
                }
            });
        }

        private void addAllServos(Document doc) {
            // FIXME: We don't yet support ContinuousRotationServo or RevSPARKMini
            NodeList servos = doc.getElementsByTagName(SERVO_TAG_NAME);

            addDevices(servos, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeServo fakeServo = new FakeServo(portNumber);

                    hardwareMap.put(name, fakeServo);

                    hardwareMap.servo.put(name, fakeServo);
                }
            });

            NodeList crServos = doc.getElementsByTagName(CR_SERVO_TAG_NAME);

            addDevices(crServos, new DeviceFromXml() {
                @Override
                public void addDeviceToHardwareMap(String name, int portNumber) {
                    final FakeCRServo fakeCrServo = new FakeCRServo();

                    hardwareMap.put(name, fakeCrServo);

                    hardwareMap.crservo.put(name, fakeCrServo);
                }
            });
        }

        private void addDevices(NodeList deviceNodeList, DeviceFromXml deviceAdder) {
            for (int i = 0; i < deviceNodeList.getLength(); i++) {
                Node deviceNode = deviceNodeList.item(i);
                NamedNodeMap attributesByName = deviceNode.getAttributes();

                Node nameNode = attributesByName.getNamedItem("name");
                String nameValue = nameNode.getNodeValue();

                if (nameValue != null) {
                    nameValue = nameValue.trim();
                }

                if (deviceNames.contains(nameValue)) {
                    // This isn't exactly real hardware map behavior, but it prevents
                    // problems at runtime if you are using
                    throw new IllegalArgumentException(String.format(
                            "Non unique device name '%s' for device type '%s'",
                            nameValue, deviceNode.getNodeName()));
                }

                deviceNames.add(nameValue);

                Node portNode = attributesByName.getNamedItem("port");
                int portValue = Integer.valueOf(portNode.getNodeValue());

                deviceAdder.addDeviceToHardwareMap(nameValue, portValue);
            }
        }
    }

    interface DeviceFromXml {
        void addDeviceToHardwareMap(String name, int portNumber);
    }
}

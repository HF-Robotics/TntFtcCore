![Logo of the project](https://raw.githubusercontent.com/HF-Robotics/TntFtcCore/master/doc/images/Ftc9929GithubLogo.png)

# TNT FTC Core Library
> A set of useful classes for building software for your FTC robot.

This library is the culmination of five seasons of work in FTC. The elements of this library were created to circumvent common issues found where the code meets the robot, making common tasks easier, and give more flexibility to robot testing.  

Capabilities and features includes expanded button functionality [`NinjaGamePad`,`OnOffButton`,`RangeInput`, `RangeInputButton`,`DebouncedButton`,`AnyButton` `ToggledButton`], expanded state machine functionality [`StateMachine`, `State`,  `StopwatchDelayState`,  `StopwatchTimeoutSafetyState`, `ServoPositionState`, `RunnableState`, `ButtonPressWithTimeoutState`, `ToggleState`], added common use features to make a safer and more reliable robot [`LowPassFilteredRangeInput`, `ParametricScaledRangeInput`,`StallDetector`, `StopwatchTimeoutSafetyState`, `ButtonPressWithTimeoutState`], and added fakes for software based testing [`FakeDcMotorEx`, `FakeServo`,`FakeDigitalChannel`, `FakeDistanceSensor`].

We have used the game controller code for five seasons. It has enabled safer and more diverse usage of buttons. When paired with the state machine features, which have been in use for five seasons, the team was able to implement complex automatic processes tied to a single button press.

The fake implementations of common FTC hardware have been part of our tool belt for two seasons and has enabled testing before the completion of the physical mechanism was complete. This eliminated some of the "stall" time that we often faced in testing. Elements used to improve the safety and longevity of the robot have been implement for four seasons and help avoid common problems amongst the moving parts of the robot.   

We hope you find this code useful. We certainly have.         


## Installing / Getting started

The components of the TNT FTC Core library are available via jitpack.io. To use
them, first add the following to build.gradle in the TeamCode directory:

```Groovy
repositories {
    // The following repositories are needed for TNT FTC Core dependencies:
    maven { url 'http://repo1.maven.org/maven2' }
    // other repositories
    jcenter()
    google()

    // Add the following line to access the TNT FTC Core library components
    maven { url 'https://jitpack.io' }
}
```

Next, add a dependencies section in build.gradle in the TeamCode directory, replacing [version] with a version from the releases listed at https://github.com/HF-Robotics/TntFtcCore/releases, or for more advanced use, the branch name followed by -SNAPSHOT which will reference the tip of the branch, or a git commit hash (see https://jitpack.io/docs/ for more information):

```Groovy
dependencies {
    // Add this line to use the core class library 'com.ftc9929.TntFtcCore:metrics:[version]'

    // Add this line to use fake hardware implementations used for unit testing
    testImplementation 'com.ftc9929.TntFtcCore:fakeHardware:[version]'
}
```

## Features

- Classes that make common FTC tasks, easier:
    - Gamepad classes in the `com.ftc9929.corelib.control` package:
         - `NinjaGamePad` - a more usable/adaptable game pad class. Source of `OnOffButton`s and `RangeInput`s for those present on an FTC Gamepad.
         - `OnOffButton` - represents a digital (on/off) input on the gamepad.
         - `RangeInput` - represents an analog (joystick, trigger) input on the gamepad.
         - `RangeInputButton` - converts a `RangeInput` to an `OnOffButton`
         - `DebouncedButton` - turns an `OnOffButton` into a button which will not "bounce" for each loop cycle (calling getRise() for example will return true if the button is pressed, and it isn't the same state (pressed) since the last time getRise() has been called.
         - `AnyButton` - makes a set of `OnOffButton` act as one.
         - `ToggledButton` - a button that toggles state on each press of an internally-debounced button.
         - `LowPassFilteredRangeInput` - applies a low pass filter to the given RangeInput to remove noise (spikes).
         - `ParametricScaledRangeInput` - applies a parametric curve to the given RangeInput.
    - Also in the `com.ftc9929.corelib.control` package:
         - `StallDetector` - Detects lack of change in an observed value over a time window, for example, it can be used to detect if a motor is stalled.
    - An object-oriented state machine and some basic state implementations in the `com.ftc9929.corelib.state` package:
         - `StateMachine` the state machine runner, also enables debugging/stepping from the drivers' station
         - `State` base class for any `StateMachine` states
         - `StopwatchDelayState` - A delay state that takes a `com.google.common.base.Ticker` as the time source, to make it possible to advance the elapsed time with a test ticker for unit testing a state machine.
         - `StopwatchTimeoutSafetyState` - Base `State` class which can be used to build a `State` which has a safety timeout, uses a `com.google.common.base.Ticker` to allow testing of timeouts without waiting for elapsed wall clock time.
         - `ServoPositionState` - a simple `State` implementation that puts the given servo in a given position and then transitions to the next `State`.
         - `RunnableState` - takes a `java.lang.Runnable` and turns it into a `StateMachine` state.
         - `ButtonPressWithTimeoutState` - Transitions to a `State` when a `DebouncedButton` has been pressed, or after a timeout, transition to another `State`.
         - `ToggleState` - A state that will perform an action when a `DebouncedButton` press causes a toggling effect (one press for "on", one press for "off").

- Implementations of FTC hardware to be used while unit testing:
    - Fakes for FTC's `DcMotorEx` and `Servo` interfaces in the `com.ftc9929.testing.fakes.drive` package:
        - `FakeDcMotorEx`
        - `FakeServo`
    - Fakes for some of the sensors in the FTC SDK in the `com.ftc9929.testing.fakes.sensors` package:
        - `FakeDigitalChannel`
        - `FakeDistanceSensor`
    - A unit-testable `Telemetry` implementation -com.ftc9929.testing.fakes.FakeTelemetry (also allows asserting Telemetry output)

## Contributing

If you'd like to contribute, please fork the repository and use a feature
branch. Pull requests are welcome.

## Links

- Project homepage: https://github.com/HF-Robotics/TntFtcCore
- Repository: https://github.com/HF-Robotics/TntFtcCore.git
- Issue tracker: https://github.com/HF-Robotics/TntFtcCore/issues
- Related projects:
  - FIRST Tech Challenge: https://github.com/FIRST-Tech-Challenge/


## Licensing

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

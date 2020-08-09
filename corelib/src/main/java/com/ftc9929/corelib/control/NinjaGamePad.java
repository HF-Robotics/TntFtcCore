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

package com.ftc9929.corelib.control;

import com.google.common.collect.ImmutableSet;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.Set;

/**
 * A more usable/adaptable game pad class. Creates OnOff and RangeInput buttons
 * for those present on an FTC Gamepad.
 */
public class NinjaGamePad {
    private final Gamepad gamepad;

    public NinjaGamePad(final Gamepad originalGamepad) {
        gamepad = originalGamepad;
    }

    public OnOffButton getAButton(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.a;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getBButton(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.b;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getXButton() {
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.x;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getYButton(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.y;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getLeftBumper(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.left_bumper;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getRightBumper(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.right_bumper;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getDpadUp(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.dpad_up;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getDpadDown(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.dpad_down;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getDpadLeft(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.dpad_left;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getDpadRight(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.dpad_right;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getLeftStickButton(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.left_stick_button;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public OnOffButton getRightStickButton(){
        return new OnOffButton() {
            private DebouncedButton debounced;

            @Override
            public boolean isPressed() {
                return gamepad.right_stick_button;
            }

            @Override
            public DebouncedButton debounced() {
                if (debounced == null) {
                    debounced = new DebouncedButton(this);
                }

                return debounced;
            }
        };
    }

    public Set<OnOffButton> getAllButtons() {
        return ImmutableSet.of(
                getRightStickButton(), getLeftStickButton(),
                getDpadUp(), getDpadDown(), getDpadLeft(), getDpadRight(),
                getRightBumper(), getLeftBumper(),
                getXButton(), getYButton(), getAButton(), getBButton());
    }

    public RangeInput getLeftStickY(){
        return new RangeInput() {
            @Override
            public float getPosition() {
                return gamepad.left_stick_y;
            }

            @Override
            public float getMaxPosition() {
                return 1.0f;
            }

            @Override
            public float getMinPosition() {
                return -1.0f;
            }
        };
    }

    public RangeInput getLeftStickX(){
        return new RangeInput() {
            @Override
            public float getPosition() {
                return gamepad.left_stick_x;
            }

            @Override
            public float getMaxPosition() {
                return 1.0f;
            }

            @Override
            public float getMinPosition() {
                return -1.0f;
            }
        };
    }

    public RangeInput getRightStickY(){
        return new RangeInput() {
            @Override
            public float getPosition() {
                return gamepad.right_stick_y;
            }

            @Override
            public float getMaxPosition() {
                return 1.0f;
            }

            @Override
            public float getMinPosition() {
                return -1.0f;
            }
        };
    }

    public RangeInput getRightStickX(){
        return new RangeInput() {
            @Override
            public float getPosition() {
                return gamepad.right_stick_x;
            }

            @Override
            public float getMaxPosition() {
                return 1.0f;
            }

            @Override
            public float getMinPosition() {
                return -1.0f;
            }
        };
    }

    public RangeInput getLeftTrigger(){
        return new RangeInput() {
            @Override
            public float getPosition() {
                return gamepad.left_trigger;
            }

            @Override
            public float getMaxPosition() {
                return 1.0f;
            }

            @Override
            public float getMinPosition() {
                return 0.0f;
            }
        };
    }

    public RangeInput getRightTrigger(){
        return new RangeInput() {
            @Override
            public float getPosition() {
                return gamepad.right_trigger;
            }

            @Override
            public float getMaxPosition() {
                return 1.0f;
            }

            @Override
            public float getMinPosition() {
                return 0.0f;
            }
        };
    }
}

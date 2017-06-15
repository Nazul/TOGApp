/*
 * Copyright 2017 Mario Contreras <marioc@nazul.net>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.iteso.msc.ms705080.togapp;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class Config {

    /**
     * Left boundary
     */
    public static final int MAX_LEFT = 427;
    /**
     * Right boundary
     */
    public static final int MAX_RIGHT = 853;
    /**
     * Drone speed
     */
    public static final int DRONE_SPEED = 10;
    /**
     * Max altitude
     */
    public static final int MAX_ALTITUDE = 2_000;
    /**
     * PID Constant - KP for X
     */
    public static final int KP_X = 5;
    /**
     * PID Constant - KI for X
     */
    public static final int KI_X = 10;
    /**
     * PID Constant - KD for X
     */
    public static final int KD_X = 12;
    /**
     * PID Constant - KP for Y
     */
    public static final int KP_Y = 5;
    /**
     * PID Constant - KI for Y
     */
    public static final int KI_Y = 10;
    /**
     * PID Constant - KD for Y
     */
    public static final int KD_Y = 12;
}

// EOF

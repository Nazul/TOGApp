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

import de.yadrone.base.ARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.BatteryListener;
import java.awt.image.BufferedImage;
import de.yadrone.base.video.ImageListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import mx.iteso.msc.ms705080.togapp.cv.ProcessedImagesListener;
import mx.iteso.msc.ms705080.togapp.cv.VideoProcessor;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class DroneManager implements ImageListener, AltitudeListener, BatteryListener {

    // Drone object
    private ARDrone drone = null;
    // Listeners to notify
    private final List<ProcessedImagesListener> videoListeners = new ArrayList<>();
    private final List<AltitudeListener> altitudeListeners = new ArrayList<>();
    private final List<BatteryListener> batteryListeners = new ArrayList<>();
    // Flags
    private boolean droneActive = false;
    private boolean droneTracking = false;
    // Current frame
    private BufferedImage currentFrame;
    // Current altitude
    private int currentAltitude;
    // A timer for processing the video stream
    private final ScheduledExecutorService videoTimer;
    // A timer for processing the drone movement
    private final ScheduledExecutorService droneTimer;
    // Video processing
    private VideoProcessor videoProcessor;
    // PID Controller
    private int xCenter = 0;
    private double xOffset;
    private double xError, xLastError, xIntegral, xDerivative;
    private int yCenter = 0;
    private double yOffset;
    private double yError, yLastError, yIntegral, yDerivative;

    @Override
    public void receivedAltitude(int altitude) {
        currentAltitude = altitude;
        altitudeListeners.forEach((listener) -> {
            listener.receivedAltitude(altitude);
        });
    }

    @Override
    public void receivedExtendedAltitude(Altitude altitude) {
        // Not used
    }

    @Override
    public void batteryLevelChanged(int level) {
        batteryListeners.forEach((listener) -> {
            listener.batteryLevelChanged(level);
        });
    }

    @Override
    public void voltageChanged(int voltage) {
        // Not used
    }

    private class videoUpdater implements Runnable {

        @Override
        public void run() {
            if (currentFrame != null) {
                List<BufferedImage> results;
                results = videoProcessor.ProcessFrame(currentFrame);
                videoListeners.forEach((listener) -> {
                    listener.imageUpdated(results);
                });
            }
        }
    }

    private class objectTracker implements Runnable {

        @Override
        public void run() {
            if (droneActive && droneTracking && videoProcessor.ObjectDetected()) {
                System.out.println("Trying to do something...");

                // PID
                // Normalize current position
                double xCurPos = 2.0d * videoProcessor.getTrackedObject().x / (double) videoProcessor.getWidth() - 1.0d;
                double yCurPos = 2.0d * videoProcessor.getTrackedObject().y / (double) videoProcessor.getHeight() - 1.0d;
                xOffset = 0.0d - xCurPos;
                yOffset = 0.0d - yCurPos;
                xError = xCurPos - xOffset;
                yError = yCurPos - yOffset;
                xIntegral += xError;
                yIntegral += yError;
                xDerivative -= xLastError;
                yDerivative -= yLastError;
                double xMove = Config.KP_X * xError + Config.KI_X * xIntegral + Config.KD_X * xDerivative;
                double yMove = Config.KP_Y * yError + Config.KI_Y * yIntegral + Config.KD_Y * yDerivative;
                xLastError = xError;
                yLastError = yError;
                CommandManager cmd = drone.getCommandManager();
                if (xMove > 0) {
                    cmd.spinRight((int) Math.abs(xMove * 0.1d)).doFor(10);
                } else {
                    cmd.spinLeft((int) Math.abs(xMove * 0.1d)).doFor(10);
                }
                if (yMove < 0) {
                    cmd.up((int) Math.abs(yMove * 0.1d)).doFor(10);
                } else {
                    cmd.down((int) Math.abs(yMove * 0.1d)).doFor(10);
                }
            }
        }
    }

    public DroneManager() {
        // Initialize drone
        drone = new ARDrone();
        drone.setHorizontalCamera();
        drone.setMaxAltitude(Config.MAX_ALTITUDE);
        drone.setSpeed(Config.DRONE_SPEED);
        drone.getCommandManager().setOutdoor(false, false);
        drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
        drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
        // Listeners
        drone.getVideoManager().addImageListener(this);
        drone.getNavDataManager().addBatteryListener(this);
        drone.getNavDataManager().addAltitudeListener(this);
        drone.start();

        // Grab a frame every 33 ms (30 frames/sec)
        videoTimer = Executors.newSingleThreadScheduledExecutor();
        videoTimer.scheduleAtFixedRate(new videoUpdater(), 0, 66, TimeUnit.MILLISECONDS);
        // Verify tracked object position every 0.5 seconds and react accordangly
        droneTimer = Executors.newSingleThreadScheduledExecutor();
        droneTimer.scheduleAtFixedRate(new objectTracker(), 0, 500, TimeUnit.MILLISECONDS);
    }

    public void addListener(ProcessedImagesListener listener) {
        videoListeners.add(listener);
    }

    public void addListener(AltitudeListener listener) {
        altitudeListeners.add(listener);
    }

    public void addListener(BatteryListener listener) {
        batteryListeners.add(listener);
    }

    public void stopDrone() {
        if (drone != null) {
            drone.stop();
        }
    }

    public void takeOffDrone() {
        if (drone != null) {
            drone.takeOff();
        }
    }

    public void landDrone() {
        if (drone != null) {
            drone.landing();
        }
    }

    public void hoverDrone() {
        if (drone != null) {
            drone.hover();
        }
    }

    public void resetDrone() {
        if (drone != null) {
            drone.reset();
        }
    }

    @Override
    public void imageUpdated(BufferedImage newImage) {
        currentFrame = newImage;
    }

    /**
     * @return the videoProcessor
     */
    public VideoProcessor getVideoProcessor() {
        return videoProcessor;
    }

    /**
     * @param videoProcessor the videoProcessor to set
     */
    public void setVideoProcessor(VideoProcessor videoProcessor) {
        this.videoProcessor = videoProcessor;
        // Initialize PID Controller
        xCenter = videoProcessor.getWidth() / 2;
        yCenter = videoProcessor.getHeight() / 2;
    }

    /**
     * @return the droneActive
     */
    public boolean isDroneActive() {
        return droneActive;
    }

    /**
     * @param droneActive the droneActive to set
     */
    public void setDroneActive(boolean droneActive) {
        this.droneActive = droneActive;
    }

    /**
     * @return the droneTracking
     */
    public boolean isDroneTracking() {
        return droneTracking;
    }

    /**
     * @param droneTracking the droneTracking to set
     */
    public void setDroneTracking(boolean droneTracking) {
        this.droneTracking = droneTracking;
    }

}

// EOF

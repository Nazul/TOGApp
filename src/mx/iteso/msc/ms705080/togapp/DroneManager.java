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
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import java.awt.image.BufferedImage;
import de.yadrone.base.video.ImageListener;
import java.util.ArrayList;
import java.util.List;
import mx.iteso.msc.ms705080.togapp.cv.ProcessedImagesListener;
import mx.iteso.msc.ms705080.togapp.cv.VideoProcessor;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class DroneManager implements ImageListener {

    // Drone object
    private ARDrone drone = null;
    // Listeners to notify
    private final List<ProcessedImagesListener> listeners = new ArrayList<>();
    // Flags
    private boolean droneActive = false;
    private boolean droneTracking = false;
    // Video processing
    private VideoProcessor videoProcessor;

    public DroneManager() {
        // Initialize drone
        drone = new ARDrone();
        drone.setHorizontalCamera();
        drone.setMaxAltitude(Config.MAX_ALTITUDE);
        drone.setSpeed(Config.DRONE_SPEED);
        drone.getCommandManager().setOutdoor(false, false);
        drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
        drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
        drone.getVideoManager().addImageListener(this);
        drone.start();
    }

    public void addListener(ProcessedImagesListener listener) {
        listeners.add(listener);
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

    private static boolean temp = false;

    @Override
    public void imageUpdated(BufferedImage newImage) {
        if (temp) {
            List<BufferedImage> results;
            //System.out.println("New frame adquired - " + System.currentTimeMillis());
            results = videoProcessor.ProcessFrame(newImage);
            listeners.forEach((listener) -> {
                listener.imageUpdated(results);
            });
        }
        else {
            temp = !temp;
        }
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
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
package mx.iteso.msc.ms705080.togapp.cv;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.iteso.msc.ms705080.togapp.Config;
import mx.iteso.msc.ms705080.togapp.DroneManager;
import mx.iteso.msc.ms705080.togapp.TrackedObject;
import mx.iteso.msc.ms705080.togapp.TrackedObjectColor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class VideoProcessor {
    // 720p (HD) - 1280x720

    // Listeners to notify
    private final List<ChannelValuesListener> listeners = new ArrayList<>();
    // Face size
    private int absoluteFaceSize;
    // OpenCV classifier for face detection
    private final CascadeClassifier faceCascade;
    // A flag to determinate if a tracked object exists on screen
    private boolean objectDetected = false;
    // The center of the currently tracked object
    private Point trackedObject = new Point(0.0d, 0.0d);
    // Predefined object to track
    private TrackedObject objectColor = new TrackedObject(TrackedObjectColor.CUSTOM);
    // A flag to determinate if mouse was clicked in an area so we can select the color around that section
    private boolean mouseClicked;
    // Mouse coordinates
    private int mx, my;
    // Channel values
    private int chMin1, chMin2, chMin3;
    private int chMax1, chMax2, chMax3;
    // PID Controller
    private int center = 0;
    private final int kp = 5, ki = 10, kd = 12;
    //private int currentPos = center;
    private double offset;
    private double error, lastError, integral, derivative;
    // Drone Manager
    private final DroneManager dm;
    // Current detection algorithm
    private ProcessType type;

    public enum ProcessType {
        COLOR_RGB,
        COLOR_HSV,
        PRECONFIG_HSV,
        FACE_DETECTION,
        QR_DETECTION
    }

    public VideoProcessor(DroneManager dm, int width) {
        this.dm = dm;
        // Load classifier for face detection
        faceCascade = new CascadeClassifier(getClass().getResource("/mx/iteso/msc/ms705080/togapp/resources/lbpcascade_frontalface.xml").getFile().substring(1).replace("/", "\\").replace("%20", " "));
        absoluteFaceSize = 0;
        // Initialize PID Controller
        center = width / 2;
    }

    public void addListener(ChannelValuesListener listener) {
        listeners.add(listener);
    }

    public void setTrackedObjectColor(TrackedObject trackedObject) {
        this.objectColor = trackedObject;
    }

    private Mat findAndDrawObjects(Mat maskedImage, Mat frame) {
        return findAndDrawObjects(maskedImage, frame, new Scalar(250, 0, 0));
    }

    private Mat findAndDrawObjects(Mat maskedImage, Mat frame, Scalar color) {
        // Init
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        // Find contours
        Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        // If any contour exist...
        if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
            // for each contour, draw a border
            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
                Imgproc.drawContours(frame, contours, idx, color, 5);
            }
        }
        return frame;
    }

    public Point readQRCode(Mat frame) {
        BufferedImage image = Util.Mat2Image(frame);
        BinaryBitmap bitmap;
        Map hintMap = new HashMap();

        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        RGBLuminanceSource source = new RGBLuminanceSource(image.getWidth(), image.getHeight(), pixels);
        bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result qrCodeResult;
        try {
            qrCodeResult = new MultiFormatReader().decode(bitmap, hintMap);
            System.out.println("Result object created");
        } catch (NotFoundException ex) {
            return null;
        }
        ResultPoint[] points = qrCodeResult.getResultPoints();
        System.out.println("Points received");
        if (points.length != 3) {
            return null;
        } else {
            Point p = new Point();
            System.out.println("P0: " + points[0]);
            System.out.println("P1: " + points[1]);
            System.out.println("P2: " + points[2]);
            p.x = (points[0].getX() + points[1].getX() + points[2].getX()) / 3;
            p.y = (points[0].getY() + points[1].getY() + points[2].getY()) / 3;
            return p;
        }
    }

    private void processFaces(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (Rect face : facesArray) {
            Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0, 255), 3);
        }
        // Get the first face and use it as a tracking object
        objectDetected = false;
        if (facesArray.length > 0) {
            objectDetected = true;
            trackedObject.x = facesArray[0].x + facesArray[0].width / 2;
            trackedObject.y = facesArray[0].y + facesArray[0].height / 2;
            Util.DrawCrosshairs(frame, (int) trackedObject.x, (int) trackedObject.y);
        }
    }

    private void processQr(Mat frame) {
        Mat grayFrame = new Mat();
        Mat blurredImage = new Mat();
        Mat binarizedImage = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);
        //Imgproc.GaussianBlur(grayFrame, blurredImage, new Size(5, 5), 0);
        Imgproc.GaussianBlur(grayFrame, blurredImage, new Size(5, 5), 0);
        /////hsvPanel.getGraphics().drawImage(Util.Mat2Image(blurredImage), 0, 0, 213, 120, null);
        //Imgproc.threshold(blurredImage, binarizedImage, 90, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(blurredImage, binarizedImage, 90, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        /////erodePanel.getGraphics().drawImage(Util.Mat2Image(binarizedImage), 0, 0, 213, 120, null);

        Point qrCenter = readQRCode(binarizedImage);

        // If we have a point (center), use it as a tracking object
        objectDetected = false;
        if (qrCenter != null) {
            objectDetected = true;
            trackedObject = qrCenter;
            Util.DrawCrosshairs(frame, (int) trackedObject.x, (int) trackedObject.y);
        }
    }

    private List<BufferedImage> processHsv(Mat frame) {
        // Init
        Mat blurredImage = new Mat();
        Mat hsvImage = new Mat();
        Mat mask = new Mat();
        Mat morphOutput = new Mat();
        List<BufferedImage> results = new ArrayList<>();
        BufferedImage eroredImage, dilatedImage;

        // Remove some noise
        Imgproc.blur(frame, blurredImage, new Size(7, 7));

        // Convert the frame to HSV
        Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        // Get color of current coordinates
        if (mouseClicked) {
            try {
                int hmin = 180, smin = 255, vmin = 255;
                int hmax = 0, smax = 0, vmax = 0;

                for (int i = mx - 50; i < mx + 50; i++) {
                    for (int j = my - 50; j < my + 50; j++) {
                        double[] hsv = hsvImage.get(j, i);
                        hmin = (int) (hsv[0] < hmin ? hsv[0] : hmin);
                        hmax = (int) (hsv[0] > hmax ? hsv[0] : hmax);
                        smin = (int) (hsv[1] < smin ? hsv[1] : smin);
                        smax = (int) (hsv[1] > smax ? hsv[1] : smax);
                        vmin = (int) (hsv[2] < vmin ? hsv[2] : vmin);
                        vmax = (int) (hsv[2] > vmax ? hsv[2] : vmax);
                    }
                }
                for (ChannelValuesListener listener : listeners) {
                    listener.channelsUpdated(hmin, smin, vmin, hmax, smax, vmax);
                }
                Imgproc.rectangle(frame, new Point(mx - 50, my - 50), new Point(mx + 50, my + 50), new Scalar(255, 0, 255), 4);
            } catch (Exception ex) {

            } finally {
                mouseClicked = false;
            }
        }

        // Get thresholding values from the UI
        // Remember: H ranges 0-180, S and V range 0-255
        Scalar minValues = new Scalar(chMin1, chMin2, chMin3);
        Scalar maxValues = new Scalar(chMax1, chMax2, chMax3);

        // Threshold HSV image to select object
        Core.inRange(hsvImage, minValues, maxValues, mask);

        // Morphological operators
        // Dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Imgproc.erode(mask, morphOutput, erodeElement);
        eroredImage = Util.Mat2Image(morphOutput);
        Imgproc.dilate(mask, morphOutput, dilateElement);
        dilatedImage = Util.Mat2Image(morphOutput);

        // Find the object(s) contours and show them
        frame = this.findAndDrawObjects(morphOutput, frame);

        // Calculate centers
        Mat temp = new Mat();
        morphOutput.copyTo(temp);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(temp, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        objectDetected = false;
        for (int i = 0; i < contours.size(); i++) {
            Rect objectBoundingRectangle = Imgproc.boundingRect(contours.get(i));
            int x = objectBoundingRectangle.x + objectBoundingRectangle.width / 2;
            int y = objectBoundingRectangle.y + objectBoundingRectangle.height / 2;
            if (i == 0) {
                objectDetected = true;
                trackedObject.x = x;
                trackedObject.y = y;
                Util.DrawCrosshairs(frame, x, y);
            }
        }
        results.add(Util.Mat2Image(frame));
        results.add(Util.Mat2Image(hsvImage));
        results.add(eroredImage);
        results.add(dilatedImage);
        return results;
    }

    private List<BufferedImage> processRgb(Mat frame) {
        // Init
        Mat blurredImage = new Mat();
        Mat rgbImage = new Mat();
        Mat mask = new Mat();
        Mat morphOutput = new Mat();
        List<BufferedImage> results = new ArrayList<>();
        BufferedImage eroredImage, dilatedImage;

        // Remove some noise
        Imgproc.blur(frame, blurredImage, new Size(7, 7));

        // Convert the frame to BGRA
        Imgproc.cvtColor(blurredImage, rgbImage, Imgproc.COLOR_BGR2BGRA);

        // Get color of current coordinates
        if (mouseClicked) {
            try {
                int hmin = 180, smin = 255, vmin = 255;
                int hmax = 0, smax = 0, vmax = 0;

                for (int i = mx - 50; i < mx + 50; i++) {
                    for (int j = my - 50; j < my + 50; j++) {
                        double[] hsv = rgbImage.get(j, i);
                        hmin = (int) (hsv[0] < hmin ? hsv[0] : hmin);
                        hmax = (int) (hsv[0] > hmax ? hsv[0] : hmax);
                        smin = (int) (hsv[1] < smin ? hsv[1] : smin);
                        smax = (int) (hsv[1] > smax ? hsv[1] : smax);
                        vmin = (int) (hsv[2] < vmin ? hsv[2] : vmin);
                        vmax = (int) (hsv[2] > vmax ? hsv[2] : vmax);
                    }
                }
                for (ChannelValuesListener listener : listeners) {
                    listener.channelsUpdated(hmin, smin, vmin, hmax, smax, vmax);
                }
                Imgproc.rectangle(frame, new Point(mx - 50, my - 50), new Point(mx + 50, my + 50), new Scalar(255, 0, 255), 4);
            } catch (Exception ex) {

            } finally {
                mouseClicked = false;
            }
        }

        // Get thresholding values from the UI
        Scalar minValues = new Scalar(chMin1, chMin2, chMin3);
        Scalar maxValues = new Scalar(chMax1, chMax2, chMax3);

        // Threshold HSV image to select object
        Core.inRange(rgbImage, minValues, maxValues, mask);

        // Morphological operators
        // Dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Imgproc.erode(mask, morphOutput, erodeElement);
        eroredImage = Util.Mat2Image(morphOutput);
        Imgproc.dilate(mask, morphOutput, dilateElement);
        dilatedImage = Util.Mat2Image(morphOutput);

        // Find the object(s) contours and show them
        frame = this.findAndDrawObjects(morphOutput, frame);

        // Calculate centers
        Mat temp = new Mat();
        morphOutput.copyTo(temp);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(temp, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        objectDetected = false;
        for (int i = 0; i < contours.size(); i++) {
            Rect objectBoundingRectangle = Imgproc.boundingRect(contours.get(i));
            int x = objectBoundingRectangle.x + objectBoundingRectangle.width / 2;
            int y = objectBoundingRectangle.y + objectBoundingRectangle.height / 2;
            if (i == 0) {
                objectDetected = true;
                trackedObject.x = x;
                trackedObject.y = y;
                Util.DrawCrosshairs(frame, x, y);
            }
        }
        results.add(Util.Mat2Image(frame));
        results.add(Util.Mat2Image(rgbImage));
        results.add(eroredImage);
        results.add(dilatedImage);
        return results;
    }

    private void processHsvObjects(Mat frame) {
        // Init
        Mat blurredImage = new Mat();
        Mat hsvImage = new Mat();
        Mat mask = new Mat();
        Mat morphOutput = new Mat();

        // Remove some noise
        Imgproc.blur(frame, blurredImage, new Size(7, 7));

        // Convert the frame to HSV
        Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
        /////hsvPanel.getGraphics().drawImage(this.mat2Image(hsvImage), 0, 0, 213, 120, null);

        // Threshold HSV image to select object
        Core.inRange(hsvImage, objectColor.getHsvMin(), objectColor.getHsvMax(), mask);

        // Morphological operators
        // Dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Imgproc.erode(mask, morphOutput, erodeElement);
        Imgproc.erode(mask, morphOutput, erodeElement);
        // Show the partial output
        /////erodePanel.getGraphics().drawImage(this.mat2Image(morphOutput), 0, 0, 213, 120, null);

        Imgproc.dilate(mask, morphOutput, dilateElement);
        Imgproc.dilate(mask, morphOutput, dilateElement);
        // Show the partial output
        /////dilatePanel.getGraphics().drawImage(this.mat2Image(morphOutput), 0, 0, 213, 120, null);

        // Find the object(s) contours and show them
        frame = this.findAndDrawObjects(morphOutput, frame, objectColor.getColor());

        // Calculate centers
        Mat temp = new Mat();
        morphOutput.copyTo(temp);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(temp, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        objectDetected = false;
        for (int i = 0; i < contours.size(); i++) {
            Rect objectBoundingRectangle = Imgproc.boundingRect(contours.get(i));
            int x = objectBoundingRectangle.x + objectBoundingRectangle.width / 2;
            int y = objectBoundingRectangle.y + objectBoundingRectangle.height / 2;
            if (i == 0) {
                objectDetected = true;
                trackedObject.x = x;
                trackedObject.y = y;
                Util.DrawCrosshairs(frame, x, y);
            }
        }
    }

    public List<BufferedImage> ProcessFrame(BufferedImage currentFrame) {
        // Init everything
        List<BufferedImage> results = null;
        //BufferedImage imageToShow = null;
        Mat frame;

        // Check if the capture is open
        if (currentFrame != null) {
            try {
                // Read the current frame
                frame = Util.Image2Mat(currentFrame);
                // Flip image for easy object manipulation
                //Core.flip(frame, frame, 1);
                switch (type) {
                    case COLOR_RGB:
                        results = processRgb(frame);
                        break;
                    case COLOR_HSV:
                        results = processHsv(frame);
                        break;
                    case PRECONFIG_HSV:
                        //results = processHsvObjects(frame);
                        break;
                    case FACE_DETECTION:
                        //results = processFaces(frame);
                        break;
                    case QR_DETECTION:
                        //results = processQr(frame);
                        break;
                }
                // If the drone is in tracking mode, then draw boundaries
                if (dm.isDroneTracking() && frame != null) {
                    Imgproc.rectangle(frame, new Point(0, 0), new Point(Config.MAX_LEFT, 720), new Scalar(255, 0, 255), 5);
                    Imgproc.rectangle(frame, new Point(Config.MAX_RIGHT, 0), new Point(1280, 720), new Scalar(255, 0, 255), 5);
                }
                // convert the Mat object (OpenCV) to Image (Java AWT)
                //imageToShow = Util.Mat2Image(frame);
            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the frame elaboration: " + e);
            }
        }
        return results;
    }

    public void MouseClicked(int x, int y) {
        mouseClicked = true;
        mx = x;
        my = y;
    }

    public void setChannelValues(int chMin1, int chMin2, int chMin3, int chMax1, int chMax2, int chMax3) {
        this.chMin1 = chMin1;
        this.chMin2 = chMin2;
        this.chMin3 = chMin3;
        this.chMax1 = chMax1;
        this.chMax2 = chMax2;
        this.chMax3 = chMax3;
    }

    /**
     * @return the type
     */
    public ProcessType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ProcessType type) {
        this.type = type;
    }
}

// EOF

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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class Util {

    public static Mat Image2Mat(BufferedImage frame) {
        Mat result = new Mat(frame.getHeight(), frame.getWidth(), CvType.CV_8UC3);

        DataBufferByte data = (DataBufferByte) frame.getRaster().getDataBuffer();
        result.put(0, 0, data.getData());

        return result;
    }

    public static BufferedImage Mat2Image(Mat frame) {
        // Create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // Encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // Build and return an Image created from the image encoded in the buffer
        // Return new BufferedImage(new ByteArrayInputStream(buffer.toArray()));
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
        } catch (IOException e) {
            // log the error
            System.err.println("Exception while converting frame: " + e);
        }
        return img;
    }

    public static void DrawCrosshairs(Mat frame, int x, int y) {
        // Show crosshair
        Imgproc.circle(frame, new Point(x, y), 20, new Scalar(0, 255, 0), 2);
        Imgproc.line(frame, new Point(x, y), new Point(x, y - 25), new Scalar(0, 255, 0), 2);
        Imgproc.line(frame, new Point(x, y), new Point(x, y + 25), new Scalar(0, 255, 0), 2);
        Imgproc.line(frame, new Point(x, y), new Point(x - 25, y), new Scalar(0, 255, 0), 2);
        Imgproc.line(frame, new Point(x, y), new Point(x + 25, y), new Scalar(0, 255, 0), 2);
        Imgproc.putText(frame, "Tracking object at (" + x + "," + y + ")", new Point(x, y), 1, 1, new Scalar(255, 0, 0), 2);
    }

}

// EOF

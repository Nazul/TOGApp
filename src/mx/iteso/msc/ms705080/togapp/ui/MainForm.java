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
package mx.iteso.msc.ms705080.togapp.ui;

import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.BatteryListener;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import mx.iteso.msc.ms705080.togapp.DroneManager;
import mx.iteso.msc.ms705080.togapp.TrackedObject;
import mx.iteso.msc.ms705080.togapp.TrackedObjectColor;
import mx.iteso.msc.ms705080.togapp.cv.VideoProcessor;
import mx.iteso.msc.ms705080.togapp.cv.VideoProcessor.ProcessType;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class MainForm extends javax.swing.JFrame {

    // Drone Manager
    private final DroneManager dm;
    // Video Processor
    private final VideoProcessor vp;
    // Charts
    private final PIDChart pidChartX;
    private final PIDChart pidChartY;
    // Levels
    private int altitude;
    private int battery;

    /**
     * Creates new form MainForm
     *
     * @param dm
     */
    public MainForm(DroneManager dm) {
        // Initialize components
        initComponents();

        // Init graphs
        this.pidChartX = new PIDChart("logX.csv");
        JPanel chartPanelX = new ChartPanel(pidChartX.getChart(), true, true, true, true, true);
        pidPanelX.add(chartPanelX, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
        this.pidChartY = new PIDChart("logY.csv");
        JPanel chartPanelY = new ChartPanel(pidChartY.getChart(), true, true, true, true, true);
        pidPanelY.add(chartPanelY, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        vp = new VideoProcessor(dm, cameraPanel.getWidth(), cameraPanel.getHeight());
        dm.setVideoProcessor(vp);
        vp.setType(ProcessType.COLOR_HSV);

        // Update status bar & radio buttons
        slidersStateChanged(null);
        changeObjectPanelStatus(false);
        // Connect to drone
        this.dm = dm;
        this.dm.addListener((List<BufferedImage> images) -> {
            dmImageUpdated(images);
        });
        this.dm.addListener(new AltitudeListener() {
            @Override
            public void receivedAltitude(int altitude) {
                dmAltitudeUpdated(altitude);
            }

            @Override
            public void receivedExtendedAltitude(Altitude altitude) {
                // Not used
            }
        });
        this.dm.addListener(new BatteryListener() {
            @Override
            public void batteryLevelChanged(int level) {
                dmBatteryUpdated(level);
            }

            @Override
            public void voltageChanged(int i) {
                // Not used
            }
        });
        vp.addListener((int chMin1, int chMin2, int chMin3, int chMax1, int chMax2, int chMax3) -> {
            vpChannelsUpdated(chMin1, chMin2, chMin3, chMax1, chMax2, chMax3);
        });
    }

    private void dmImageUpdated(List<BufferedImage> images) {
        cameraPanel.getGraphics().drawImage(images.get(0), 0, 0, 640, 360, null);
        if (images.size() > 1) {
            hsvPanel.getGraphics().drawImage(images.get(1), 0, 0, 213, 120, null);
        }
        if (images.size() > 2) {
            erodePanel.getGraphics().drawImage(images.get(2), 0, 0, 213, 120, null);
        }
        if (images.size() > 3) {
            dilatePanel.getGraphics().drawImage(images.get(3), 0, 0, 213, 120, null);
        }
    }

    private void dmAltitudeUpdated(int altitude) {
        this.altitude = altitude;
        updateLevels();
    }

    private void dmBatteryUpdated(int level) {
        this.battery = level;
        updateLevels();
    }

    private void updateLevels() {
        levelsLabel.setText(String.format("Battery: %d - Altitude: %d", battery, altitude));
    }

    private void vpChannelsUpdated(int chMin1, int chMin2, int chMin3, int chMax1, int chMax2, int chMax3) {
        hueMinSlider.setValue(chMin1);
        hueMaxSlider.setValue(chMax1);
        saturationMinSlider.setValue(chMin2);
        saturationMaxSlider.setValue(chMax2);
        valueMinSlider.setValue(chMin3);
        valueMaxSlider.setValue(chMax3);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        detectionTypeButtonGroup = new javax.swing.ButtonGroup();
        objectColorButtonGroup = new javax.swing.ButtonGroup();
        cameraPanel = new javax.swing.JPanel();
        processPanel = new javax.swing.JPanel();
        hsvPanel = new javax.swing.JPanel();
        erodePanel = new javax.swing.JPanel();
        dilatePanel = new javax.swing.JPanel();
        chartsPanel = new javax.swing.JPanel();
        pidPanelX = new javax.swing.JPanel();
        pidPanelY = new javax.swing.JPanel();
        pidPanelZ = new javax.swing.JPanel();
        startDroneButton = new javax.swing.JButton();
        startTrackingButton = new javax.swing.JButton();
        resetDroneButton = new javax.swing.JButton();
        huePanel = new javax.swing.JPanel();
        hueMinSlider = new javax.swing.JSlider();
        hueMaxSlider = new javax.swing.JSlider();
        saturationPanel = new javax.swing.JPanel();
        saturationMinSlider = new javax.swing.JSlider();
        saturationMaxSlider = new javax.swing.JSlider();
        valuePanel = new javax.swing.JPanel();
        valueMinSlider = new javax.swing.JSlider();
        valueMaxSlider = new javax.swing.JSlider();
        objectColorPanel = new javax.swing.JPanel();
        blueObjectColorRadioButton = new javax.swing.JRadioButton();
        greenObjectColorRadioButton = new javax.swing.JRadioButton();
        redObjectColorRadioButton = new javax.swing.JRadioButton();
        yellowObjectColorRadioButton = new javax.swing.JRadioButton();
        detectionTypePanel = new javax.swing.JPanel();
        rgbColorDetectionRadioButton = new javax.swing.JRadioButton();
        hsvColorDetectionRadioButton = new javax.swing.JRadioButton();
        preconfigDetectionRadioButton = new javax.swing.JRadioButton();
        faceDetectionRadioButton = new javax.swing.JRadioButton();
        qrDetectionRadioButton = new javax.swing.JRadioButton();
        statusLabel = new javax.swing.JLabel();
        levelsLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("UAV Ground Control Station");
        setName("mainForm"); // NOI18N
        setPreferredSize(new java.awt.Dimension(820, 700));
        setResizable(false);
        setSize(new java.awt.Dimension(820, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cameraPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        cameraPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cameraPanelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout cameraPanelLayout = new javax.swing.GroupLayout(cameraPanel);
        cameraPanel.setLayout(cameraPanelLayout);
        cameraPanelLayout.setHorizontalGroup(
            cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 636, Short.MAX_VALUE)
        );
        cameraPanelLayout.setVerticalGroup(
            cameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 356, Short.MAX_VALUE)
        );

        getContentPane().add(cameraPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 640, 360));

        processPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        processPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        hsvPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout hsvPanelLayout = new javax.swing.GroupLayout(hsvPanel);
        hsvPanel.setLayout(hsvPanelLayout);
        hsvPanelLayout.setHorizontalGroup(
            hsvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );
        hsvPanelLayout.setVerticalGroup(
            hsvPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 116, Short.MAX_VALUE)
        );

        processPanel.add(hsvPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 213, 120));

        erodePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout erodePanelLayout = new javax.swing.GroupLayout(erodePanel);
        erodePanel.setLayout(erodePanelLayout);
        erodePanelLayout.setHorizontalGroup(
            erodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );
        erodePanelLayout.setVerticalGroup(
            erodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 116, Short.MAX_VALUE)
        );

        processPanel.add(erodePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 0, 213, 120));

        dilatePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout dilatePanelLayout = new javax.swing.GroupLayout(dilatePanel);
        dilatePanel.setLayout(dilatePanelLayout);
        dilatePanelLayout.setHorizontalGroup(
            dilatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 209, Short.MAX_VALUE)
        );
        dilatePanelLayout.setVerticalGroup(
            dilatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 116, Short.MAX_VALUE)
        );

        processPanel.add(dilatePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(427, 0, 213, 120));

        getContentPane().add(processPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 640, 120));

        chartsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        chartsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pidPanelX.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pidPanelX.setLayout(new java.awt.GridBagLayout());
        chartsPanel.add(pidPanelX, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 213, 100));

        pidPanelY.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pidPanelY.setLayout(new java.awt.GridBagLayout());
        chartsPanel.add(pidPanelY, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 0, 213, 100));

        pidPanelZ.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pidPanelZ.setLayout(new java.awt.GridBagLayout());
        chartsPanel.add(pidPanelZ, new org.netbeans.lib.awtextra.AbsoluteConstraints(427, 0, 213, 100));

        getContentPane().add(chartsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 640, 100));

        startDroneButton.setText("Start Drone");
        startDroneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDroneButtonActionPerformed(evt);
            }
        });
        getContentPane().add(startDroneButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, 140, -1));

        startTrackingButton.setText("Start Tracking");
        startTrackingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTrackingButtonActionPerformed(evt);
            }
        });
        getContentPane().add(startTrackingButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 40, 140, -1));

        resetDroneButton.setText("Reset Drone");
        resetDroneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDroneButtonActionPerformed(evt);
            }
        });
        getContentPane().add(resetDroneButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 70, 140, -1));

        huePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hue"));
        huePanel.setPreferredSize(new java.awt.Dimension(300, 60));

        hueMinSlider.setMaximum(180);
        hueMinSlider.setValue(0);
        hueMinSlider.setPreferredSize(new java.awt.Dimension(120, 26));
        hueMinSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slidersStateChanged(evt);
            }
        });
        huePanel.add(hueMinSlider);

        hueMaxSlider.setMaximum(180);
        hueMaxSlider.setValue(180);
        hueMaxSlider.setPreferredSize(new java.awt.Dimension(120, 26));
        hueMaxSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slidersStateChanged(evt);
            }
        });
        huePanel.add(hueMaxSlider);

        getContentPane().add(huePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 100, 140, 90));

        saturationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Saturation"));
        saturationPanel.setPreferredSize(new java.awt.Dimension(300, 60));

        saturationMinSlider.setMaximum(255);
        saturationMinSlider.setValue(0);
        saturationMinSlider.setPreferredSize(new java.awt.Dimension(120, 26));
        saturationMinSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slidersStateChanged(evt);
            }
        });
        saturationPanel.add(saturationMinSlider);

        saturationMaxSlider.setMaximum(255);
        saturationMaxSlider.setValue(255);
        saturationMaxSlider.setPreferredSize(new java.awt.Dimension(120, 26));
        saturationMaxSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slidersStateChanged(evt);
            }
        });
        saturationPanel.add(saturationMaxSlider);

        getContentPane().add(saturationPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 190, 140, 90));

        valuePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Value"));
        valuePanel.setPreferredSize(new java.awt.Dimension(300, 60));

        valueMinSlider.setMaximum(255);
        valueMinSlider.setValue(0);
        valueMinSlider.setPreferredSize(new java.awt.Dimension(120, 26));
        valueMinSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slidersStateChanged(evt);
            }
        });
        valuePanel.add(valueMinSlider);

        valueMaxSlider.setMaximum(255);
        valueMaxSlider.setValue(255);
        valueMaxSlider.setPreferredSize(new java.awt.Dimension(120, 26));
        valueMaxSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slidersStateChanged(evt);
            }
        });
        valuePanel.add(valueMaxSlider);

        getContentPane().add(valuePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 280, 140, 90));

        objectColorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Object Color"));

        objectColorButtonGroup.add(blueObjectColorRadioButton);
        blueObjectColorRadioButton.setText("Blue");
        blueObjectColorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blueObjectColorRadioButtonActionPerformed(evt);
            }
        });
        objectColorPanel.add(blueObjectColorRadioButton);

        objectColorButtonGroup.add(greenObjectColorRadioButton);
        greenObjectColorRadioButton.setText("Green");
        greenObjectColorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                greenObjectColorRadioButtonActionPerformed(evt);
            }
        });
        objectColorPanel.add(greenObjectColorRadioButton);

        objectColorButtonGroup.add(redObjectColorRadioButton);
        redObjectColorRadioButton.setText("Red");
        redObjectColorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redObjectColorRadioButtonActionPerformed(evt);
            }
        });
        objectColorPanel.add(redObjectColorRadioButton);

        objectColorButtonGroup.add(yellowObjectColorRadioButton);
        yellowObjectColorRadioButton.setSelected(true);
        yellowObjectColorRadioButton.setText("Yellow");
        yellowObjectColorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yellowObjectColorRadioButtonActionPerformed(evt);
            }
        });
        objectColorPanel.add(yellowObjectColorRadioButton);

        getContentPane().add(objectColorPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 370, 140, 80));

        detectionTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detection Type"));

        detectionTypeButtonGroup.add(rgbColorDetectionRadioButton);
        rgbColorDetectionRadioButton.setText("Color Detection (RGB)");
        rgbColorDetectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rgbColorDetectionRadioButtonActionPerformed(evt);
            }
        });
        detectionTypePanel.add(rgbColorDetectionRadioButton);

        detectionTypeButtonGroup.add(hsvColorDetectionRadioButton);
        hsvColorDetectionRadioButton.setSelected(true);
        hsvColorDetectionRadioButton.setText("Color Detection (HSV)");
        hsvColorDetectionRadioButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        hsvColorDetectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hsvColorDetectionRadioButtonActionPerformed(evt);
            }
        });
        detectionTypePanel.add(hsvColorDetectionRadioButton);

        detectionTypeButtonGroup.add(preconfigDetectionRadioButton);
        preconfigDetectionRadioButton.setText("Pre-Configured (HSV)");
        preconfigDetectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preconfigDetectionRadioButtonActionPerformed(evt);
            }
        });
        detectionTypePanel.add(preconfigDetectionRadioButton);

        detectionTypeButtonGroup.add(faceDetectionRadioButton);
        faceDetectionRadioButton.setText("Face Detection (LBP)");
        faceDetectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faceDetectionRadioButtonActionPerformed(evt);
            }
        });
        detectionTypePanel.add(faceDetectionRadioButton);

        detectionTypeButtonGroup.add(qrDetectionRadioButton);
        qrDetectionRadioButton.setText("QR Detection            ");
        qrDetectionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qrDetectionRadioButtonActionPerformed(evt);
            }
        });
        detectionTypePanel.add(qrDetectionRadioButton);

        getContentPane().add(detectionTypePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 600, 800, 50));

        statusLabel.setText("[statusLabel]");
        statusLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 650, 580, 20));

        levelsLabel.setText("[levelsLabel]");
        levelsLabel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        getContentPane().add(levelsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 650, 220, 20));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cameraPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cameraPanelMouseClicked
        vp.MouseClicked(evt.getX() * 2, evt.getY() * 2);
    }//GEN-LAST:event_cameraPanelMouseClicked

    private void slidersStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slidersStateChanged
        String valuesToPrint;

        if (hsvColorDetectionRadioButton.isSelected()) {
            valuesToPrint = "Hue range: " + this.hueMinSlider.getValue() + "-" + this.hueMaxSlider.getValue()
                    + ". Sat. range: " + this.saturationMinSlider.getValue() + "-" + this.saturationMaxSlider.getValue()
                    + ". Value range: " + this.valueMinSlider.getValue() + "-" + this.valueMaxSlider.getValue();
        } else if (rgbColorDetectionRadioButton.isSelected()) {
            valuesToPrint = "Red range: " + this.hueMinSlider.getValue() + "-" + this.hueMaxSlider.getValue()
                    + ". Green range: " + this.saturationMinSlider.getValue() + "-" + this.saturationMaxSlider.getValue()
                    + ". Blue range: " + this.valueMinSlider.getValue() + "-" + this.valueMaxSlider.getValue();
        } else if (faceDetectionRadioButton.isSelected()) {
            valuesToPrint = "Face detection mode";
        } else if (qrDetectionRadioButton.isSelected()) {
            valuesToPrint = "QR Code detection mode";
        } else {
            valuesToPrint = "Pre-configured HSV object detection. Search for blue, green, yellow and red objects.";
        }
        statusLabel.setText(valuesToPrint);
        vp.setChannelValues(hueMinSlider.getValue(), saturationMinSlider.getValue(), valueMinSlider.getValue(),
                hueMaxSlider.getValue(), saturationMaxSlider.getValue(), valueMaxSlider.getValue());
    }//GEN-LAST:event_slidersStateChanged

    private void startDroneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDroneButtonActionPerformed
        if (!dm.isDroneActive()) {
            dm.takeOffDrone();
            startDroneButton.setText("Stop Drone");
            dm.hoverDrone();
        } else {
            dm.landDrone();
            startDroneButton.setText("Start Drone");
        }
        dm.setDroneActive(!dm.isDroneActive());
    }//GEN-LAST:event_startDroneButtonActionPerformed

    private void startTrackingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTrackingButtonActionPerformed
        if (!dm.isDroneTracking()) {
            startTrackingButton.setText("Stop Tracking");
        } else {
            startTrackingButton.setText("Start Tracking");
        }
        dm.setDroneTracking(!dm.isDroneTracking());
    }//GEN-LAST:event_startTrackingButtonActionPerformed

    private void blueObjectColorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blueObjectColorRadioButtonActionPerformed
        vp.setTrackedObjectColor(new TrackedObject(TrackedObjectColor.BLUE));
    }//GEN-LAST:event_blueObjectColorRadioButtonActionPerformed

    private void greenObjectColorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greenObjectColorRadioButtonActionPerformed
        vp.setTrackedObjectColor(new TrackedObject(TrackedObjectColor.GREEN));
    }//GEN-LAST:event_greenObjectColorRadioButtonActionPerformed

    private void redObjectColorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redObjectColorRadioButtonActionPerformed
        vp.setTrackedObjectColor(new TrackedObject(TrackedObjectColor.RED));
    }//GEN-LAST:event_redObjectColorRadioButtonActionPerformed

    private void yellowObjectColorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yellowObjectColorRadioButtonActionPerformed
        vp.setTrackedObjectColor(new TrackedObject(TrackedObjectColor.YELLOW));
    }//GEN-LAST:event_yellowObjectColorRadioButtonActionPerformed

    private void rgbColorDetectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rgbColorDetectionRadioButtonActionPerformed
        changeSlidersStatus(true);
        changeSliderType("RGB");
        slidersStateChanged(null);
        changeObjectPanelStatus(false);
        vp.setType(ProcessType.COLOR_RGB);
    }//GEN-LAST:event_rgbColorDetectionRadioButtonActionPerformed

    private void hsvColorDetectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hsvColorDetectionRadioButtonActionPerformed
        changeSlidersStatus(true);
        changeSliderType("HSV");
        slidersStateChanged(null);
        changeObjectPanelStatus(false);
        vp.setType(ProcessType.COLOR_HSV);
    }//GEN-LAST:event_hsvColorDetectionRadioButtonActionPerformed

    private void preconfigDetectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preconfigDetectionRadioButtonActionPerformed
        changeSlidersStatus(false);
        slidersStateChanged(null);
        changeObjectPanelStatus(true);
        vp.setType(ProcessType.PRECONFIG_HSV);
    }//GEN-LAST:event_preconfigDetectionRadioButtonActionPerformed

    private void faceDetectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faceDetectionRadioButtonActionPerformed
        changeSlidersStatus(false);
        slidersStateChanged(null);
        changeObjectPanelStatus(false);
        vp.setType(ProcessType.FACE_DETECTION);
    }//GEN-LAST:event_faceDetectionRadioButtonActionPerformed

    private void qrDetectionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qrDetectionRadioButtonActionPerformed
        changeSlidersStatus(false);
        slidersStateChanged(null);
        changeObjectPanelStatus(false);
        vp.setType(ProcessType.QR_DETECTION);
    }//GEN-LAST:event_qrDetectionRadioButtonActionPerformed

    private void changeSlidersStatus(boolean status) {
        hueMinSlider.setEnabled(status);
        saturationMinSlider.setEnabled(status);
        valueMinSlider.setEnabled(status);
        hueMaxSlider.setEnabled(status);
        saturationMaxSlider.setEnabled(status);
        valueMaxSlider.setEnabled(status);
    }

    private void changeSliderType(String type) {
        if (type.equals("HSV")) {
            huePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hue"));
            saturationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Saturation"));
            valuePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Value"));
            hueMinSlider.setMaximum(180);
            hueMaxSlider.setMaximum(180);
        } else {
            huePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Red"));
            saturationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Green"));
            valuePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Blue"));
            hueMinSlider.setMaximum(255);
            hueMaxSlider.setMaximum(255);
        }
    }

    private void changeObjectPanelStatus(boolean status) {
        blueObjectColorRadioButton.setEnabled(status);
        greenObjectColorRadioButton.setEnabled(status);
        yellowObjectColorRadioButton.setEnabled(status);
        redObjectColorRadioButton.setEnabled(status);
    }

    private void resetDroneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDroneButtonActionPerformed
        dm.resetDrone();
    }//GEN-LAST:event_resetDroneButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        dm.stopDrone();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton blueObjectColorRadioButton;
    private javax.swing.JPanel cameraPanel;
    private javax.swing.JPanel chartsPanel;
    private javax.swing.ButtonGroup detectionTypeButtonGroup;
    private javax.swing.JPanel detectionTypePanel;
    private javax.swing.JPanel dilatePanel;
    private javax.swing.JPanel erodePanel;
    private javax.swing.JRadioButton faceDetectionRadioButton;
    private javax.swing.JRadioButton greenObjectColorRadioButton;
    private javax.swing.JRadioButton hsvColorDetectionRadioButton;
    private javax.swing.JPanel hsvPanel;
    private javax.swing.JSlider hueMaxSlider;
    private javax.swing.JSlider hueMinSlider;
    private javax.swing.JPanel huePanel;
    private javax.swing.JLabel levelsLabel;
    private javax.swing.ButtonGroup objectColorButtonGroup;
    private javax.swing.JPanel objectColorPanel;
    private javax.swing.JPanel pidPanelX;
    private javax.swing.JPanel pidPanelY;
    private javax.swing.JPanel pidPanelZ;
    private javax.swing.JRadioButton preconfigDetectionRadioButton;
    private javax.swing.JPanel processPanel;
    private javax.swing.JRadioButton qrDetectionRadioButton;
    private javax.swing.JRadioButton redObjectColorRadioButton;
    private javax.swing.JButton resetDroneButton;
    private javax.swing.JRadioButton rgbColorDetectionRadioButton;
    private javax.swing.JSlider saturationMaxSlider;
    private javax.swing.JSlider saturationMinSlider;
    private javax.swing.JPanel saturationPanel;
    private javax.swing.JButton startDroneButton;
    private javax.swing.JButton startTrackingButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JSlider valueMaxSlider;
    private javax.swing.JSlider valueMinSlider;
    private javax.swing.JPanel valuePanel;
    private javax.swing.JRadioButton yellowObjectColorRadioButton;
    // End of variables declaration//GEN-END:variables
}

// EOF

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

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import mx.iteso.msc.ms705080.togapp.ui.MainForm;
import org.opencv.core.Core;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class TOGApp {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            DroneManager dm = null;

            try {
                // Set UI theme
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }

                // Initialize UAV Manager
                dm = new DroneManager();

                // Show main window
                // Center
                JFrame mainForm = new MainForm(dm);
                mainForm.setLocationRelativeTo(null);
                mainForm.setVisible(true);

                //new TestFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                if (dm != null) {
                    dm.stopDrone();
                }
                System.exit(-1);
            }
        });
    }

}

// EOF

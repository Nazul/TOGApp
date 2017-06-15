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

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Mario Contreras <marioc@nazul.net>
 */
public class PIDChart {

    protected JFreeChart chart;
    protected TimeSeriesCollection dataset;

    private TimeSeries errorSeries;
    private TimeSeries integralSeries;
    private TimeSeries derivativeSeries;

    // Log file
    private PrintWriter pw = null;
    private StringBuilder sb = new StringBuilder();

    /**
     * Create a new instance of this chart.
     *
     * @param logName
     */
    public PIDChart(String logName) {
        this.errorSeries = new TimeSeries("Error", "Value", "Time");
        this.integralSeries = new TimeSeries("Integral", "Value", "Time");
        this.derivativeSeries = new TimeSeries("Derivative", "Value", "Time");

        errorSeries.setMaximumItemCount(40);
        integralSeries.setMaximumItemCount(40);
        derivativeSeries.setMaximumItemCount(40);

        dataset = new TimeSeriesCollection();
        dataset.addSeries(this.errorSeries);
        dataset.addSeries(this.integralSeries);
        dataset.addSeries(this.derivativeSeries);

        createChart("Time", "Value", "Error", true);

        // Initialize log
        try {
            pw = new PrintWriter(new File(logName));
            sb.append("Time,");
            sb.append("Error,");
            sb.append("Integral,");
            sb.append("Derivative\n");
        } catch (FileNotFoundException exc) {
            System.exit(-1);
        }
    }

    /**
     * Update the chart.
     *
     * @param error
     * @param integral
     * @param derivative
     */
    public void setError(double error, double integral, double derivative) {
        Millisecond ms = new Millisecond(new Date());
        errorSeries.addOrUpdate(ms, error);
        integralSeries.addOrUpdate(ms, integral);
        derivativeSeries.addOrUpdate(ms, derivative);
        // Append data to log
        sb.append(System.currentTimeMillis()).append(",");
        sb.append(error).append(",");
        sb.append(integral).append(",");
        sb.append(derivative).append("\n");
    }

    protected void createChart(String domainAxisTitle, String rangeAxisTitle, String chartsTitle, boolean includeLegend) {
        DateAxis domain = new DateAxis(domainAxisTitle);
        NumberAxis range = new NumberAxis(rangeAxisTitle);

        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.magenta);
        renderer.setSeriesPaint(2, Color.green);
        renderer.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        XYPlot plot = new XYPlot(dataset, domain, range, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

        domain.setAutoRange(true);
        domain.setLowerMargin(0.0);
        domain.setUpperMargin(0.0);
        domain.setTickLabelsVisible(true);

        range.setAutoRange(true);
        range.setAutoRangeIncludesZero(true);
        range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        chart = new JFreeChart(plot);
        chart.setBackgroundPaint(Color.white);
    }

    public void closeLog() {
        if (pw != null) {
            pw.close();
        }
    }

    /**
     * Get the chart object.
     *
     * @return The object to be displayed on a panel.
     */
    public JFreeChart getChart() {
        return chart;
    }
}

// EOF

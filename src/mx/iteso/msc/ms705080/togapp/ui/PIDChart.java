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

    /**
     * Create a new instance of this chart.
     */
    public PIDChart() {
        this.errorSeries = new TimeSeries("Error", "Value", "Time");

        errorSeries.setMaximumItemCount(1000);

        dataset = new TimeSeriesCollection();
        dataset.addSeries(this.errorSeries);

        createChart("Time", "Value", "Error", true);
    }

    /**
     * Update the chart.
     *
     * @param tuple The tuple containing the current signal level.
     */
    public void setError(double error) {
        Millisecond ms = new Millisecond(new Date());
        errorSeries.addOrUpdate(ms, error);
    }

    protected void createChart(String domainAxisTitle, String rangeAxisTitle, String chartsTitle, boolean includeLegend) {
        DateAxis domain = new DateAxis(domainAxisTitle);
        NumberAxis range = new NumberAxis(rangeAxisTitle);

        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.blue);
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

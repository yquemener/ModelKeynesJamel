/* =========================================================
 * JAMEL : a Java (tm) Agent-based MacroEconomic Laboratory.
 * =========================================================
 *
 * (C) Copyright 2007-2013, Pascal Seppecher.
 * 
 * Project Info <http://p.seppecher.free.fr/jamel/>. 
 *
 * This file is a part of JAMEL (Java Agent-based MacroEconomic Laboratory).
 * 
 * JAMEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JAMEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JAMEL. If not, see <http://www.gnu.org/licenses/>.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates.]
 */

package jamel.gui.charts.industry;

import jamel.Circuit;
import jamel.gui.charts.JamelColor;
import jamel.gui.charts.TimeChart;
import jamel.util.data.Labels;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * A <code>ChartPanel</code> that contains 
 * a time chart with the wages series.
 */
@SuppressWarnings("serial")
public class MarkupTarget extends ChartPanel {
	
	/**
	 * Returns the chart.
	 * @return the chart.
	 */
	private static JFreeChart newChart() {
		TimeChart chart = new TimeChart("Markup Targets", "Percent",
				Circuit.getCircuit().getTimeSeries().get(Labels.markupIntermediateMedian),
				Circuit.getCircuit().getTimeSeries().get(Labels.markupFinalMedian)
				) ;
		chart.setColors( 0,JamelColor.intermediateColor,JamelColor.finalColor);
		chart.addLineLegendItem("Intermediate Sector", JamelColor.intermediateColor);
		chart.addLineLegendItem("Final Sector", JamelColor.finalColor);
		chart.setRangeAxisRange(0,100);
		return chart;
	}
	
	/**
	 * Creates the <code>ChartPanel</code>.
	 */
	public MarkupTarget() {
		super(newChart());
	}

}

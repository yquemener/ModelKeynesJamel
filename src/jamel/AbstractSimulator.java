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

package jamel;

import java.lang.reflect.InvocationTargetException;

import org.jfree.data.time.Month;

/**
 *
 */
public interface AbstractSimulator {

	/**
	 * Systemic crisis.
	 */
	public abstract void failure();

	/**
	 * Adds a marker to the time charts.
	 * @param label the label.
	 * @param month the month.
	 */
	public abstract void marker(String label, Month month);

	/**
	 * Sets the sate of the simulation (paused or running).
	 * @param b a flag that indicates whether or not the simulation must be paused.
	 */
	public abstract void pause(boolean b);

	/**
	 * Prints a String in the console panel.
	 * @param message the String to print.
	 */
	public abstract void println(String message);

	/**
	 * Sets the chart in the specified panel.
	 * @param tabIndex the index of the tab to customize.
	 * @param panelIndex the id of the ChartPanel to customize.
	 * @param chartPanelName the name of the ChartPanel to set.
	 * @throws ClassNotFoundException... 
	 * @throws NoSuchMethodException...
	 * @throws InvocationTargetException... 
	 * @throws IllegalAccessException...
	 * @throws InstantiationException...
	 * @throws SecurityException...
	 * @throws IllegalArgumentException... 
	 */
	public abstract void setChart(int tabIndex, int panelIndex,
			String chartPanelName) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException;

	/**
	 * Sets the selected index for the tabbedpane of the application window.
	 * @param index the index to be selected.
	 */
	public abstract void setVisiblePanel(int index);

	/**
	 * Zooms in the time charts.
	 * @param aZoom the zoom factor.
	 */
	public abstract void zoom(int aZoom);

}
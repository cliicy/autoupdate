package com.ca.arcserve.edge.app.base.webservice.contract.model;

import java.util.ArrayList;
import java.util.List;

public class ChartSeries implements ChartModel {

	private List<Series> seriesList;

	public ChartSeries() {
		seriesList = new ArrayList<Series>();
	}

	public List<DataPoint> addSeries() {
		return this.addSeries(null, null);
	}

	public List<DataPoint> addSeries(String name, String color, String renderAs) {
		Series s = new Series();

		if (name != null) {
			s.setName(name);
		}

		if (color != null) {
			s.setColor(color);
		}
		
		if(renderAs != null){
			s.setRenderAs(renderAs);
		}

		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		s.setDataPoints(dataPoints);
		seriesList.add(s);

		return dataPoints;
	}
	
	public List<DataPoint> addSeries(String name, String color) {
		Series s = new Series();

		if (name != null) {
			s.setName(name);
		}

		if (color != null) {
			s.setColor(color);
		}

		s.setRenderAs("");
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		s.setDataPoints(dataPoints);
		seriesList.add(s);

		return dataPoints;
	}

	public void removeSeries(Series s) {
		this.seriesList.remove(s);
	}

	public void addSeries(Series s) {
		seriesList.add(s);
	}

	public Series getSeries(int index) {
		return seriesList.get(index);
	}

	public int getSeriesCount() {
		return seriesList.size();
	}

}

package com.sportProject.model;

public class Statistica {
	private Double TSR = 0.0;
	private Double STR = 0.0;
	public Double getSTR() {
		return STR;
	}

	public void setSTR(double sTR) {
		STR = sTR;
	}

	private double media = 0;

	public double getMedia() {
		return media;
	}

	public void setMedia() {
		this.media = this.STR+this.getTSR()/2;
	}

	public Statistica() {}

	public Double getTSR() {
		return TSR;
	}

	public void setTSR(double TSR) {
		this.TSR = TSR;
	}
	
	public void setMedia(double media){
		this.media = media;
	}
}

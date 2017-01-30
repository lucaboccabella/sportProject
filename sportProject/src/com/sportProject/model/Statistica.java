package com.sportProject.model;

public class Statistica {
	private String nomeSquadra = "";
	public String getNomeSquadra() {
		return nomeSquadra;
	}

	public void setNomeSquadra(String nomeSquadra) {
		this.nomeSquadra = nomeSquadra;
	}

	public void setTSR(Double tSR) {
		TSR = tSR;
	}

	public void setSTR(Double sTR) {
		STR = sTR;
	}

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
		this.media = this.STR+this.TSR/2;
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

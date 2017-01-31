package com.sportProject.model;

public class StatisticaClassifica {
	private double tiriFatti;
	private double tiriSubiti;
	private double tiriInPortaFatti;
	private double tiriInPortaSubiti;
	private double STR;
	private double TSR;
	private double media;
	private String nomeSquadra;
	private String stato;
	
	public StatisticaClassifica(){}

	public double getTiriFatti() {
		return tiriFatti;
	}

	public void setTiriFatti(double tiriFatti) {
		this.tiriFatti = tiriFatti;
	}

	public double getTiriSubiti() {
		return tiriSubiti;
	}

	public void setTiriSubiti(double tiriSubiti) {
		this.tiriSubiti = tiriSubiti;
	}

	public double getTiriInPortaFatti() {
		return tiriInPortaFatti;
	}

	public void setTiriInPortaFatti(double tiriInPortaFatti) {
		this.tiriInPortaFatti = tiriInPortaFatti;
	}

	public double getTiriInPortaSubiti() {
		return tiriInPortaSubiti;
	}

	public void setTiriInPortaSubiti(double tiriInPortaSubiti) {
		this.tiriInPortaSubiti = tiriInPortaSubiti;
	}

	public String getNomeSquadra() {
		return nomeSquadra;
	}

	public void setNomeSquadra(String nomeSquadra) {
		this.nomeSquadra = nomeSquadra;
	}

	public double getSTR() {
		return STR;
	}

	public void setSTR(double sTR) {
		STR = sTR;
	}

	public double getTSR() {
		return TSR;
	}

	public void setTSR(double tSR) {
		TSR = tSR;
	}

	public double getMedia() {
		return media;
	}

	public void setMedia(double media) {
		this.media = media;
	}

	public String getStato() {
		return stato;
	}

	public void setStato() {
		if(media>0.9){
			stato = "success";
		}
		else if(media<0.6){
			stato = "danger";
		}
		else{
			stato = "warning";
		}
	}
	
	
}

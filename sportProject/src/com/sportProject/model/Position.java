package com.sportProject.model;

public class Position {
	private String nome;
	private int punti;
	private String situazionePunti;
	
	public String getSituazionePunti() {
		return situazionePunti;
	}
	public void setSituazionePunti(String situazionePunti) {
		this.situazionePunti = situazionePunti;
	}
	public Position() {
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int getPunti() {
		return punti;
	}
	public void setPunti(int punti) {
		this.punti = punti;
	}
	
}

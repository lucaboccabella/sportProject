package com.sportProject.model;

public class Marcatore {

	private String nomeMarcatore;
	private String nomeSquadra;
	private int goal;
	private int rigori;
	
	
	public int getRigori() {
		return rigori;
	}

	public void setRigori(int rigori) {
		this.rigori = rigori;
	}

	public String getNomeMarcatore() {
		return nomeMarcatore;
	}
	
	public void setNomeMarcatore(String nomeMarcatore) {
		this.nomeMarcatore = nomeMarcatore;
	}
	
	public String getNomeSquadra() {
		return nomeSquadra;
	}
	
	public void setNomeSquadra(String nomeSquadra) {
		this.nomeSquadra = nomeSquadra;
	}
	
	public int getGoal() {
		return goal;
	}
	
	public void setGoal(int goal) {
		this.goal = goal;
	}
	
	public Marcatore(String nomeMarcatore, String nomeSquadra, int goal, int rigori) {
		super();
		this.nomeMarcatore = nomeMarcatore;
		this.nomeSquadra = nomeSquadra;
		this.goal = goal;
		this.rigori = rigori;
	}
	
	public Marcatore() {
		
	}
	
	
	
	
}

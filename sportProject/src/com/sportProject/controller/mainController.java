package com.sportProject.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sportProject.model.Position;
import com.sportProject.model.Risultati;
import com.sportProject.model.Statistica;

@Controller
public class mainController {
	private static final Log logger = LogFactory.getLog(mainController.class);
	public int giornata = 0;

	@RequestMapping(value = "/index")
	public String index(Model model) throws UnirestException {
		/* RECUPERO STAGIONE ATTUALE */
		JSONObject response = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons").asJson()
				.getBody().getObject().getJSONObject("data");
		JSONArray seasons = response.getJSONArray("seasons");
		JSONObject season = seasons.getJSONObject(0);
		String stagioneAttuale = season.getString("season_slug");
		logger.info("JSON: " + stagioneAttuale);
		model.addAttribute("stagioneAttuale", stagioneAttuale);

		/* RECUPERO CLASSIFICA */
		JSONObject responseStandings = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings").asJson().getBody()
				.getObject().getJSONObject("data");
		List<Position> classifica = new ArrayList<Position>();
		JSONArray squadre = responseStandings.getJSONArray("standings");
		for (int i = 0; i < squadre.length(); i++) {
			JSONObject squadra = squadre.getJSONObject(i);
			Position pos = new Position();
			pos.setNome(squadra.getString("team"));
			pos.setPunti(squadra.getJSONObject("overall").getInt("points"));
			if (i >= 0 && i <= 2) {
				pos.setSituazionePunti("success");
			} else if (i == 3 || i == 4) {
				pos.setSituazionePunti("info");
			} else if (i >= 17 && i <= 19) {
				pos.setSituazionePunti("danger");
			} else {
				pos.setSituazionePunti("active");
			}
			classifica.add(pos);
		}
		model.addAttribute("classifica", classifica);

		/*
		 * RECUPERO RISULTATI ULTIMA GIORNATA(da integrare meglio scelta
		 * giornata)
		 */
		giornata = squadre.getJSONObject(10).getJSONObject("overall").getInt("matches_played");
		JSONObject risultatiGiornata = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-" + giornata
						+ "/matches")
				.asJson().getBody().getObject().getJSONObject("data");
		List<Risultati> ultimiRisultati = new ArrayList<Risultati>();
		JSONArray results = risultatiGiornata.getJSONArray("matches");
		for (int j = 0; j < results.length(); j++) {
			Risultati r = new Risultati();
			JSONObject partita = results.getJSONObject(j);
			r.setHome(partita.getJSONObject("home").getString("team"));
			r.setHomeres(partita.getJSONObject("home").getInt("goals"));
			r.setAway(partita.getJSONObject("away").getString("team"));
			r.setAwayres(partita.getJSONObject("away").getInt("goals"));
			ultimiRisultati.add(r);
		}
		model.addAttribute("risultati", ultimiRisultati);
		return "index";
	}

	@RequestMapping(value = "/pronostici")
	public String pronostici(Model model) throws UnirestException {
		/*
		 * RECUPERO STATISTICHE 
		 */
		JSONObject risultatiGiornata = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-" + (giornata+1)
						+ "/matches")
				.asJson().getBody().getObject().getJSONObject("data");
		JSONArray classifica = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings").asJson().getBody()
				.getObject().getJSONObject("data").getJSONArray("standings");
		JSONArray results = risultatiGiornata.getJSONArray("matches");
		logger.info("Dimensione array matches: "+results.length());
		Statistica st = new Statistica();
		List<double[][]> probabilita = new ArrayList<double[][]>();
		for(int i=0;i<results.length();i++){
			logger.info("dentro for matches");
			String homeTeam = results.getJSONObject(i).getJSONObject("home").getString("team");
			for(int j=0;j<classifica.length();j++){
				String squadra = classifica.getJSONObject(j).getString("team");
				if(homeTeam.equals(squadra)){
					logger.info("squadra home: "+homeTeam+" squadra classifica: "+squadra);
					st.setNomeCasa(homeTeam);
					st.setPartiteGiocateCasa(classifica.getJSONObject(j).getJSONObject("home").getInt("matches_played"));
					st.setGoalFattiCasa(classifica.getJSONObject(j).getJSONObject("home").getInt("scores"));
					st.setGoalSubitiCasa(classifica.getJSONObject(j).getJSONObject("home").getInt("conceded"));
				}
			}
			String awayTeam = results.getJSONObject(i).getJSONObject("away").getString("team");
			for(int j=0;j<classifica.length();j++){
				String squadra = classifica.getJSONObject(j).getString("team");
				if(awayTeam.equals(squadra)){
					logger.info("squadra away: "+homeTeam+" squadra classifica: "+squadra);
					st.setNomeTrasferta(awayTeam);
					st.setPartiteGiocateTrasferta(classifica.getJSONObject(j).getJSONObject("away").getInt("matches_played"));
					st.setGoalFattiTrasferta(classifica.getJSONObject(j).getJSONObject("away").getInt("scores"));
					st.setGoalSubitiTrasferta(classifica.getJSONObject(j).getJSONObject("away").getInt("conceded"));
				}
			}
			
			double A = (st.getGoalFattiCasa()/st.getPartiteGiocateCasa() + st.getGoalSubitiTrasferta()/st.getPartiteGiocateTrasferta()) /2;
			logger.info("A: "+A);
			double B = (st.getGoalSubitiCasa()/st.getPartiteGiocateCasa() + st.getGoalFattiTrasferta()/st.getPartiteGiocateTrasferta())/2;
			logger.info("B: "+B);
			final double EULERO = 2.71828183;
			final double[] FACTORIAL = {1,1,2,6,24,120,720}; 
			double[] formulaHome = new double[7];
			double[] formulaAway = new double[7];
			for(int j=0;j<7;j++){
				formulaHome[j] = (Math.pow(A, j)*Math.pow(EULERO, -A))/FACTORIAL[j];
				formulaAway[j] = (Math.pow(B, j)*Math.pow(EULERO, -B))/FACTORIAL[j];
				logger.info("Formula Home: "+formulaHome[j]+" Formula Away: "+formulaAway[j]);
			}
			double[][] risultatiProbabili = new double[7][7];
			for(int x=0;x<7;x++){
				for(int y=0;y<7;y++){
					risultatiProbabili[x][y] = formulaHome[x]*formulaAway[y]*100;
					logger.info(risultatiProbabili[x][y]);
				}
			}
			probabilita.add(risultatiProbabili);
		}
		model.addAttribute("stats",probabilita);
		return "pronostici";
	}
}

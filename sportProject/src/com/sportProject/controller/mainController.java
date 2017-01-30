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
		 * RECUPERO STATISTICHE E LI SALVO IN UNA MAPPA CON CHIAVE IL NOME E
		 * VALORI LA LISTA DI STATISTICHE
		 */
		giornata = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings").asJson()
				.getBody().getObject().getJSONObject("data").getJSONArray("standings").getJSONObject(10)
				.getJSONObject("overall").getInt("matches_played");
		List<Statistica> stats = new ArrayList<Statistica>();
		for (int i = 1; i <= giornata; i++) {
			JSONArray matches = Unirest.get(
					"http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-" + i + "/matches")
					.asJson().getBody().getObject().getJSONObject("data").getJSONArray("matches");
			for (int j = 0; j < matches.length(); j++) {
				JSONObject partita = matches.getJSONObject(j);
				String squadraHome = partita.getJSONObject("home").getString("team");
				String squadraAway = partita.getJSONObject("away").getString("team");
				int tiriHome = partita.getJSONObject("home").getInt("shots_on_goal")
						+ partita.getJSONObject("home").getInt("shots_off_goal");
				int tiriAway = partita.getJSONObject("away").getInt("shots_on_goal")
						+ partita.getJSONObject("away").getInt("shots_off_goal");
				Statistica s = new Statistica();
				Statistica s2 = new Statistica();
				Double TSR = 0.0;
				Double STR = 0.0;
				if(arrayListIndexByName(squadraHome,stats)!=-1){
					if (tiriHome != 0 && tiriAway != 0) {
						TSR = stats.get(arrayListIndexByName(squadraHome,stats)).getTSR() + (double) (tiriHome / tiriHome + tiriAway);
				
					} else {
						TSR = stats.get(arrayListIndexByName(squadraHome,stats)).getTSR();
					}
					s.setTSR(TSR);
				}
				else{
					if (tiriHome != 0 && tiriAway != 0) {
						TSR = (double) (tiriHome / tiriHome + tiriAway);
				
					} else {
						TSR = 0.0;
					}
					s.setNomeSquadra(squadraHome);
					s.setTSR(TSR);
					stats.add(s);
				}
				if(arrayListIndexByName(squadraHome,stats)!=-1){
					if (partita.getJSONObject("home").getInt("shots_on_goal") != 0
							&& partita.getJSONObject("away").getInt("shots_on_goal") != 0) {
						STR = stats.get(arrayListIndexByName(squadraHome,stats)).getSTR()
								+ (double) partita.getJSONObject("home").getInt("shots_on_goal")
										/ (partita.getJSONObject("home").getInt("shots_on_goal")
												+ partita.getJSONObject("away").getInt("shots_on_goal"));
					} else {
						STR = stats.get(arrayListIndexByName(squadraHome,stats)).getSTR();
					}
					s.setSTR(STR);
				}
				else{
					if (partita.getJSONObject("home").getInt("shots_on_goal") != 0
							&& partita.getJSONObject("away").getInt("shots_on_goal") != 0) {
						STR = (double) partita.getJSONObject("home").getInt("shots_on_goal")
						/ (partita.getJSONObject("home").getInt("shots_on_goal")
								+ partita.getJSONObject("away").getInt("shots_on_goal"));
				
					} else {
						STR = 0.0;
					}
					s.setNomeSquadra(squadraHome);
					s.setSTR(STR);
					stats.add(s);
				}

				/**/
				
				if(arrayListIndexByName(squadraAway,stats)!=-1){
					if (tiriHome != 0 && tiriAway != 0) {
						TSR = stats.get(arrayListIndexByName(squadraAway,stats)).getTSR() + (double) (tiriAway / tiriHome + tiriAway);
				
					} else {
						TSR = stats.get(arrayListIndexByName(squadraAway,stats)).getTSR();
					}
					s2.setTSR(TSR);
				}
				else{
					if (tiriHome != 0 && tiriAway != 0) {
						TSR = (double) (tiriAway / tiriHome + tiriAway);
				
					} else {
						TSR = 0.0;
					}
					s2.setNomeSquadra(squadraAway);
					s2.setTSR(TSR);
					stats.add(s2);
				}
				if(arrayListIndexByName(squadraAway,stats)!=-1){
					if (partita.getJSONObject("away").getInt("shots_on_goal") != 0
							&& partita.getJSONObject("home").getInt("shots_on_goal") != 0) {
						STR = stats.get(arrayListIndexByName(squadraAway,stats)).getSTR()
								+ (double) partita.getJSONObject("away").getInt("shots_on_goal")
										/ (partita.getJSONObject("home").getInt("shots_on_goal")
												+ partita.getJSONObject("away").getInt("shots_on_goal"));
					} else {
						STR = stats.get(arrayListIndexByName(squadraAway,stats)).getSTR();
					}
					s2.setSTR(STR);
				}
				else{
					if (partita.getJSONObject("home").getInt("shots_on_goal") != 0
							&& partita.getJSONObject("away").getInt("shots_on_goal") != 0) {
						STR = (double) partita.getJSONObject("away").getInt("shots_on_goal")
						/ (partita.getJSONObject("home").getInt("shots_on_goal")
								+ partita.getJSONObject("away").getInt("shots_on_goal"));
				
					} else {
						STR = 0.0;
					}
					s2.setNomeSquadra(squadraHome);
					s2.setSTR(STR);
					stats.add(s2);
				}
			}
		}
		for(Statistica st : stats){
			st.setSTR(st.getSTR()/giornata);
			st.setTSR(st.getTSR()/giornata);
			st.setMedia();
		}
		
		Collections.sort(stats, new Comparator<Statistica>() {
		    @Override
		    public int compare(Statistica o1, Statistica o2) {
		        if(o1.getMedia()>o2.getMedia()){
		        	return -1;
		        }
		        else if(o1.getMedia()<o2.getMedia()){
		        	return 1;
		        }
		        else{
		        	return 0;
		        }
		    }
		});
		model.addAttribute("stats", stats);
		return "pronostici";
	}
	
	public static int arrayListIndexByName(String name,List<Statistica> array){
		for(int i=0;i<array.size();i++){
			if(name.equals(array.get(i).getNomeSquadra())){
				return i;
			}
		}
		return -1;
	}
}

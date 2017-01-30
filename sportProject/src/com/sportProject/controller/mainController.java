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
		HashMap<String, Statistica> stats = new HashMap<String, Statistica>();
		List<String> listaSquadre = new ArrayList<String>();
		JSONArray arraySquadre = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings").asJson().getBody()
				.getObject().getJSONObject("data").getJSONArray("standings");
		for (int i = 0; i < arraySquadre.length(); i++) {
			JSONObject squadra = arraySquadre.getJSONObject(i);
			listaSquadre.add(squadra.getString("team"));
		}
		for (String s : listaSquadre) {
			stats.put(s, new Statistica());
		}
		for (int i = 1; i <= giornata; i++) {
			JSONArray matches = Unirest.get(
					"http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-" + i + "/matches")
					.asJson().getBody().getObject().getJSONObject("data").getJSONArray("matches");
			for (int j = 0; j < matches.length(); j++) {
				JSONObject partita = matches.getJSONObject(j);
				String squadraHome = partita.getJSONObject("home").getString("team");
				String squadraAway = partita.getJSONObject("away").getString("team");
				logger.info("Casa: " + squadraHome + " Trasferta: " + squadraAway);
				int tiriHome = partita.getJSONObject("home").getInt("shots_on_goal")
						+ partita.getJSONObject("home").getInt("shots_off_goal");
				int tiriAway = partita.getJSONObject("away").getInt("shots_on_goal")
						+ partita.getJSONObject("away").getInt("shots_off_goal");
				logger.info("Tiri Casa: " + tiriHome + " Tiri Trasferta: " + tiriAway);
				Statistica s = new Statistica();
				Double TSR;
				Double STR;
				if (tiriHome != 0 && tiriAway != 0) {
					TSR = stats.get(squadraHome).getTSR() + (double) (tiriHome / tiriHome + tiriAway);
				} else {
					TSR = stats.get(squadraHome).getTSR();
				}
				s.setTSR(TSR);
				if (partita.getJSONObject("home").getInt("shots_on_goal") != 0
						&& partita.getJSONObject("away").getInt("shots_on_goal") != 0) {
					STR = stats.get(squadraHome).getSTR()
							+ (double) partita.getJSONObject("home").getInt("shots_on_goal")
									/ (partita.getJSONObject("home").getInt("shots_on_goal")
											+ partita.getJSONObject("away").getInt("shots_on_goal"));
				} else {
					STR = stats.get(squadraHome).getSTR();
				}
				s.setSTR(STR);
				s.setMedia();
				stats.remove(squadraHome);
				stats.put(squadraHome, s);

				if (tiriHome != 0 && tiriAway != 0) {
					TSR = stats.get(squadraAway).getTSR() + (double) (tiriAway / tiriAway + tiriHome);
				} else {
					TSR = stats.get(squadraAway).getTSR();
				}
				s.setTSR(TSR);
				if (partita.getJSONObject("away").getInt("shots_on_goal") != 0
						&& partita.getJSONObject("home").getInt("shots_on_goal") != 0) {
					STR = stats.get(squadraAway).getSTR()
							+ (double) partita.getJSONObject("away").getInt("shots_on_goal")
									/ (partita.getJSONObject("away").getInt("shots_on_goal")
											+ partita.getJSONObject("home").getInt("shots_on_goal"));
				} else {
					STR = stats.get(squadraAway).getSTR();
				}
				s.setMedia();
				stats.remove(squadraAway);
				stats.put(squadraAway, s);

			}
		}
		for (Statistica value : stats.values()) {
			value.setTSR(value.getTSR() / giornata);
			value.setSTR(value.getSTR() / giornata);
			value.setMedia();
		}
		LinkedHashMap<String,Statistica> statsSorted = sortMapByValues(stats);
		model.addAttribute("stats", statsSorted);
		return "pronostici";
	}
	
	public static LinkedHashMap<String,Statistica> sortMapByValues(HashMap<String,Statistica> hm){
		Set<Entry<String,Statistica>> mapEntries = hm.entrySet();
		List<Entry<String,Statistica>> aList = new LinkedList<Entry<String,Statistica>>(mapEntries);
		Collections.sort(aList, new Comparator<Entry<String,Statistica>>() {
            @Override
            public int compare(Entry<String, Statistica> ele1,
                    Entry<String, Statistica> ele2) {
                if(ele1.getValue().getMedia()>ele2.getValue().getMedia()){
                	return -1;
                }
                else if(ele1.getValue().getMedia()<ele2.getValue().getMedia()){
                	return 1;
                }
                else{
                	return 0;
                }
            }
        });
		LinkedHashMap<String,Statistica> aMap2 = new LinkedHashMap<String, Statistica>();
	    for(Entry<String,Statistica> entry: aList) {
	    	aMap2.put(entry.getKey(), entry.getValue());
	    }
		return aMap2;
	}
}

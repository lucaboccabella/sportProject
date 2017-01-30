package com.sportProject.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		/*RECUPERO STAGIONE ATTUALE*/
		JSONObject response = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons")
				.asJson().getBody().getObject().getJSONObject("data");
		JSONArray seasons = response.getJSONArray("seasons");
		JSONObject season = seasons.getJSONObject(0);
		String stagioneAttuale = season.getString("season_slug");
		logger.info("JSON: "+stagioneAttuale);
		model.addAttribute("stagioneAttuale",stagioneAttuale);
		
		/*RECUPERO CLASSIFICA*/
		JSONObject responseStandings = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings")
				.asJson().getBody().getObject().getJSONObject("data");
		List<Position> classifica = new ArrayList<Position>();
		JSONArray squadre = responseStandings.getJSONArray("standings");
		for(int i=0;i<squadre.length();i++){
			JSONObject squadra = squadre.getJSONObject(i);
			Position pos = new Position();
			pos.setNome(squadra.getString("team"));
			pos.setPunti(squadra.getJSONObject("overall").getInt("points"));
			if(i>=0 && i<=2){
				pos.setSituazionePunti("success");
			}
			else if(i==3 || i==4){
				pos.setSituazionePunti("info");
			}
			else if(i>=17 && i<=19){
				pos.setSituazionePunti("danger");
			}
			else{
				pos.setSituazionePunti("active");
			}
			classifica.add(pos);
		}
		model.addAttribute("classifica",classifica);
		
		/*RECUPERO RISULTATI ULTIMA GIORNATA(da integrare meglio scelta giornata)*/
		giornata = squadre.getJSONObject(10).getJSONObject("overall").getInt("matches_played");
		JSONObject risultatiGiornata = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-"+giornata+"/matches")
				.asJson().getBody().getObject().getJSONObject("data");
		List<Risultati> ultimiRisultati = new ArrayList<Risultati>();
		JSONArray results = risultatiGiornata.getJSONArray("matches");
		for(int j=0;j<results.length();j++){
			Risultati r = new Risultati();
			JSONObject partita = results.getJSONObject(j);
			r.setHome(partita.getJSONObject("home").getString("team"));
			r.setHomeres(partita.getJSONObject("home").getInt("goals"));
			r.setAway(partita.getJSONObject("away").getString("team"));
			r.setAwayres(partita.getJSONObject("away").getInt("goals"));
			ultimiRisultati.add(r);
		}
		model.addAttribute("risultati",ultimiRisultati);
		return "index";
	}
	
	@RequestMapping(value = "/pronostici")
	public String pronostici(Model model) throws UnirestException {
		/*
		RECUPERO STATISTICHE E LI SALVO IN UNA MAPPA CON CHIAVE IL NOME E VALORI LA LISTA DI STATISTICHE 
		*/
        Map<String,Statistica> stats = new HashMap<String,Statistica>();
        giornata = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings")
				.asJson().getBody().getObject().getJSONObject("data").getJSONArray("standings").getJSONObject(10).getJSONObject("overall").getInt("matches_played");;
        for(int i=0;i<=giornata;i++){
        	JSONObject response = Unirest.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-"+giornata+"/matches")
				.asJson().getBody().getObject().getJSONObject("data");
        	JSONArray partite = response.getJSONArray("matches");
    		List<String> squadre = new ArrayList<String>();
        	for(int j=0;j<partite.length();j++){
        		JSONObject partita = partite.getJSONObject(j);
        		double tiriHomeFatti = partita.getJSONObject("home").getInt("shots_on_goal") + partita.getJSONObject("home").getInt("shots_off_goal");
        		double tiriAwayFatti = partita.getJSONObject("away").getInt("shots_on_goal") + partita.getJSONObject("away").getInt("shots_off_goal");
        		Double TSR = 0.0;
        		Double STR = 0.0;
        		Statistica stat = new Statistica();
        		String squadraHome = partita.getJSONObject("home").getString("team");
        		squadre.add(squadraHome);
        		String squadraAway = partita.getJSONObject("away").getString("team");
        		squadre.add(squadraAway);
        		if(stats.get(squadraHome) != null && !(stats.get(squadraHome).getTSR().isNaN()) && !(stats.get(squadraHome).getTSR().isInfinite()) && !(stats.get(squadraHome).getSTR().isNaN()) && !(stats.get(squadraHome).getSTR().isInfinite())){
        			TSR += stats.get(squadraHome).getTSR()+tiriHomeFatti/(tiriHomeFatti+tiriAwayFatti);
        			STR += stats.get(squadraHome).getSTR()+(double) partita.getJSONObject("home").getInt("shots_on_goal") / (partita.getJSONObject("home").getInt("shots_on_goal") + partita.getJSONObject("away").getInt("shots_on_goal"));
        		}
        		else{
        			TSR = tiriHomeFatti/(tiriHomeFatti+tiriAwayFatti);
        			STR = (double) partita.getJSONObject("home").getInt("shots_on_goal") / (partita.getJSONObject("home").getInt("shots_on_goal") + partita.getJSONObject("away").getInt("shots_on_goal"));
        		}
    			stat.setTSR(TSR);
        		stat.setSTR(STR);
        		stat.setMedia();
        		logger.info("TSR: "+TSR);
        		logger.info("STR: "+STR);
        		stats.put(squadraHome, stat);
        		if(stats.get(squadraAway) != null && !(stats.get(squadraAway).getTSR().isNaN()) && !(stats.get(squadraAway).getTSR().isInfinite()) && !(stats.get(squadraAway).getSTR().isNaN()) && !(stats.get(squadraAway).getSTR().isInfinite())){
        			TSR += stats.get(squadraAway).getTSR() + tiriAwayFatti/(tiriAwayFatti+tiriHomeFatti);
        			STR += stats.get(squadraAway).getSTR()+(double) partita.getJSONObject("away").getInt("shots_on_goal") / (partita.getJSONObject("away").getInt("shots_on_goal") + partita.getJSONObject("home").getInt("shots_on_goal"));
        		}
        		else{
        			TSR = tiriAwayFatti/(tiriAwayFatti+tiriHomeFatti);
        			STR = (double) partita.getJSONObject("away").getInt("shots_on_goal") / (partita.getJSONObject("away").getInt("shots_on_goal") + partita.getJSONObject("home").getInt("shots_on_goal"));
        		}
        		stat.setTSR(TSR);
        		stat.setSTR(STR);
        		stat.setMedia();
        		logger.info("TSR: "+TSR);
        		logger.info("STR: "+STR);
        		stats.put(squadraAway, stat);
        	}
        	for(String s:squadre){
        		Statistica st = new Statistica();
        		st.setMedia(stats.get(s).getMedia()/i);
        		stats.remove(s);
        		stats.put(s, st);
        	}
        }
        model.addAttribute("stats",stats);
        return "pronostici";
	}
}

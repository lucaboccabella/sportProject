package com.sportProject.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sportProject.model.Marcatore;
import com.sportProject.model.Position;
import com.sportProject.model.Risultati;
import com.sportProject.model.Statistica;
import com.sportProject.model.StatisticaClassifica;

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
	public String pronostici(Model model,HttpSession session) throws Exception {
		/*
		 * RECUPERO STATISTICHE 
		 */
		JSONArray classifica = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/standings").asJson().getBody()
				.getObject().getJSONObject("data").getJSONArray("standings");
		giornata = classifica.getJSONObject(10).getJSONObject("overall").getInt("matches_played");
		JSONObject risultatiGiornata = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-" + (giornata+1)
						+ "/matches")
				.asJson().getBody().getObject().getJSONObject("data");
		JSONArray results = risultatiGiornata.getJSONArray("matches");
		HashMap<String,Statistica> probabile = new HashMap<String,Statistica>();
		for(int i=0;i<results.length();i++){
			Statistica st = new Statistica();
			double A = 0.0;
			double B = 0.0;
			String homeTeam = results.getJSONObject(i).getJSONObject("home").getString("team");
			for(int j=0;j<classifica.length();j++){
				String squadra = classifica.getJSONObject(j).getString("team");
				if(homeTeam.equals(squadra)){
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
					st.setNomeTrasferta(awayTeam);
					st.setPartiteGiocateTrasferta(classifica.getJSONObject(j).getJSONObject("away").getInt("matches_played"));
					st.setGoalFattiTrasferta(classifica.getJSONObject(j).getJSONObject("away").getInt("scores"));
					st.setGoalSubitiTrasferta(classifica.getJSONObject(j).getJSONObject("away").getInt("conceded"));
					B = ((double) st.getGoalSubitiCasa()/(double) st.getPartiteGiocateCasa() + (double) st.getGoalFattiTrasferta()/(double) st.getPartiteGiocateTrasferta())/2;
					A = ((double) st.getGoalFattiCasa()/(double) st.getPartiteGiocateCasa() + (double) st.getGoalSubitiTrasferta()/(double) st.getPartiteGiocateTrasferta()) /2;
				}
			}
			
			
			final double EULERO = 2.71828183;
			final double[] FACTORIAL = {1,1,2,6,24,120,720}; 
			double[] formulaHome = new double[7];
			double[] formulaAway = new double[7];
			for(int j=0;j<7;j++){
				formulaHome[j] = (Math.pow(A, j)*Math.pow(EULERO, -A))/FACTORIAL[j];
				formulaAway[j] = (Math.pow(B, j)*Math.pow(EULERO, -B))/FACTORIAL[j];
			}
			double[][] risultatiProbabili = new double[7][7];
			double maxProb = 0;
			String pos = "0-0";
			for(int x=0;x<7;x++){
				for(int y=0;y<7;y++){
					risultatiProbabili[x][y] = formulaHome[x]*formulaAway[y]*100;
					if(risultatiProbabili[x][y] > maxProb){
						maxProb=risultatiProbabili[x][y];
						pos = x+"-"+y;
					}
				}
			}
			st.setRisultatoProbabile(pos);
			double uno = 0.0;
			for(int risCasa=1;risCasa<7;risCasa++){
				for(int risTrasferta=0;risTrasferta<risCasa;risTrasferta++){
					uno += risultatiProbabili[risCasa][risTrasferta];
				}
			}
			st.setUno(Math.round(uno));
			
			double due = 0.0;
			for(int risTrasferta=1;risTrasferta<7;risTrasferta++){
				for(int risCasa=0;risCasa<risTrasferta;risCasa++){
					due += risultatiProbabili[risCasa][risTrasferta];
				}
			}
			st.setDue(Math.round(due));
			st.setPareggio(Math.floor(100-(uno+due)));
			
			
			double under = 0.0;
			double over = 0.0;
			for(int risCasa=0;risCasa<7;risCasa++){
				for(int risTrasferta=0;risTrasferta<7;risTrasferta++){
					if(risCasa+risTrasferta>2){
						over += risultatiProbabili[risCasa][risTrasferta];
					}
					else{
						under += risultatiProbabili[risCasa][risTrasferta];
					}
				}
			}
			st.setOver(Math.round(over));
			st.setUnder(Math.round(under));
			
			double goal = 0.0;
			for(int risCasa=1;risCasa<7;risCasa++){
				for(int risTrasferta=1;risTrasferta<7;risTrasferta++){
					goal += risultatiProbabili[risCasa][risTrasferta];
				}
			}
			st.setGoal(Math.round(goal));
			st.setNogoal(Math.round(100-goal));
			probabile.put(homeTeam+"-"+awayTeam, st);
		}
		model.addAttribute("stats",probabile);
		
		/*STATISTICHE CLASSIFICA*/
		
		
		HashMap<String,StatisticaClassifica> previsioneClassifica = new HashMap<String,StatisticaClassifica>();
		HashMap<String,StatisticaClassifica> inSession = (HashMap<String, StatisticaClassifica>) session.getAttribute("classificaInSession");
		if(inSession==null){
		for(int i=0;i<classifica.length();i++){
			double tiriFatti = 0.0;
			double tiriSubiti = 0.0;
			double tiriPortaFatti = 0.0;
			double tiriPortaSubiti = 0.0;
			
			for(int j=giornata-5;j<=giornata;j++){
				JSONArray matches = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/rounds/round-" + j
						+ "/matches")
				.asJson().getBody().getObject().getJSONObject("data").getJSONArray("matches");
				for(int k=0;k<matches.length();k++){
					if(classifica.getJSONObject(i).getString("team").equals(matches.getJSONObject(k).getJSONObject("home").getString("team"))){
						tiriFatti += matches.getJSONObject(k).getJSONObject("home").getInt("shots_on_goal")+matches.getJSONObject(k).getJSONObject("home").getInt("shots_off_goal");
						tiriPortaFatti += matches.getJSONObject(k).getJSONObject("home").getInt("shots_on_goal");
						tiriSubiti += matches.getJSONObject(k).getJSONObject("away").getInt("shots_on_goal")+matches.getJSONObject(k).getJSONObject("away").getInt("shots_off_goal");
						tiriPortaSubiti += matches.getJSONObject(k).getJSONObject("away").getInt("shots_on_goal");
						break;
					}
					else if(classifica.getJSONObject(i).getString("team").equals(matches.getJSONObject(k).getJSONObject("away").getString("team"))){
						tiriFatti += matches.getJSONObject(k).getJSONObject("away").getInt("shots_on_goal")+matches.getJSONObject(k).getJSONObject("away").getInt("shots_off_goal");
						tiriPortaFatti += matches.getJSONObject(k).getJSONObject("away").getInt("shots_on_goal");
						tiriSubiti += matches.getJSONObject(k).getJSONObject("home").getInt("shots_on_goal")+matches.getJSONObject(k).getJSONObject("home").getInt("shots_off_goal");
						tiriPortaSubiti += matches.getJSONObject(k).getJSONObject("home").getInt("shots_on_goal");
						break;
					}
				}
			}
			StatisticaClassifica stc = new StatisticaClassifica();
			stc.setNomeSquadra(classifica.getJSONObject(i).getString("team"));
			stc.setTiriFatti(tiriFatti);
			stc.setTiriSubiti(tiriSubiti);
			stc.setTiriInPortaFatti(tiriPortaFatti);
			stc.setTiriInPortaSubiti(tiriPortaSubiti);
			stc.setTSR(tiriFatti/(tiriFatti+tiriSubiti));
			stc.setSTR(tiriPortaFatti/(tiriPortaFatti+tiriPortaSubiti));
			stc.setMedia((tiriFatti/(tiriFatti+tiriSubiti))+(tiriPortaFatti/(tiriPortaFatti+tiriPortaSubiti))/2);
			stc.setStato();
			previsioneClassifica.put(classifica.getJSONObject(i).getString("team"), stc);
		}
		session.setAttribute("classificaInSession",sortMapByValues(previsioneClassifica));
		model.addAttribute("classificaPrevista",sortMapByValues(previsioneClassifica));
		}
		else{
			model.addAttribute("classificaPrevista",session.getAttribute("classificaInSession"));
		}
		return "pronostici";
	}

	@RequestMapping(value="/squadre")
	public String squadre(Model model) throws UnirestException{
		return "squadre";
	}
	
	@RequestMapping(value="/giocatori")
	public String giocatori(Model model) throws UnirestException{
		return "giocatori";
	}
	
	private static Map<String, StatisticaClassifica> sortMapByValues(Map<String, StatisticaClassifica> aMap) {
        
        Set<Entry<String,StatisticaClassifica>> mapEntries = aMap.entrySet();
        
        logger.info("Values and Keys before sorting ");
        for(Entry<String,StatisticaClassifica> entry : mapEntries) {
            logger.info(entry.getValue().getMedia() + " - "+ entry.getKey());
        }
        
        // used linked list to sort, because insertion of elements in linked list is faster than an array list. 
        List<Entry<String,StatisticaClassifica>> aList = new LinkedList<Entry<String,StatisticaClassifica>>(mapEntries);

        // sorting the List
        Collections.sort(aList, new Comparator<Entry<String,StatisticaClassifica>>() {

            @Override
            public int compare(Entry<String, StatisticaClassifica> ele1,
                    Entry<String, StatisticaClassifica> ele2) {
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
        
        // Storing the list into Linked HashMap to preserve the order of insertion. 
        Map<String,StatisticaClassifica> aMap2 = new LinkedHashMap<String, StatisticaClassifica>();
        for(Entry<String,StatisticaClassifica> entry: aList) {
            aMap2.put(entry.getKey(), entry.getValue());
        }
        return aMap2;
	}
	
	@RequestMapping(value = "/marcatori")
	public String marcatori(Model model,HttpSession session) throws Exception {
		
		JSONArray classificaMarcatori = Unirest
				.get("http://soccer.sportsopendata.net/v1/leagues/serie-a/seasons/16-17/topscorers").asJson().getBody()
				.getObject().getJSONObject("data").getJSONArray("topscorers");
		
		ArrayList<Marcatore> marcatori = new ArrayList<Marcatore>();
		
		ArrayList<String> nomiTrovati = new ArrayList<String>();
		
		for(int i = 0; i < classificaMarcatori.length(); i++) {
			
			Marcatore m = new Marcatore();
			m.setNomeMarcatore(classificaMarcatori.getJSONObject(i).getString("fullname"));
			m.setNomeSquadra(classificaMarcatori.getJSONObject(i).getString("team"));
			m.setGoal(classificaMarcatori.getJSONObject(i).getInt("goals"));
			m.setRigori(classificaMarcatori.getJSONObject(i).getInt("penalties"));
			
			boolean sw = false;
			for(String s:nomiTrovati){
				if(classificaMarcatori.getJSONObject(i).getString("fullname").equals(s)){
					sw = true;
					break;
				}
			}
			if(sw == false){
				marcatori.add(m);
				nomiTrovati.add(m.getNomeMarcatore());
			}
		}
		
		
		
		model.addAttribute("marcatori", marcatori);
		
		
		
		return "marcatori";
	}

	
		
}

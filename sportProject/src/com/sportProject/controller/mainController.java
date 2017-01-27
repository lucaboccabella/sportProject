package com.sportProject.controller;

import java.util.ArrayList;
import java.util.List;

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

@Controller
public class mainController {
	private static final Log logger = LogFactory.getLog(mainController.class);

	@RequestMapping(value = "/index")
	public String inputProduct(Model model) throws UnirestException {	
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
		return "index";
	}
}

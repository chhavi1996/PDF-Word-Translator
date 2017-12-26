package pdftrans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;
import javax.swing.JEditorPane;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pdftrans.model.DefData;

public class DictionaryAPICall {

	private DefData data;

	public void callAPI(String word, JEditorPane result) throws Exception {
		result.setText("");
		
		String root="https://od-api.oxforddictionaries.com:443/api/v1/inflections/en/"+word;
		URL url = new URL(root);
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Accept", "application/json");
		urlConnection.setRequestProperty("app_id", "b7229e33");
		urlConnection.setRequestProperty("app_key", "d163cf0f9a69e1334df3325d98145673");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = br.readLine()) != null)
			response.append(inputLine);

		br.close();

		// System.out.println(response.toString());
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(response.toString());

		JSONArray arr = (JSONArray) obj.get("results");
		obj = (JSONObject) arr.get(0);
		arr = (JSONArray) obj.get("lexicalEntries");
		JSONObject obj1 = (JSONObject) arr.get(0);
		JSONArray arr1 = (JSONArray) obj1.get("inflectionOf");
		JSONObject obj2 = (JSONObject) arr1.get(0);
		word =  obj2.get("text").toString();
		
		String u = "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" + word + "/synonyms;antonyms";
		url = new URL(u);
		urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Accept", "application/json");
		urlConnection.setRequestProperty("app_id", "b7229e33");
		urlConnection.setRequestProperty("app_key", "d163cf0f9a69e1334df3325d98145673");
		int responseCode = urlConnection.getResponseCode();

		System.out.println("Response code: " + responseCode);

		br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		inputLine="";
		response = new StringBuffer();

		while ((inputLine = br.readLine()) != null)
			response.append(inputLine);

		br.close();

		// System.out.println(response.toString());
		 parser = new JSONParser();
		 obj = (JSONObject) parser.parse(response.toString());

		arr = (JSONArray) obj.get("results");
		obj = (JSONObject) arr.get(0);
		arr = (JSONArray) obj.get("lexicalEntries");
		obj1 = (JSONObject) arr.get(0);
		arr1 = (JSONArray) obj1.get("entries");
		obj2 = (JSONObject) arr1.get(0);
		arr = (JSONArray) obj2.get("senses");
		obj = (JSONObject) arr.get(0);
		arr = (JSONArray) obj.get("synonyms");
		arr1 = (JSONArray) obj.get("antonyms");

		String synres = "<ul>", antres = "<ul>", defres = "<ul>";
		data = new DefData();
		String[] def = data.getDefinition(word);

		if (def != null)
			for (String r : def) {
				if (r == null)
					break;
				defres += "<li>" + r + "</li>";
			}
		if (arr != null)
			for (Object syn : arr) {
				synres += "<li>" + (((JSONObject) syn).get("id").toString()) + "</li>";
			}

		if (arr1 != null)
			for (Object syn : arr1) {
				antres += "<li>" + (((JSONObject) syn).get("id").toString()) + "</li>";
			}

		synres += "</ul>";
		antres += "</ul>";
		defres += "</ul>";
		if (synres == null)
			synres = "";

		if (antres == null)
			antres = "";

		result.setContentType("text/html");
		result.setText("<u><b>Synonyms</b></u><br><br>" + "<font color='blue'>" + synres
				+ "</font><br><u><b>Antonyms</b></u><br><br>" + "<font color='red'>" + antres + "</font>"
				+ "<br><br><u><b>Definition</b></u><br><br><font color='purple'>" + defres + "</font>");

		
	}

	

}

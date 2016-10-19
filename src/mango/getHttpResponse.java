package mango;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class getHttpResponse {

	private HttpURLConnection connection;
	public String response;
	public Map<String, Object> tree;

	public getHttpResponse(String link) {
		// TODO Auto-generated constructor stub

		try {
			setConnection(link);
			getResponse();
//			System.out.println(response);

			JSONObject jsonObject = new JSONObject(response.substring(4, response.length()));
			tree = parseJSONObjectToMap(jsonObject);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Object query(String query) {
		// String path="items.0.volumeInfo.subtitle";
		String[] nodes = query.split("\\.");
		return traverseTree(tree, nodes, 0);
	}
	
	public Map getRoot() {
		return tree;
	}
	
	public enum classes{
		HASHMAP{
			public String toString(){
				return "java.util.HashMap";
			}
		},
		ARRAYLIST{
			public String toString(){
				return "java.util.ArrayList";
			}
		},
		STRING{
			public String toString(){
				return "java.lang.String";
			}
		},
		INTEGER{
			public String toString(){
				return "java.lang.Integer";
			}
		}
	}
	
	public void searchPathInTree(String path) {
		showTree(tree, "$", null, false, path, true);
	}
	
	public void searchField(String field) {
		showTree(tree, "$", field, true, null, false);
	}
	
	public void showTree(Map<String, Object> curTree,String prefix,String query,boolean QUERY,String path,boolean PATH) {
		Set<String> keys = curTree.keySet();
		
		for (String key : keys) {
			String newPrefix = prefix+"."+key;	

			if(PATH && newPrefix.length() > path.length())
				continue;
			
			boolean show = false;
			if(QUERY && key.equals(query)){
				show = true;
			}

			if(PATH && path.equals(newPrefix)){
				show = true;
			}
			
			String className = curTree.get(key).getClass().getName();
			switch (className) {
			case "java.util.HashMap":
				showTree((HashMap)curTree.get(key),newPrefix,query,QUERY,path,PATH);
				break;
			case "java.util.ArrayList":
				List<Object> objsArray = ((List)curTree.get(key));
				
				if(objsArray.get(0).getClass().getName().equals(classes.HASHMAP.toString())){
					for (Object object : objsArray) {
						showTree((HashMap)object,newPrefix+"."+objsArray.indexOf((HashMap)object),query,QUERY,path,PATH);
					}					
				}
				else{
					if(show)
						System.out.println(newPrefix+" : "+curTree.get(key));
				}
				break;
			case "java.lang.String":
				if(show)
					System.out.println(newPrefix+" : "+curTree.get(key));
				break;
			case "java.lang.Integer":
				if(show)
					System.out.println(newPrefix+" : "+curTree.get(key));				
				break;
			case "java.lang.Boolean":
				if(show)
					System.out.println(newPrefix+" : "+curTree.get(key));				
				break;
			default:
				if(show)
					System.out.println(className+" Class Not found");
				break;
			}
		}
	}
	
	public Object traverseTree(Map<String, Object> tree, String[] nodes, int i) {
		if (i<nodes.length && tree.containsKey(nodes[i])) {
			if (tree.get(nodes[i]).getClass().getName().equals(String.class.getName()))
				return tree.get(nodes[i]);
			else if (tree.get(nodes[i]).getClass().getName().equals(Integer.class.getName()))
				return tree.get(nodes[i]);
			else if (tree.get(nodes[i]).getClass().getName().equals(ArrayList.class.getName())) {
				String key = nodes[i];
				i += 1;
				if (i == nodes.length - 1) {
					int val = Integer.parseInt(nodes[i]);
					return ((List<Object>) tree.get(key)).get(val);
				}
				else if(i<nodes.length-1){
					int val = Integer.parseInt(nodes[i]);
					i += 1;
					return traverseTree((HashMap<String, Object>) (((List<Object>) tree.get(key)).get(val)), nodes,i);
				} else {
					return tree.get(key);
				}
			} else if (tree.get(nodes[i]).getClass().getName().equals(HashMap.class.getName())) {
				if (i < nodes.length - 1) {
					return traverseTree((HashMap<String, Object>) (tree.get(nodes[i])), nodes, i + 1);
				}
				return tree.get(nodes[i]);
			} else {
				return null;
			}
		} else {
			return "Key \"" + nodes[i] + "\" not found";
		}
	}

	public static Map<String, Object> parseJSONObjectToMap(JSONObject jsonObject) throws JSONException {
		Map<String, Object> mapData = new HashMap<String, Object>();
		Iterator<String> keysItr = jsonObject.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			Object value = jsonObject.get(key);

			if (value instanceof JSONArray) {
				value = parseJSONArrayToList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = parseJSONObjectToMap((JSONObject) value);
			}
			mapData.put(key, value);
		}
		return mapData;
	}

	public static List<Object> parseJSONArrayToList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = parseJSONArrayToList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = parseJSONObjectToMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}

	public void setConnection(String link) throws IOException {
		URL url = new URL(link);
		System.out.println(url.getProtocol());
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

		connection.addRequestProperty("Content-Type", "application/json");

		if (connection.getResponseCode() != 200) {
//			throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			System.out.println(connection.getResponseCode());
			System.out.println(connection.getResponseMessage());
			System.exit(0);
		}
		
		System.out.println("Connected");
	}

	public void getResponse() throws IOException {
		if (connection != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
			System.out.println("Output from Server .... \n");
			String seq = "";
			while ((seq = br.readLine()) != null) {
				response += seq;
				// System.out.println(seq);
			}
			connection.disconnect();
		}
	}
}

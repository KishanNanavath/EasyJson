package mango;

import java.util.ArrayList;
import java.util.HashMap;

public class alfanso {
	public static void main(String[] args) {
		new alfanso();
	}
	public alfanso() {
		// TODO Auto-generated constructor stub
		String link = "https://www.googleapis.com/books/v1/volumes?q=inauthor%3A%22dan+brown%22&maxResults=100&printType=books";
		getHttpResponse response = new getHttpResponse(link);
		
//		System.out.println("sellerStatus");
//		ArrayList<Object> sellerStatus = (ArrayList<Object>) response.query("message.sellerStatus");
//		
//		for(int i = 0;i<sellerStatus.size();i++){
//			HashMap curSeller = (HashMap) sellerStatus.get(i);
//			System.out.println(curSeller.get("uiStatus")+" : "+curSeller.get("systemStatus"));
//		}
//		
//		System.out.println(response.query("message.shipTo"));
		
//		response.showTree(response.getRoot(),"$","title");
		response.searchField("authors");
		response.searchPathInTree("$.items.0.volumeInfo.title");
	}
}

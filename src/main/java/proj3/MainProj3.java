package proj3;

import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;
import static spark.Spark.*;

public class MainProj3 {

	public static void main(String[] args) {

		staticFileLocation("/public"); // index.html is served at localhost:4567
										// (default port)
		webSocket("/chat", ChatWebSocketHandler.class); 
		init();
	}

}
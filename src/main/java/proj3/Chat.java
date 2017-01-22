package proj3;
import org.eclipse.jetty.websocket.api.*;
import org.json.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {
	private ChatWebSocketHandler webSocket;
	private String name;
    
	Chat(String name,ChatWebSocketHandler webSocket){
		this.webSocket=webSocket;
		this.name=name;
	}

	
	// this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    public void addSession(Session session){
    	this.userUsernameMap.put(session,this.webSocket.getUserInfo(session).getName());
    	broadcastMessage(session, "I am joining the chat");
    }
    public void removeSession(Session session){
    	broadcastMessage(session, "I am leaving the chat");
    	this.userUsernameMap.remove(session);
    }
    
 
    //Sends a message from one user to all users, along with a list of current usernames
    public void broadcastMessage(Session senderSession,String message) {
        String mes=createHtmlMessageFromSender(this.userUsernameMap.get(senderSession), message);
    	this.userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
        	try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                	.put("typ", "updateChat")
                    .put("userMessage",mes)
                    .put("userlist", this.userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

}
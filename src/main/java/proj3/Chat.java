package proj3;
import org.eclipse.jetty.websocket.api.*;
import org.json.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {

	private String name;
    
	Chat(String name){
		this.name=name;
	}

	
	// this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    private int nextUserNumber = 1; //Assign to username for next connecting user

    public void addSession(Session session,String name){
    	this.userUsernameMap.put(session,name);
    }
    public void removeSession(Session session){
    	this.userUsernameMap.remove(session);
    }
    
 
    //Sends a message from one user to all users, along with a list of current usernames
    public void broadcastMessage(Session senderSession,String message) {
        this.userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
        	try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage",createHtmlMessageFromSender(this.userUsernameMap.get(senderSession), message))
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
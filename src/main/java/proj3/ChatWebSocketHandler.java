package proj3;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

@WebSocket
public class ChatWebSocketHandler {
	private Map<String,Chat> chats = new ConcurrentHashMap<>();
	private Map<Session,UserInfo> users= new ConcurrentHashMap<>();
	private int index=1;
 
    private String messageJson;
    
    public ChatWebSocketHandler() {
    	makeStringJson();
	}
    
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        UserInfo info=new UserInfo("User" + index++);
        users.put(user, info);
        
        updateListOfChats(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        Chat whichChat=users.get(user).getChat();
        if(null!=whichChat){
        	whichChat.removeSession(user);
        }
        users.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String mes) {
    	try {
    		JSONObject json=new JSONObject(mes);
    		String typ=(String)json.get("typ");  
    		String message=(String)json.get("message");
			if(typ.equals("chat")){ 
				String name=(String)json.get("name");
	    		chats.get(name).broadcastMessage(user,message);
	    	
			}else if(typ.equals("main")){
	    		chats.put(message, new Chat(message));
	    		
	    		makeStringJson();
	    		users.keySet().stream().filter(Session::isOpen).forEach(ses->{
	    			updateListOfChats(ses);  
	    		});
	    	}
			
		} catch (JSONException e) {
			System.out.println("niespodziewana wiadomosc"); 
		}  
    	
    }
    
    private void makeStringJson(){
    	try {
			this.messageJson=String.valueOf(new JSONObject()
					.put("chatlist", chats.keySet())
					.put("typ","updateListOfChats")
					);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    private void updateListOfChats(Session session){
		try {
			session.getRemote().sendString(messageJson);
		} catch (Exception e) {
	        e.printStackTrace();
	    }
    }
}
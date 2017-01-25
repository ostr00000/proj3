package proj3;

import org.eclipse.jetty.websocket.api.*;
import org.json.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

public class Chat {
	private ChatWebSocketHandler webSocket;
	protected String name;
	protected Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

	Chat(String name, ChatWebSocketHandler webSocket) {
		this.webSocket = webSocket;
		this.name = name;
	}

	public void addSession(Session session) {
		this.userUsernameMap.put(session, this.webSocket.getUserInfo(session).getName());
		broadcastMessage(userUsernameMap.get(session), "I am joining the chat");
	}

	public void removeSession(Session session) {
		this.userUsernameMap.remove(session);
		broadcastMessage(this.webSocket.getUserInfo(session).getName(), "I am leaving the chat");
	}

	public void broadcastMessage(String userName, String message) {
		String mes = createHtmlMessageFromSender(userName, message);
		this.userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
			try {
				session.getRemote().sendString(String.valueOf(new JSONObject().put("typ", "updateChat")
						.put("userMessage", mes).put("userlist", this.userUsernameMap.values())));
			} catch (Exception e) {
				System.out.println("send message problem ");
				e.printStackTrace();
			}
		});
	}

	protected String createHtmlMessageFromSender(String sender, String message) {
		return article()
				.with(b(sender + " says:"), p(message),
						span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date())))
				.render();
	}

}
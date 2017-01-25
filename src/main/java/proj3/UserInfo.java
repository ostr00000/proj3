package proj3;

public class UserInfo {
	private Chat chat = null;
	private String name = null;

	public UserInfo(String name) {
		this.name = name;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

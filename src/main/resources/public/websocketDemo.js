//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updatePage(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

//possible types "main","chat","change"
var messageTyp="main";


//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

id("back").addEventListener("click", function () {
	sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
	if (message !== "") {
		var mes={"typ":messageTyp,"message":message}
        webSocket.send(JSON.stringify(mes));
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updatePage(msg) {
    var data = JSON.parse(msg.data);
	if("updateChat"==data.typ){
		id("message").value="";
		id("message").placeholder="Type your message";
		id("back").style.display= "inline";
		insert("chat", data.userMessage);
		id("list").innerHTML = "";
		id("send").textContent="Send";
		data.userlist.forEach(function (user) {
			insert("list", "<li>" + user + "</li>");
		});
	}else{
		id("message").value="";
		id("message").placeholder="Type name of new chat";
		id("back").style.display= "none";
		id("chat").value="";
		id("list").innerHTML = "";
		id("send").textContent="Add chat";
		data.chatlist.forEach(function (chat) {
			insert("list", "<li onlick=change("+chat+")>" + chat + "</li>");
		});
	}
}

function change(name){
	console.log(name);
	if(""!==name){
		messageTyp="change";
		sendMessage(name);
		messageTyp="chat";
	}
}


//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}
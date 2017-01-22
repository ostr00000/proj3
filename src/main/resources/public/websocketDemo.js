function getName(){
	if(readCookie("username")==null){
		var name="";
		while(name==""){
			name=prompt("What is your name?");
		}
		document.cookie = "username="+name;
	}
}
getName();


//possible types "main","chat","change","back","setname"
var messageTyp="setname";

var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { 
	updatePage(msg); 
};
webSocket.onclose = function () { 
	document.body.innerHTML="WebSocket connection closed";
};
webSocket.onopen = function () { 
	sendMessage(readCookie("username"));
	messageTyp="main";
};

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

id("back").addEventListener("click", function () {
	messageTyp="back";
	webSocket.send(JSON.stringify({"typ":"back","message":""}));
	messageTyp="main";
});

id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

function sendMessage(message) {
	if (message !== "") {
		var mes={"typ":messageTyp,"message":message};
        webSocket.send(JSON.stringify(mes));
        id("message").value = "";
    }
}

function updatePage(msg) {
    var data = JSON.parse(msg.data);
	if("updateChat"==data.typ){
		id("message").value="";
		id("message").placeholder="Type your message";
		id("back").style.display= "inline";
		id("send").textContent="Send";
		
		insert("chat", data.userMessage);
		id("list").innerHTML = "";
		data.userlist.forEach(function (user) {
			insert("list", "<li>" + user + "</li>");
		});
	}else{
		id("message").value="";
		id("message").placeholder="Type name of new chat";
		id("back").style.display= "none";
		id("send").textContent="Add chat";
		
		id("chat").innerHTML = "";
		id("list").innerHTML = "";
		data.chatlist.forEach(function (chat) {
			insert("list", "<li onclick=change(\""+chat+"\")>" + chat + "</li>");
		});
	}
}

function change(name){
	messageTyp="change";
	sendMessage(name);
	messageTyp="chat";
}

function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

function id(id) {
    return document.getElementById(id);
}
$(document).ready(function () {

    console.log("connecting with RSocket...");
    const transport = new RSocketWebSocketClient({
        url: "ws://localhost:80/rsocket"
    });
});


/*
function listenRequestStream() {
    const requestStream = new EventSource("http://localhost/rsocket/active");
    requestStream.addEventListener('open', event => console.log("Connection to request stream opened"));
}
*/
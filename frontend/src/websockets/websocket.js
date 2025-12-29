import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

let stompClient = null;

export function connectWebSocket(onMessage) {
  stompClient = new Client({
    webSocketFactory: () =>
      new SockJS("https://localhost:8443/ws", null, {
        withCredentials: true, // 🔥 THIS FIXES AUTH
      }),

    reconnectDelay: 5000,

    onConnect: () => {
      console.log("✅ STOMP connected");

      stompClient.subscribe("/user/queue/messages", (msg) => {
        onMessage(JSON.parse(msg.body));
      });
    },

    onStompError: (frame) => {
      console.error("❌ Broker error", frame.headers?.message);
    },
  });

  stompClient.activate();
}

export function sendMessage(message) {
  if (!stompClient || !stompClient.connected) return;

  stompClient.publish({
    destination: "/app/chat.send",
    body: JSON.stringify(message),
  });
}

export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
    console.log("🛑 STOMP disconnected");
  }
}

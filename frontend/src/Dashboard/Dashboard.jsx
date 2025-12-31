import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";

export default function Dashboard() {
  const [data, setData] = useState("");
  const stompClientRef = useRef(null);

  useEffect(() => {
    console.log("🔥 Dashboard mounted");

    // STEP 1: verify auth via HTTP (cookie-based JWT)
    fetch("https://localhost:8080/me", {
      credentials: "include", // 🔥 REQUIRED
    })
      .then(async (res) => {
        console.log("🔥 /me response status:", res.status);
        if (!res.ok) {
          throw new Error("Not authenticated");
        }
        return res.text(); // must match backend
      })
      .then((text) => {
        console.log("🔥 /me success:", text);
        setData(text);

        // STEP 2: connect WebSocket ONLY after auth
        connectWebSocket();
      })
      .catch((err) => {
        console.error("❌ Auth failed:", err);
      });

    // ❌ IMPORTANT: DO NOT deactivate in dev (StrictMode kills connection)
    return () => {
      console.log("🔥 Dashboard unmount");
    };
  }, []);

  const connectWebSocket = () => {
  console.log("🔥 connectWebSocket() CALLED");

  const socket = new WebSocket("wss://localhost:8080/ws");

  const client = new Client({
    webSocketFactory: () => socket,

    // cookie-based auth → browser sends cookies automatically
    connectHeaders: {},

    onConnect: () => {
      console.log("✅ STOMP CONNECTED");
    },

    onStompError: (frame) => {
      console.error("❌ STOMP ERROR:", frame.headers?.message, frame.body);
    },

    onWebSocketClose: (event) => {
      console.warn("⚠️ WebSocket CLOSED:", event);
    },

    onWebSocketError: (error) => {
      console.error("❌ WebSocket ERROR:", error);
    },

    debug: (msg) => console.log("🐛 STOMP:", msg),
  });

  console.log("🔥 Activating STOMP client");
  client.activate();

  stompClientRef.current = client;
};

  return (
    <div>
      <h2>Dashboard</h2>
      <p>{data}</p>
    </div>
  );
}

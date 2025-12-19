import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function ChatRoom() {
  const clientRef = useRef(null);

  const [connected, setConnected] = useState(false);
  const [receiver, setReceiver] = useState("");
  const [text, setText] = useState("");
  const [messages, setMessages] = useState([]);

  // 🔌 Connect once
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Login first");
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        setConnected(true);

        client.subscribe("/user/queue/messages", (msg) => {
          const message = JSON.parse(msg.body);
          setMessages((prev) => [...prev, message]);
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: (err) => console.error("STOMP error", err),
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, []);

  // 📤 Send message
  const sendMessage = () => {
    if (!connected) {
      alert("Not connected");
      return;
    }
    if (!receiver.trim() || !text.trim()) return;

    clientRef.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({
        receiver,
        content: text,
      }),
    });

    // optimistic UI (so sender sees message instantly)
    setMessages((prev) => [
      ...prev,
      {
        sender: "me",
        content: text,
        timestamp: new Date().toISOString(),
      },
    ]);

    setText("");
  };

  return (
    <div style={{ maxWidth: 500, margin: "auto" }}>
      <h3>Chat Room</h3>

      <div>
        Status:{" "}
        <b style={{ color: connected ? "green" : "red" }}>
          {connected ? "Connected" : "Disconnected"}
        </b>
      </div>

      <input
        placeholder="Receiver username"
        value={receiver}
        onChange={(e) => setReceiver(e.target.value)}
        style={{ width: "100%", marginTop: 10 }}
      />

      <div
        style={{
          border: "1px solid #ccc",
          height: 300,
          overflowY: "auto",
          marginTop: 10,
          padding: 5,
        }}
      >
        {messages.map((m, i) => (
          <div key={i} style={{ marginBottom: 6 }}>
            <b>{m.sender}</b>: {m.content}
            {m.timestamp && (
              <div style={{ fontSize: 10, color: "#777" }}>
                {new Date(m.timestamp).toLocaleTimeString()}
              </div>
            )}
          </div>
        ))}
      </div>

      <input
        placeholder="Type message"
        value={text}
        onChange={(e) => setText(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && sendMessage()}
        style={{ width: "78%", marginTop: 10 }}
      />

      <button onClick={sendMessage} style={{ width: "20%", marginLeft: "2%" }}>
        Send
      </button>
    </div>
  );
}

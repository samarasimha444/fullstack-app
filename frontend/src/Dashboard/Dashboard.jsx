import { useEffect, useRef, useState } from "react";
import Sidebar from "./SideBar.jsx";
import { Client } from "@stomp/stompjs";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [receiver, setReceiver] = useState(null);
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const [connected, setConnected] = useState(false);

  const stompClientRef = useRef(null);

  /* =====================
     AUTH CHECK
     ===================== */
  useEffect(() => {
    fetch("https://localhost:8080/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not authenticated");
        return res.json();
      })
      .then(setUser)
      .catch(() => {
        window.location.href =
          "https://localhost:8080/oauth2/authorization/google";
      });
  }, []);

  /* =====================
     WEBSOCKET CONNECT (NATIVE)
     ===================== */
  useEffect(() => {
    if (!user || !receiver) return;

    setMessages([]);
    setConnected(false);

    const client = new Client({
      brokerURL: "wss://localhost:8080/ws",
      reconnectDelay: 5000,

      debug: (msg) => console.log("🐛 STOMP:", msg),

      onConnect: () => {
        console.log("✅ STOMP CONNECTED");
        setConnected(true);

        client.subscribe("/user/queue/messages", (message) => {
          const body = JSON.parse(message.body);
          setMessages((prev) => [...prev, body]);
        });
      },

      onDisconnect: () => {
        console.log("❌ STOMP DISCONNECTED");
        setConnected(false);
      },

      onStompError: (frame) => {
        console.error("❌ STOMP ERROR", frame);
      },
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
      stompClientRef.current = null;
      setConnected(false);
    };
  }, [user, receiver]);

  /* =====================
     SEND MESSAGE
     ===================== */
  const sendMessage = () => {
    if (!connected || !text.trim() || !receiver) return;

    const message = {
      receiverId: receiver.id.toString(), // 🔥 MUST match principal.getName()
      content: text,
    };

    stompClientRef.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(message),
    });

    // optimistic UI
    setMessages((prev) => [
      ...prev,
      {
        senderId: user.id.toString(),
        receiverId: receiver.id.toString(),
        content: text,
      },
    ]);

    setText("");
  };

  if (!user) return <p>Loading...</p>;

  return (
    <div style={{ display: "flex", padding: 20 }}>
      {/* Sidebar */}
      <Sidebar onSelectReceiver={setReceiver} />

      {/* Main Area */}
      <div style={{ marginLeft: 30, width: "100%" }}>
        <h2>Dashboard</h2>

        <img
          src={user.picture}
          width={80}
          height={80}
          style={{ borderRadius: "50%" }}
        />

        <p><b>ID:</b> {user.id}</p>
        <p><b>Name:</b> {user.name}</p>
        <p><b>Email:</b> {user.email}</p>

        {receiver ? (
          <>
            <h4>Chat with {receiver.name}</h4>
            <p>Status: {connected ? "🟢 Connected" : "🔴 Connecting..."}</p>

            <div
              style={{
                border: "1px solid #ccc",
                height: 250,
                overflowY: "auto",
                padding: 10,
                marginBottom: 10,
              }}
            >
              {messages.map((m, i) => (
                <div key={i}>
                  <b>
                    {m.senderId === user.id.toString() ? "Me" : "Them"}:
                  </b>{" "}
                  {m.content}
                </div>
              ))}
            </div>

            <input
              type="text"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Type a message"
            />
            <button onClick={sendMessage} disabled={!connected}>
              Send
            </button>
          </>
        ) : (
          <p style={{ marginTop: 20 }}>No receiver selected</p>
        )}
      </div>
    </div>
  );
}

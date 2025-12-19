import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { jwtDecode } from "jwt-decode";

/* ---------- helpers ---------- */
const normalize = (v) =>
  typeof v === "string" ? v.trim().toLowerCase() : "";

const getCurrentUserFromJWT = () => {
  const token = localStorage.getItem("token");
  if (!token) return "";

  try {
    const decoded = jwtDecode(token);
    return normalize(decoded.sub); // 👈 username from JWT
  } catch {
    return "";
  }
};

export default function ChatRoom({ receiver }) {
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");

  const stompClient = useRef(null);
  const bottomRef = useRef(null);

  const currentUser = getCurrentUserFromJWT(); // ✅ SINGLE SOURCE OF TRUTH

  /* ===============================
     1️⃣ LOAD CHAT HISTORY (REST)
     =============================== */
  useEffect(() => {
    if (!receiver || !currentUser) return;

    fetch(`http://localhost:8080/chat/history?receiver=${receiver}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    })
      .then(res => res.json())
      .then(data => {
        const history = data.map(m => ({
          content: m.content,
          timestamp: m.timestamp,
          isMine: normalize(m.sender) === currentUser, // ✅ LEFT / RIGHT
        }));
        setMessages(history);
      })
      .catch(err => console.error("History error:", err));
  }, [receiver, currentUser]);

  /* ===============================
     2️⃣ WEBSOCKET (REAL-TIME)
     =============================== */
  useEffect(() => {
    if (!receiver || !currentUser) return;

    const socket = new SockJS("http://localhost:8080/ws");

    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      onConnect: () => {
        client.subscribe("/user/queue/messages", msg => {
          const incoming = JSON.parse(msg.body);

          setMessages(prev => [
            ...prev,
            {
              content: incoming.content,
              timestamp: new Date().toISOString(),
              isMine:
                normalize(incoming.sender) === currentUser, // ✅ SAME RULE
            },
          ]);
        });
      },
    });

    client.activate();
    stompClient.current = client;

    return () => {
      client.deactivate();
      stompClient.current = null;
    };
  }, [receiver, currentUser]);

  /* ===============================
     3️⃣ AUTO-SCROLL
     =============================== */
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  /* ===============================
     4️⃣ SEND MESSAGE (OPTIMISTIC)
     =============================== */
  const sendMessage = () => {
    if (!text.trim() || !stompClient.current) return;

    setMessages(prev => [
      ...prev,
      {
        content: text,
        timestamp: new Date().toISOString(),
        isMine: true, // 👈 sender is ME
      },
    ]);

    stompClient.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({
        receiver,
        content: text,
      }),
    });

    setText("");
  };

  if (!receiver) {
    return <div style={{ padding: 20 }}>Select a user to start chatting</div>;
  }

  /* ===============================
     UI
     =============================== */
  return (
    <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
      {/* Header */}
      <div
        style={{
          padding: "12px",
          borderBottom: "1px solid #ddd",
          fontWeight: "bold",
        }}
      >
        {receiver}
      </div>

      {/* Messages */}
      <div
        style={{
          flex: 1,
          overflowY: "auto",
          padding: "16px",
          backgroundColor: "#ECE5DD",
        }}
      >
        {messages.map((m, i) => (
          <div
            key={i}
            style={{
              display: "flex",
              justifyContent: m.isMine ? "flex-end" : "flex-start",
              marginBottom: "8px",
            }}
          >
            <div
              style={{
                backgroundColor: m.isMine ? "#DCF8C6" : "#FFFFFF",
                padding: "8px 12px",
                borderRadius: m.isMine
                  ? "12px 12px 0 12px"
                  : "12px 12px 12px 0",
                maxWidth: "65%",
                boxShadow: "0 1px 1px rgba(0,0,0,0.1)",
                fontSize: "14px",
              }}
            >
              <div>{m.content}</div>
              <div
                style={{
                  fontSize: "11px",
                  color: "#555",
                  textAlign: "right",
                  marginTop: "4px",
                }}
              >
                {new Date(m.timestamp).toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </div>
            </div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      {/* Input */}
      <div
        style={{
          display: "flex",
          padding: "10px",
          borderTop: "1px solid #ddd",
        }}
      >
        <input
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Type a message"
          style={{
            flex: 1,
            padding: "10px",
            borderRadius: "20px",
            border: "1px solid #ccc",
          }}
          onKeyDown={e => e.key === "Enter" && sendMessage()}
        />
        <button
          onClick={sendMessage}
          style={{
            marginLeft: "10px",
            padding: "10px 16px",
            borderRadius: "50%",
            border: "none",
            background: "#25D366",
            color: "#fff",
            cursor: "pointer",
          }}
        >
          ➤
        </button>
      </div>
    </div>
  );
}

import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { jwtDecode } from "jwt-decode";

const normalize = v =>
  typeof v === "string" ? v.trim().toLowerCase() : "";

const getCurrentUserFromJWT = () => {
  const token = localStorage.getItem("token");
  if (!token) return "";
  try {
    return normalize(jwtDecode(token).sub);
  } catch {
    return "";
  }
};

export default function ChatRoom({ receiver }) {
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const [onlineUsers, setOnlineUsers] = useState(new Set());

  const stompClient = useRef(null);
  const bottomRef = useRef(null);

  const currentUser = getCurrentUserFromJWT();

  /* ---------- CHAT HISTORY ---------- */
  useEffect(() => {
    if (!receiver || !currentUser) return;

    fetch(`http://localhost:8080/chat/history?receiver=${receiver}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    })
      .then(res => res.json())
      .then(data =>
        setMessages(
          data.map(m => ({
            content: m.content,
            timestamp: m.timestamp,
            isMine: normalize(m.sender) === currentUser,
          }))
        )
      );
  }, [receiver, currentUser]);

  /* ---------- WEBSOCKET ---------- */
  useEffect(() => {
    if (!receiver || !currentUser) return;

    const socket = new SockJS("http://localhost:8080/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      onConnect: () => {
        // 🔹 CHAT
        client.subscribe("/user/queue/messages", msg => {
          const incoming = JSON.parse(msg.body);
          setMessages(prev => [
            ...prev,
            {
              content: incoming.content,
              timestamp: new Date().toISOString(),
              isMine: normalize(incoming.sender) === currentUser,
            },
          ]);
        });

        // 🔹 INITIAL ONLINE USERS
        client.subscribe("/user/queue/presence-init", msg => {
          const { username } = JSON.parse(msg.body);
          setOnlineUsers(prev => new Set(prev).add(normalize(username)));
        });

        // 🔹 LIVE PRESENCE UPDATES
        client.subscribe("/topic/presence", msg => {
          const { username, status } = JSON.parse(msg.body);
          setOnlineUsers(prev => {
            const updated = new Set(prev);
            status === "ONLINE"
              ? updated.add(normalize(username))
              : updated.delete(normalize(username));
            return updated;
          });
        });
      },
    });

    client.activate();
    stompClient.current = client;

    return () => client.deactivate();
  }, [receiver, currentUser]);

  /* ---------- SCROLL ---------- */
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const sendMessage = () => {
    if (!text.trim() || !stompClient.current) return;

    stompClient.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({ receiver, content: text }),
    });

    setMessages(prev => [
      ...prev,
      { content: text, timestamp: new Date().toISOString(), isMine: true },
    ]);

    setText("");
  };

  if (!receiver) return <div>Select a user</div>;

  const isReceiverOnline = onlineUsers.has(normalize(receiver));

  return (
    <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
      <div style={{ padding: 12, borderBottom: "1px solid #ddd" }}>
        {receiver}{" "}
        <span style={{ color: isReceiverOnline ? "green" : "gray" }}>
          {isReceiverOnline ? "● Online" : "○ Offline"}
        </span>
      </div>

      <div style={{ flex: 1, overflowY: "auto", padding: 16 }}>
        {messages.map((m, i) => (
          <div key={i} style={{ textAlign: m.isMine ? "right" : "left" }}>
            {m.content}
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <input
        value={text}
        onChange={e => setText(e.target.value)}
        onKeyDown={e => e.key === "Enter" && sendMessage()}
      />
    </div>
  );
}

import { useEffect } from "react";

export default function ChatRoom({
  user,
  receiver,
  messages,
  setMessages,
  text,
  setText,
  sendMessage,
  connected,
  presenceMap,
}) {
  useEffect(() => {
    if (!receiver || !user) return;

    setMessages([]);

    fetch(`https://localhost:8443/history/${String(receiver.id)}`, {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load chat history");
        return res.json();
      })
      .then((history) => setMessages(history))
      .catch((err) => console.error("❌ Chat history error:", err));
  }, [receiver, user, setMessages]);

  if (!receiver) {
    return <p style={{ marginTop: 20 }}>No receiver selected</p>;
  }

  // ✅ Normalize presence lookup
  const receiverKey = String(receiver.id);

  const receiverStatus = presenceMap?.[receiverKey] ?? "OFFLINE";

  return (
    <>
      <h1>Chat with {receiver.name}</h1>

      <p>WebSocket: {connected ? "🟢 Connected" : "🔴 Connecting..."}</p>

      <p>
        Receiver:{" "}
        {receiverStatus === "ONLINE" ? "🟢 Online" : "⚫ Offline"}
      </p>

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
          <div key={m.id ?? i}>
            <b>{String(m.senderId) === String(user.id) ? "Me" : "Them"}:</b>{" "}
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

      <button onClick={sendMessage} disabled={!connected || !text.trim()}>
        Send
      </button>
    </>
  );
}

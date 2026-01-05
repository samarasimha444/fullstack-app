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
}) {
  /* =====================
     LOAD CHAT HISTORY
     ===================== */
  useEffect(() => {
    if (!receiver || !user) return;

    // clear old chat immediately
    setMessages([]);

    fetch(`https://localhost:8443/history/${receiver.id}`, {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to load chat history");
        return res.json();
      })
      .then((history) => {
        // history = [{ senderId, receiverId, content, timestamp }]
        setMessages(history);
      })
      .catch((err) => {
        console.error("❌ Chat history error:", err);
      });
  }, [receiver, user, setMessages]);

  /* =====================
     UI
     ===================== */
  if (!receiver) {
    return <p style={{ marginTop: 20 }}>No receiver selected</p>;
  }

  return (
    <>
      <h1>Chat with {receiver.name}</h1>
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
          <div key={m.id ?? i}>
            <b>{m.senderId === user.id.toString() ? "Me" : "Them"}:</b>{" "}
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

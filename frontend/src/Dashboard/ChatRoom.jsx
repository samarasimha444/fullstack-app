export default function ChatRoom({
  user,
  receiver,
  messages,
  text,
  setText,
  sendMessage,
  connected,
}) {
  if (!receiver) {
    return <p style={{ marginTop: 20 }}>No receiver selected</p>;
  }

  return (
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

      <button onClick={sendMessage} disabled={!connected}>
        Send
      </button>
    </>
  );
}

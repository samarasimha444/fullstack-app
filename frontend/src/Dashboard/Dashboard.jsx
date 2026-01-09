import { useEffect, useState } from "react";
import Sidebar from "./SideBar.jsx";
import ChatRoom from "./ChatRoom.jsx";
import Profile from "./Profile.jsx";
import { useChatSocket } from "./useChatSocket.js";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [receiver, setReceiver] = useState(null);
  const [text, setText] = useState("");

  /* =====================
     AUTH CHECK
     ===================== */
  useEffect(() => {
    fetch("https://localhost:8443/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not authenticated");
        return res.json();
      })
      .then((me) => {
        me.id = String(me.id);
        setUser(me);
      })
      .catch(() => {
        window.location.href =
          "https://localhost:8443/oauth2/authorization/google";
      });
  }, []);

  // ✅ ALL socket logic moved out
  const {
    connected,
    messages,
    setMessages,
    presenceMap,
    sendMessage: sendSocketMessage,
  } = useChatSocket(user);

  const sendMessage = () => {
    if (!connected || !text.trim() || !receiver || !user) return;

    const receiverId = String(receiver.id);

    const message = {
      receiverId,
      content: text,
    };

    const ok = sendSocketMessage(message);
    if (!ok) return;

    // optimistic UI
    setMessages((prev) => [
      ...prev,
      {
        senderId: String(user.id),
        receiverId,
        content: text,
      },
    ]);

    setText("");
  };

  if (!user) return <p>Loading...</p>;

  return (
    <div style={{ display: "flex", padding: 20 }}>
      <Sidebar
        onSelectReceiver={(r) => {
          if (!r) return setReceiver(null);
          r.id = String(r.id);
          setReceiver(r);
        }}
        presenceMap={presenceMap}
      />

      <div style={{ marginLeft: 30, width: "100%" }}>
        <Profile user={user} />

        <ChatRoom
          user={user}
          receiver={receiver}
          messages={messages}
          text={text}
          setText={setText}
          sendMessage={sendMessage}
          connected={connected}
          setMessages={setMessages}
          presenceMap={presenceMap}
        />
      </div>
    </div>
  );
}

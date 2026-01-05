import { useEffect, useRef, useState } from "react";
import Sidebar from "./SideBar.jsx";
import ChatRoom from "./ChatRoom.jsx";
import { Client } from "@stomp/stompjs";
import Profile from "./Profile.jsx";

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
    fetch("https://localhost:8443/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not authenticated");
        return res.json();
      })
      .then(setUser)
      .catch(() => {
        window.location.href =
          "https://localhost:8443/oauth2/authorization/google";
      });
  }, []);

  /* =====================
     WEBSOCKET CONNECT
     ===================== */
  useEffect(() => {
    if (!user || !receiver) return;

    setMessages([]);
    setConnected(false);

    const client = new Client({
      brokerURL: "wss://localhost:8443/ws",
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
      receiverId: receiver.id.toString(),
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

       <div style={{ marginLeft: 30, width: "100%" }}>
        


      <Sidebar onSelectReceiver={setReceiver} />

     


         <Profile user={user}/>
       

        <ChatRoom
          user={user}
          receiver={receiver}
          messages={messages}
          text={text}
          setText={setText}
          sendMessage={sendMessage}
          connected={connected}
          setMessages={setMessages}
        />
      </div>
    </div>
  );
}

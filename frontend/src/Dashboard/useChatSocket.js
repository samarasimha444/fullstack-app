import { useEffect, useRef, useState, useCallback } from "react";
import { Client } from "@stomp/stompjs";

export function useChatSocket(user) {
  const [connected, setConnected] = useState(false);
  const [messages, setMessages] = useState([]);
  const [presenceMap, setPresenceMap] = useState({});

  const stompClientRef = useRef(null);
  const heartbeatIntervalRef = useRef(null);

  useEffect(() => {
    if (!user?.id) return;

    setConnected(false);

    const client = new Client({
      brokerURL: "wss://localhost:8443/ws",
      reconnectDelay: 5000,
      debug: (msg) => console.log("🐛 STOMP:", msg),

      onConnect: () => {
        console.log("✅ STOMP CONNECTED");
        setConnected(true);

        // ✅ private messages
        client.subscribe("/user/queue/messages", (message) => {
          const body = JSON.parse(message.body);
          setMessages((prev) => [...prev, body]);
        });

        // ✅ presence events
        client.subscribe("/topic/presence", (message) => {
          const event = JSON.parse(message.body);
          console.log("✅ PRESENCE EVENT:", event);

          const key = String(event.userId);
          setPresenceMap((prev) => ({
            ...prev,
            [key]: event.status,
          }));
        });

        // ✅ heartbeat (every 5 sec)
        heartbeatIntervalRef.current = setInterval(() => {
          if (client.connected) {
            client.publish({
              destination: "/app/heartbeat",
              body: "{}",
            });
          }
        }, 5000);
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
      if (heartbeatIntervalRef.current) {
        clearInterval(heartbeatIntervalRef.current);
        heartbeatIntervalRef.current = null;
      }

      if (client.active) client.deactivate();
      stompClientRef.current = null;

      setConnected(false);
      setPresenceMap({});
      setMessages([]);
    };
  }, [user?.id]);

  // ✅ send message (hook exposes function)
  const sendMessage = useCallback((message) => {
    const client = stompClientRef.current;
    if (!client?.connected) return false;

    client.publish({
      destination: "/app/chat.send",
      body: JSON.stringify(message),
    });

    return true;
  }, []);

  return {
    connected,
    messages,
    setMessages,
    presenceMap,
    sendMessage,
  };
}

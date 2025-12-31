
import { Client } from "@stomp/stompjs";
export default function App() {



const client = new Client({
  brokerURL: "wss://localhost:8080/ws",
  onConnect: () => {
    console.log("✅ STOMP CONNECTED");
  },
  onStompError: (frame) => {
    console.error("❌ STOMP ERROR", frame);
  },
  debug: (msg) => console.log("🐛 STOMP:", msg),
});

client.activate();


return(
  <div>samara</div>
)
}
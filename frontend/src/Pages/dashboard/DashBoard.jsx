import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import SearchBar from "./SearchBar";
import ChatRoom from "./ChatRoom";
import Recents from "./Recents";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [receiver, setReceiver] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    try {
      const decoded = jwtDecode(token);
      if (decoded.exp * 1000 < Date.now()) {
        localStorage.removeItem("token");
        navigate("/login");
        return;
      }
      setUser(decoded.sub);
    } catch {
      navigate("/login");
    }
  }, [navigate]);

  if (!user) return null;

  return (
    <div style={{ display: "flex", height: "100vh" }}>
      <div style={{ width: 300, borderRight: "1px solid #ccc", padding: 10 }}>
        <SearchBar onSelectUser={setReceiver} />
        <Recents onSelectUser={setReceiver} />
      </div>

      <ChatRoom receiver={receiver} />
    </div>
  );
}

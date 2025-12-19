import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import SearchBar from "./SearchBar";

export default function Dashboard() {
  const [user, setUser] = useState(null);          // logged-in user
  const [receiver, setReceiver] = useState(null); // selected user
  const navigate = useNavigate();

  // 🔐 Auth guard
  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      navigate("/login");
      return;
    }

    try {
      const decoded = jwtDecode(token);
      const username = decoded.sub;

      if (!username) {
        navigate("/login");
        return;
      }

      if (decoded.exp * 1000 < Date.now()) {
        localStorage.removeItem("token");
        navigate("/login");
        return;
      }

      setUser(username);
    } catch {
      navigate("/login");
    }
  }, [navigate]);

  if (!user) return null;

  return (
    <div style={{ display: "flex", height: "100vh" }}>
      {/* LEFT PANEL */}
      <div
        style={{
          width: "30%",
          borderRight: "1px solid #ccc",
          padding: "1rem",
        }}
      >
        <h3>Welcome, {user}</h3>
        <SearchBar onSelectUser={setReceiver} />
      </div>

      {/* RIGHT PANEL – RECEIVER SHOWN CLEARLY */}
      <div
        style={{
          width: "70%",
          padding: "1rem",
          fontSize: "22px",
          fontWeight: "bold",
          color: "green",
        }}
      >
        {receiver ? (
          <>Selected user: {receiver}</>
        ) : (
          <>Select a user from the search bar</>
        )}
      </div>
    </div>
  );
}

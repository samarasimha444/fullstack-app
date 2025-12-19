import { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";


const normalize = (v) =>
  typeof v === "string" ? v.trim().toLowerCase() : "";

const getCurrentUserFromJWT = () => {
  const token = localStorage.getItem("token");
  if (!token) return "";

  try {
    const decoded = jwtDecode(token);
    return normalize(decoded.sub);
  } catch {
    return "";
  }
};

export default function Recents({ onSelectUser }) {
  const [recents, setRecents] = useState([]);
  const currentUser = getCurrentUserFromJWT();

  useEffect(() => {
    fetch("http://localhost:8080/chat/recents", {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    })
      .then(res => res.json())
      .then(data => {
        // data = List<Message>
        const mapped = data.map(m => {
          const otherUser =
            normalize(m.sender) === currentUser
              ? m.receiver
              : m.sender;

          return {
            username: otherUser,
            lastMessage: m.content,
            timestamp: m.timestamp,
          };
        });

        setRecents(mapped);
      })
      .catch(err => console.error("Failed to load recents", err));
  }, [currentUser]);

  return (
    <div
      style={{
        width: "280px",
        borderRight: "1px solid #ddd",
        height: "100vh",
        overflowY: "auto",
      }}
    >
      <div
        style={{
          padding: "12px",
          fontWeight: "bold",
          borderBottom: "1px solid #ddd",
        }}
      >
        Chats
      </div>

      {recents.map((r, i) => (
        <div
          key={i}
          onClick={() => onSelectUser(r.username)}
          style={{
            padding: "12px",
            cursor: "pointer",
            borderBottom: "1px solid #f0f0f0",
          }}
        >
          <div
            style={{
              fontWeight: "600",
              marginBottom: "4px",
            }}
          >
            {r.username}
          </div>

          <div
            style={{
              fontSize: "13px",
              color: "#555",
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
            }}
          >
            {r.lastMessage}
          </div>

          <div
            style={{
              fontSize: "11px",
              color: "#999",
              marginTop: "4px",
            }}
          >
            {new Date(r.timestamp).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </div>
        </div>
      ))}

      {recents.length === 0 && (
        <div style={{ padding: "12px", color: "#777" }}>
          No recent chats
        </div>
      )}
    </div>
  );
}

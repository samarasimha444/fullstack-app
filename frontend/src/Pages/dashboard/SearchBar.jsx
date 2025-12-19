import { useEffect, useState } from "react";

export default function SearchBar({ onSelectUser }) {
  const [query, setQuery] = useState("");
  const [users, setUsers] = useState([]);

  useEffect(() => {
    if (!query.trim()) {
      setUsers([]);
      return;
    }

    const controller = new AbortController();

    fetch(
      `http://localhost:8080/users/search?query=${encodeURIComponent(query)}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        signal: controller.signal,
      }
    )
      .then(res => res.json())
      .then(data => setUsers(data)) // data = ["mithin"]
      .catch(err => {
        if (err.name !== "AbortError") {
          console.error(err);
        }
      });

    return () => controller.abort();
  }, [query]);

  return (
    <div style={{ position: "relative" }}>
      <input
        type="text"
        placeholder="Search users..."
        value={query}
        onChange={e => setQuery(e.target.value)}
        style={{ width: "100%", padding: "8px" }}
      />

      {users.length > 0 && (
        <div
          style={{
            position: "absolute",
            background: "#fff",
            border: "1px solid #ccc",
            width: "100%",
            zIndex: 10,
          }}
        >
          {users.map((username, index) => (
            <div
              key={`${username}-${index}`}   // ✅ unique & valid
              style={{ padding: "8px", cursor: "pointer" }}
              onClick={() => {
                onSelectUser(username);
                setQuery("");
                setUsers([]);
              }}
            >
              {username}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

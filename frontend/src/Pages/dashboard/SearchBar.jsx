import { useEffect, useState } from "react";

export default function SearchBar() {
  const [query, setQuery] = useState("");
  const [users, setUsers] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);

  useEffect(() => {
    // Don’t hit API for empty input
    if (!query.trim()) {
      setUsers([]);
      return;
    }

    const controller = new AbortController();

    const timeout = setTimeout(() => {
      fetch(`http://localhost:8080/users/search?query=${query}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`
        },
        signal: controller.signal
      })
        .then(res => res.json())
        .then(data => {
          setUsers(data);
          setShowDropdown(true);
        })
        .catch(err => {
          if (err.name !== "AbortError") {
            console.error(err);
          }
        });
    }, 300); // 👈 debounce (important)

    return () => {
      clearTimeout(timeout);
      controller.abort();
    };
  }, [query]);

  const handleSelectUser = (username) => {
    console.log("receiver:", username);
    setShowDropdown(false);
    setQuery("");
  };

  return (
    <div style={{ position: "relative", width: "300px" }}>
      <input
        type="text"
        placeholder="Search users..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{ width: "100%", padding: "8px" }}
      />

      {showDropdown && users.length > 0 && (
        <div
          style={{
            position: "absolute",
            top: "100%",
            left: 0,
            right: 0,
            border: "1px solid #ccc",
            background: "#fff",
            zIndex: 10
          }}
        >
          {users.map((user) => (
            <button
              key={user}
              onClick={() => handleSelectUser(user)}
              style={{
                width: "100%",
                padding: "8px",
                textAlign: "left",
                border: "none",
                background: "white",
                cursor: "pointer"
              }}
            >
              {user}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

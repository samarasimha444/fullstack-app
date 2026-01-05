import { useEffect, useState } from "react";

export default function Sidebar({ onSelectReceiver }) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  /* =====================
     SEARCH AFTER @
     ===================== */
  useEffect(() => {
    const atIndex = query.indexOf("@");

    if (atIndex === -1 || query.length - atIndex < 3) {
      setResults([]);
      return;
    }

    const controller = new AbortController();

    fetch(
      `https://localhost:8443/receiver?q=${query.slice(atIndex + 1)}`,
      {
        credentials: "include",
        signal: controller.signal,
      }
    )
      .then((res) => res.json())
      .then(setResults)
      .catch(() => {});

    return () => controller.abort();
  }, [query]);

  return (
    <div style={{ width: 280 }}>
      <h4>Search User</h4>

      <input
        type="text"
        placeholder="Type @ to search user"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{ width: "100%" }}
      />

      {/* DROPDOWN */}
      {results.length > 0 && (
        <div
          style={{
            border: "1px solid #ccc",
            background: "#fff",
            marginTop: 5,
          }}
        >
          {results.map((u) => (
            <div
              key={u.id}
              style={{ padding: 8, cursor: "pointer" }}
              onClick={() => {
                onSelectReceiver({
                  id: u.id,
                  name: u.name,
                  email: u.email,
                  picture: u.picture,
                });
                setQuery(u.email);
                setResults([]);
              }}
            >
              {u.email}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

import { useEffect, useState } from "react";

export default function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/hello", {
      credentials: "include", // 🔥 REQUIRED for cookies
    })
      .then((res) => {
        if (res.status === 401) {
          setUser(null);
          return;
        }
        return res.json();
      })
      .then((data) => {
        if (data) setUser(data);
      })
      .finally(() => setLoading(false));
  }, []);

  const login = () => {
    // full redirect — NOT fetch
    window.location.href =
      "http://localhost:8080/oauth2/authorization/google";
  };

  if (loading) return <h2>Loading...</h2>;

  if (!user) {
    return (
      <div>
        <h2>Not logged in</h2>
        <button onClick={login}>Login with Google</button>
      </div>
    );
  }

  return (
    <div>
      <h2>Logged in</h2>
      <p>Name: {user.name}</p>
      <p>Email: {user.email}</p>
    </div>
  );
}

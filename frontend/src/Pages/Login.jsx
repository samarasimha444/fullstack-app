import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../login.css";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const usernameRegex = /^[a-zA-Z0-9]+$/;

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!usernameRegex.test(username)) {
      setError("Username can contain only letters and digits");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });

      const data = await res.text();

      if (!res.ok || data.startsWith("Invalid") || data.startsWith("No")) {
        setError(data);
        setLoading(false);
        return;
      }

      localStorage.setItem("token", data);
      navigate("/chat");
    } catch {
      setError("Server not reachable");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleLogin} >
        <h2>Login</h2>

        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
          disabled={loading}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          disabled={loading}
        />

        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>

        {error && <div className="error">{error}</div>}

        <div className="signup-link">
          <span>Don’t have an account?</span>
          <button
            type="button"
            className="link-button"
            onClick={() => navigate("/signup")}
          >
            Sign up
          </button>
        </div>
      </form>
    </div>
  );
}

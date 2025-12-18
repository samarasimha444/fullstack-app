import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../Signup.css";

export default function Signup() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const usernameRegex = /^[a-zA-Z0-9]+$/;

  const handleSignup = async (e) => {
    e.preventDefault();
    setError("");

    if (!usernameRegex.test(username)) {
      setError("Username can contain only letters and digits");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });

      const data = await res.text();

      if (!res.ok || data.startsWith("Username")) {
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
    <div className="signup-container">
      <form className="signup-form" onSubmit={handleSignup}>
        <h2>Signup</h2>

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
          {loading ? "Creating account..." : "Signup"}
        </button>

        {error && <div className="error">{error}</div>}

        <div className="login-link">
          <span>Already have an account?</span>
          <button
            type="button"
            className="link-button"
            onClick={() => navigate("/login")}
          >
            Login
          </button>
        </div>
      </form>
    </div>
  );
}

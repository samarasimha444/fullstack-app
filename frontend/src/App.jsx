import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./Dashboard";

function Login() {
  const handleLogin = () => {
    // Redirect to BACKEND OAuth endpoint
    window.location.href =
      "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <div style={{ padding: 40 }}>
      <h2>Login</h2>
      <button onClick={handleLogin}>
        Login with Google
      </button>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

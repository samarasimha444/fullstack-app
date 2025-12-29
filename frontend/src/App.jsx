import { useEffect } from "react";
import { BrowserRouter, Routes, Route, useNavigate } from "react-router-dom";
import Dashboard from "./dashboard/DashbBoard.jsx";

/* =========================
   HOME (Auth Gate)
   ========================= */
function Home() {
  const navigate = useNavigate();

  useEffect(() => {
    fetch("https://localhost:8443/data", {
      credentials: "include", // 🔥 required for HttpOnly cookies
    })
      .then((res) => {
        if (res.ok) {
          // ✅ already authenticated
          navigate("/dashboard", { replace: true });
        } else {
          // ❌ not authenticated → Google login
          window.location.href =
            "https://localhost:8443/oauth2/authorization/google";
        }
      })
      .catch(() => {
        // backend down / network issue → still redirect to login
        window.location.href =
          "https://localhost:8443/oauth2/authorization/google";
      });
  }, [navigate]);

  return <h2>Checking authentication...</h2>;
}

/* =========================
   APP ROUTER
   ========================= */
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/dashboard" element={<Dashboard />} />
      </Routes>
    </BrowserRouter>
  );
}

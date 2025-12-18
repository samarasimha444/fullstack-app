import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

export default function Chat() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    
    if (!token) {
      navigate("/login");
      return;}

    try {
      const decoded = jwtDecode(token);

      // backend stored username in `sub`
      if (!decoded.sub) {
        throw new Error("Invalid token");
      }

    setUsername(decoded.sub);
    } catch {
      localStorage.removeItem("token");
      navigate("/login");
    }
  }, [navigate]);

  return (
    <div style={{ padding: "20px" }}>
     
      <h1>
       {username}
      </h1>
    </div>
  );
}

import { useEffect, useState } from "react";


export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");


  useEffect(() => {
    fetch("https://localhost:8080/me", {
      credentials: "include", // 🔥 sends JWT cookie
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not authenticated");
        return res.json();
      })
      .then((data) => {
        setUser(data);
      })
      .catch((err) => {
       //navigate to https://localhost:8080/oauth2/authorization/google
       window.location.href = "https://localhost:8080/oauth2/authorization/google";
      });
  }, []);

  if (error) {
    return <p>{error}</p>;
  }

  if (!user) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <h2>Dashboard</h2>

      <img
        src={user.picture}
        
        width={80}
        height={80}
        style={{ borderRadius: "50%" }}
      />

      <p><b>ID:</b> {user.id}</p>
      <p><b>Name:</b> {user.name}</p>
      <p><b>Email:</b> {user.email}</p>
      <p>{user.picture}</p>
    </div>
  );
}

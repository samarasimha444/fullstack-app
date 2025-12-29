import { useEffect, useState } from "react";

export default function Dashboard() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/data", {
      credentials: "include", // 🔥 send cookie
    })
      .then((res) => {
        if (res.status === 401) {
          window.location.href = "/";
          return null;
        }
        return res.json();
      })
      .then((data) => {
        if (!data) return;
        setUser(data);
        setLoading(false);
      })
      .catch(() => {
        window.location.href = "/";
      });
  }, []);

  if (loading) return <h2>Loading dashboard...</h2>;

  return (
    <div>
        <div>
        <div>{user.name}</div>
        <div>{user.userId}</div>
        <div>{user.email}</div>
        <div>{user.picture}</div>
        </div>
    </div>

    );
}

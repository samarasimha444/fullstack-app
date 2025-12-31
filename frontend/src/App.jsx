import { useEffect, useState } from "react";

export default function App() {
  const [data, setData] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/me", {
      credentials: "include" // 🔥 THIS sends the JWT cookie
    })
      .then(res => res.text())
      .then(data => setData(data));
  }, []); // 👈 add dependency array
          
  return (
    <div>
      {data}
    </div>
  );
}

import { useEffect } from "react";

export default function Home() {
  useEffect(() => {
    fetch("http://localhost:8080/data", {
      credentials: "include", // 🔥 REQUIRED for cookies
    })
      .then((res) => {
        if (res.ok) {
          // Already logged in
          window.location.href = "/dashboard";
        } else {
          // Not authenticated → go to Google
          window.location.href =
            "http://localhost:8080/oauth2/authorization/google";
        }
      })
      .catch(() => {
        // Network / backend down → still go to login
        window.location.href =
          "http://localhost:8080/oauth2/authorization/google";
      });
  }, []);

  return <h1>Checking authentication...</h1>;
}

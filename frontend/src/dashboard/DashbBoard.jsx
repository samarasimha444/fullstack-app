import { useEffect, useState } from "react";
import Profile from "./SideBar/Profile";
import Recents from "./SideBar/Recents";
import ChatRoom from "./ChatRoom/ChatRoom";

export default function DashBoard() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch("https://localhost:8443/data", {
      credentials: "include",
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
      })
      .catch(() => {
        window.location.href = "/";
      });
  }, []);

  if (!user) return <h2>Loading...</h2>;

  return (
    <>
      <Profile user={user} />
      <Recents user={user} />
      <ChatRoom user={user} />
    </>
  );
}

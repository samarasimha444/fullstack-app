import ChatRoomHeader from "./ChatRoomHeader.jsx";
export default function ChatRoom({user}){
    return(
        <div>
            <ChatRoomHeader user={user} />
            <h1>Chat Room</h1>
        </div>
    )
} 
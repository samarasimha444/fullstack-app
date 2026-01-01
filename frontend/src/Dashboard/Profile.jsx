export default function Profile({user}){
    return(
       <div>
         <img
                  src={user.picture}
                  width={80}
                  height={80}
                  style={{ borderRadius: "50%" }}
                />
                
                <p><b>ID:</b> {user.id}</p>
                <p><b>Name:</b> {user.name}</p>
                <p><b>Email:</b> {user.email}</p>

       </div>

    )
}
import { BrowserRouter,Routes,Route  } from "react-router-dom";
import DashBoard from "./Dashboard/Dashboard.jsx";
import Home from "./Home.jsx";
export default function App(){
    return(
        <BrowserRouter>
        <Routes>
            <Route path="/" element={<Home/>} />
            <Route path="/dashboard" element={<DashBoard/>} />
        </Routes>

        </BrowserRouter>
    )
}
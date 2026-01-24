import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { useAuth } from 'oidc-react';
import Home from './pages/Home';
import PaymentResult from "./pages/PaymentResult.jsx";

function App() {
    const auth = useAuth();

    if (auth.isLoading) return <div>Loading authentication...</div>;

    if (!auth.userData) {
        return (
            <div className="container mt-5 text-center">
                <h1>Please log in to continue</h1>
                <button className="btn btn-primary" onClick={() => auth.signIn()}>Login</button>
            </div>
        );
    }

    return (
        <Router>
            <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
                <div className="container">
                    <Link className="navbar-brand" border="1" to="/">Shop Manager</Link>
                    <div className="navbar-nav">
                        <Link className="nav-link" to="/">Home</Link>
                    </div>
                    <button className="btn btn-outline-light ms-auto" onClick={() => auth.signOut()}>Logout</button>
                </div>
            </nav>
            <div className="container">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/payment-result" element={<PaymentResult />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { MapPin, LogIn, LogOut, Package, HeartPulse } from 'lucide-react';

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light glass-panel sticky-top mb-4">
      <div className="container">
        {/* Brand */}
        <Link className="navbar-brand d-flex align-items-center fw-bold text-primary" to="/">
          <HeartPulse className="me-2" size={28} />
          PharmaMap
        </Link>
        
        {/* Mobile Toggle Toggle */}
        <button 
          className="navbar-toggler border-0 shadow-none" 
          type="button" 
          data-bs-toggle="collapse" 
          data-bs-target="#navbarNav"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        {/* Links */}
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link className="nav-link fw-medium" to="/">Find Medicines</Link>
            </li>
          </ul>

          {/* Auth Conditional Renders */}
          <div className="d-flex align-items-center">
            {isAuthenticated ? (
              <>
                <div className="text-secondary small me-4 d-none d-md-block">
                  Signed in as <strong>{user?.email}</strong>
                </div>
                {user?.role === 'PHARMACY' && (
                  <Link to="/dashboard" className="btn btn-outline-primary me-2 d-flex align-items-center">
                    <Package size={18} className="me-2"/> Dashboard
                  </Link>
                )}
                <button onClick={handleLogout} className="btn btn-primary d-flex align-items-center">
                  <LogOut size={18} className="me-2"/> Logout
                </button>
              </>
            ) : (
              <Link to="/login" className="btn btn-primary d-flex align-items-center">
                <LogIn size={18} className="me-2"/> Pharmacy Login
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;

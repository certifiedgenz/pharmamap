import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';

import Home from './pages/Home';
import SearchResults from './pages/SearchResults';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import PharmacyRegistration from './pages/PharmacyRegistration';

// Protected Route Wrapper for Pharmacy Dashboard
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, user, loading } = useAuth();
  
  if (loading) return <div className="text-center mt-5"><span className="spinner-border"></span></div>;
  
  if (!isAuthenticated || user?.role !== 'PHARMACY') {
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="d-flex flex-column min-vh-100">
          <Navbar />
          <main className="flex-grow-1">
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<Home />} />
              <Route path="/search" element={<SearchResults />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<PharmacyRegistration />} />
              
              {/* Protected Routes */}
              <Route 
                path="/dashboard" 
                element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                } 
              />
            </Routes>
          </main>
          
          <footer className="bg-white py-4 mt-auto border-top">
            <div className="container text-center text-muted">
              <small>&copy; {new Date().getFullYear()} PharmaMap. All rights reserved.</small>
            </div>
          </footer>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;

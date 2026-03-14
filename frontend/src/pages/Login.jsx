import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Lock, Mail, AlertCircle } from 'lucide-react';

const Login = () => {
  const [email, setEmail] = useState('pharmacy@example.com'); // Pre-fill with user's test data
  const [password, setPassword] = useState('securepassword123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) return;
    
    setLoading(true);
    setError('');
    
    const result = await login(email, password);
    
    if (result.success) {
      navigate('/dashboard');
    } else {
      setError(result.message);
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5 pt-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-6 col-lg-5">
          <div className="glass-panel p-5 animate-fade-in">
            <div className="text-center mb-4">
              <div className="bg-primary text-white p-3 rounded-circle d-inline-flex mb-3 shadow">
                <Lock size={32} />
              </div>
              <h2 className="fw-bold">Pharmacy Portal</h2>
              <p className="text-muted">Manage your inventory and presence</p>
            </div>

            {error && (
              <div className="alert alert-danger d-flex align-items-center" role="alert">
                <AlertCircle className="me-2 flex-shrink-0" size={18} />
                <div>{error}</div>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-4">
                <label className="form-label text-secondary fw-medium small">Email Address</label>
                <div className="input-group">
                  <span className="input-group-text bg-white text-muted border-end-0">
                    <Mail size={18} />
                  </span>
                  <input 
                    type="email" 
                    className="form-control border-start-0 ps-0" 
                    placeholder="name@pharmacy.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="mb-4">
                <label className="form-label text-secondary fw-medium small">Password</label>
                <div className="input-group">
                  <span className="input-group-text bg-white text-muted border-end-0">
                    <Lock size={18} />
                  </span>
                  <input 
                    type="password" 
                    className="form-control border-start-0 ps-0" 
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>

              <button 
                type="submit" 
                className="btn btn-primary w-100 py-2 fw-bold d-flex justify-content-center align-items-center"
                disabled={loading}
              >
                {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : null}
                {loading ? 'Authenticating...' : 'Sign In securely'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;

import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { User, Mail, Lock, Building, FileText, MapPin, AlertCircle } from 'lucide-react';

const PharmacyRegistration = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    pharmacyName: '',
    licenseNumber: '',
    address: '',
    city: '',
    pincode: '',
    latitude: 0.0,
    longitude: 0.0
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    // Auto-parse floats if coordinates
    const parsedValue = (name === 'latitude' || name === 'longitude') ? parseFloat(value) || 0 : value;
    setFormData(prev => ({ ...prev, [name]: parsedValue }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    const result = await register(formData);

    if (result.success) {
      navigate('/dashboard'); // Auto-login on success
    } else {
      setError(result.message);
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5 pt-3 mb-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-7">
          <div className="glass-panel p-5 animate-fade-in">
            <div className="text-center mb-4">
              <div className="bg-primary text-white p-3 rounded-circle d-inline-flex mb-3 shadow">
                <Building size={32} />
              </div>
              <h2 className="fw-bold">Partner with PharmaMap</h2>
              <p className="text-muted">Register your pharmacy to reach thousands of customers nearby.</p>
            </div>

            {error && (
              <div className="alert alert-danger d-flex align-items-center" role="alert">
                <AlertCircle className="me-2 flex-shrink-0" size={18} />
                <div>{error}</div>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <h5 className="mb-3 text-secondary border-bottom pb-2">Account Details</h5>
              
              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label text-secondary fw-medium small">Owner Name</label>
                  <div className="input-group">
                    <span className="input-group-text bg-white text-muted border-end-0">
                      <User size={18} />
                    </span>
                    <input type="text" className="form-control border-start-0 ps-0" name="name" 
                      placeholder="John Doe" value={formData.name} onChange={handleChange} required />
                  </div>
                </div>
                <div className="col-md-6 mt-3 mt-md-0">
                  <label className="form-label text-secondary fw-medium small">Email Address</label>
                  <div className="input-group">
                    <span className="input-group-text bg-white text-muted border-end-0">
                      <Mail size={18} />
                    </span>
                    <input type="email" className="form-control border-start-0 ps-0" name="email" 
                      placeholder="owner@pharmacy.com" value={formData.email} onChange={handleChange} required />
                  </div>
                </div>
              </div>

              <div className="row mb-4">
                <div className="col-md-6">
                  <label className="form-label text-secondary fw-medium small">Phone Number</label>
                  <div className="input-group">
                    <span className="input-group-text bg-white text-muted border-end-0">
                      +
                    </span>
                    <input type="text" className="form-control border-start-0 ps-0" name="phone" 
                      placeholder="9876543210" value={formData.phone} onChange={handleChange} required />
                  </div>
                </div>
                <div className="col-md-6 mt-3 mt-md-0">
                  <label className="form-label text-secondary fw-medium small">Secure Password</label>
                  <div className="input-group">
                    <span className="input-group-text bg-white text-muted border-end-0">
                      <Lock size={18} />
                    </span>
                    <input type="password" className="form-control border-start-0 ps-0" name="password" 
                      placeholder="••••••••" value={formData.password} onChange={handleChange} required minLength="6" />
                  </div>
                </div>
              </div>

              <h5 className="mb-3 text-secondary border-bottom pb-2 mt-4">Pharmacy Details</h5>

              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label text-secondary fw-medium small">Pharmacy Name</label>
                  <div className="input-group">
                    <span className="input-group-text bg-white text-muted border-end-0">
                      <Building size={18} />
                    </span>
                    <input type="text" className="form-control border-start-0 ps-0" name="pharmacyName" 
                      placeholder="HealthPlus Pharmacy" value={formData.pharmacyName} onChange={handleChange} required />
                  </div>
                </div>
                <div className="col-md-6 mt-3 mt-md-0">
                  <label className="form-label text-secondary fw-medium small">License Number</label>
                  <div className="input-group">
                    <span className="input-group-text bg-white text-muted border-end-0">
                      <FileText size={18} />
                    </span>
                    <input type="text" className="form-control border-start-0 ps-0" name="licenseNumber" 
                      placeholder="DL-123456" value={formData.licenseNumber} onChange={handleChange} required />
                  </div>
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label text-secondary fw-medium small">Street Address</label>
                <div className="input-group">
                  <span className="input-group-text bg-white text-muted border-end-0">
                    <MapPin size={18} />
                  </span>
                  <input type="text" className="form-control border-start-0 ps-0" name="address" 
                    placeholder="123 Medic Way" value={formData.address} onChange={handleChange} required />
                </div>
              </div>

              <div className="row mb-3">
                <div className="col-md-6">
                  <label className="form-label text-secondary fw-medium small">City</label>
                  <input type="text" className="form-control" name="city" 
                    placeholder="New York" value={formData.city} onChange={handleChange} required />
                </div>
                <div className="col-md-6 mt-3 mt-md-0">
                  <label className="form-label text-secondary fw-medium small">Pincode</label>
                  <input type="text" className="form-control" name="pincode" 
                    placeholder="10001" value={formData.pincode} onChange={handleChange} required />
                </div>
              </div>

              <div className="row mb-4">
                <div className="col-md-6">
                  <label className="form-label text-secondary fw-medium small">Map Latitude (Optional)</label>
                  <input type="number" step="any" className="form-control" name="latitude" 
                    placeholder="e.g. 40.7128" value={formData.latitude || ''} onChange={handleChange} />
                </div>
                <div className="col-md-6 mt-3 mt-md-0">
                  <label className="form-label text-secondary fw-medium small">Map Longitude (Optional)</label>
                  <input type="number" step="any" className="form-control" name="longitude" 
                    placeholder="e.g. -74.0060" value={formData.longitude || ''} onChange={handleChange} />
                </div>
              </div>

              <button 
                type="submit" 
                className="btn btn-primary w-100 py-3 fw-bold d-flex justify-content-center align-items-center mt-2"
                disabled={loading}
              >
                {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : null}
                {loading ? 'Creating Account...' : 'Register Pharmacy'}
              </button>
              
              <div className="text-center mt-4">
                <p className="text-muted small">
                  Already partnered with us? <Link to="/login" className="text-primary fw-medium text-decoration-none hover-underline">Sign In here</Link>
                </p>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PharmacyRegistration;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, MapPin, Navigation } from 'lucide-react';

const MOCK_GEOCODE = {
  "new york": { lat: 40.7128, lng: -74.0060 },
  "london": { lat: 51.5074, lng: -0.1278 },
  "tokyo": { lat: 35.6762, lng: 139.6503 },
  "mumbai": { lat: 18.9218, lng: 72.8347 }
};

const Home = () => {
  const [query, setQuery] = useState('');
  const [locationText, setLocationText] = useState('Mumbai');
  const [loadingLocation, setLoadingLocation] = useState(false);
  const [coordinates, setCoordinates] = useState({ lat: 18.9218, lng: 72.8347 }); // Default coordinates
  const navigate = useNavigate();

  const handleSearch = (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    
    // Quick mock geocode for the aesthetic UX
    const cityKey = locationText.toLowerCase().trim();
    let finalLat = coordinates.lat;
    let finalLng = coordinates.lng;
    
    if (MOCK_GEOCODE[cityKey]) {
        finalLat = MOCK_GEOCODE[cityKey].lat;
        finalLng = MOCK_GEOCODE[cityKey].lng;
    }
    
    // Pass coordinates and query to the search page visually
    navigate(`/search?medicine=${encodeURIComponent(query)}&lat=${finalLat}&lng=${finalLng}`);
  };

  const getUserLocation = () => {
    setLoadingLocation(true);
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setCoordinates({
            lat: position.coords.latitude,
            lng: position.coords.longitude
          });
          setLocationText("Current Location");
          setLoadingLocation(false);
        },
        (error) => {
          console.error("Error getting location", error);
          alert("Could not fetch location. Using default city.");
          setLoadingLocation(false);
        }
      );
    } else {
      alert("Geolocation is not supported by your browser");
      setLoadingLocation(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center align-items-center" style={{ minHeight: '70vh' }}>
        <div className="col-12 col-md-8 col-lg-6 text-center animate-fade-in">
          
          <h1 className="fw-bold mb-4" style={{ fontSize: '3rem' }}>
            Find Medicines <br />
            <span className="text-primary">Near You</span>
          </h1>
          
          <p className="text-muted mb-5 lead">
            Instantly search real-time stock across local pharmacies. Never bounce between medical stores again.
          </p>

          <div className="glass-panel p-4 mb-5">
            <form onSubmit={handleSearch}>
              <div className="input-group input-group-lg mb-3 shadow-sm rounded-pill overflow-hidden">
                <span className="input-group-text bg-white border-0 ps-4">
                  <Search className="text-muted" size={20} />
                </span>
                <input 
                  type="text" 
                  className="form-control border-0 shadow-none ps-2" 
                  placeholder="e.g. Paracetamol, Azithromycin..." 
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  autoFocus
                />
                <span className="input-group-text bg-white border-0 border-start ps-3 rounded-0">
                  <MapPin className="text-danger" size={20} />
                </span>
                <input 
                  type="text" 
                  className="form-control border-0 shadow-none ps-2" 
                  style={{maxWidth: '180px'}}
                  placeholder="City or Zip" 
                  value={locationText}
                  onChange={(e) => setLocationText(e.target.value)}
                />
                <button type="submit" className="btn btn-primary px-4 fw-bold shadow-none" disabled={!query.trim()}>
                  Search
                </button>
              </div>
              
              <div className="d-flex justify-content-center align-items-center mt-3 text-secondary small">
                <button 
                  type="button"
                  className="btn btn-sm btn-link text-decoration-none ms-2"
                  onClick={getUserLocation}
                  disabled={loadingLocation}
                >
                  {loadingLocation ? "Locating..." : "📍 Use my current GPS coordinates"}
                </button>
              </div>
            </form>
          </div>
          
        </div>
      </div>
    </div>
  );
};

export default Home;

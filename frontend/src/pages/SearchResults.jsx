import React, { useState, useEffect, useMemo } from 'react';
import { useLocation, Link } from 'react-router-dom';
import API from '../services/api';
import { MapPin, Pill, Phone, Building2, Navigation, AlertTriangle } from 'lucide-react';
import { GoogleMap, useLoadScript, Marker, InfoWindow } from '@react-google-maps/api';

const mapContainerStyle = {
  width: '100%',
  height: 'calc(100vh - 180px)', // sticky height relative to viewport
  borderRadius: '0.5rem',
  boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)'
};

const SearchResults = () => {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedMarker, setSelectedMarker] = useState(null);
  
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const medicineName = queryParams.get('medicine');
  const lat = parseFloat(queryParams.get('lat'));
  const lng = parseFloat(queryParams.get('lng'));

  // Load Google Maps script
  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: import.meta.env.VITE_GOOGLE_MAPS_API_KEY || "YOUR_FALLBACK_TEST_KEY", 
  });

  const mapCenter = useMemo(() => ({ lat: lat || 0, lng: lng || 0 }), [lat, lng]);

  useEffect(() => {
    const fetchResults = async () => {
      try {
        setLoading(true);
        const response = await API.get(`/search?medicine=${medicineName}&lat=${lat}&lng=${lng}`);
        setResults(response.data.data || []);
        setError(null);
      } catch (err) {
        console.error("Search failed", err);
        setError("Could not fetch available pharmacies. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    if (medicineName && lat && lng) {
      fetchResults();
    }
  }, [medicineName, lat, lng]);

  if (loading) {
    return (
      <div className="container mt-5 pt-5 text-center">
        <div className="spinner-border text-primary shadow-sm" role="status" style={{ width: '3rem', height: '3rem' }}></div>
        <h4 className="mt-4 text-secondary">Scanning nearby pharmacies...</h4>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold mb-1">
            Results for <span className="text-primary">'{medicineName}'</span>
          </h2>
          <p className="text-muted d-flex align-items-center">
            <MapPin size={16} className="me-1" />
            Showing stock near coordinates [{parseFloat(lat).toFixed(2)}, {parseFloat(lng).toFixed(2)}]
          </p>
        </div>
        <Link to="/" className="btn btn-outline-secondary btn-sm rounded-pill px-3">
          New Search
        </Link>
      </div>

      {error ? (
        <div className="alert alert-danger bg-white border-danger shadow-sm d-flex align-items-center">
          <AlertTriangle className="text-danger me-3" size={24} />
          <div>{error}</div>
        </div>
      ) : results.length === 0 ? (
        <div className="glass-panel p-5 text-center my-4 animate-fade-in">
          <Pill size={48} className="text-secondary opacity-50 mb-3" />
          <h3 className="fw-bold text-dark mb-2">No Stock Detected</h3>
          <p className="text-muted mb-0">We couldn't find any nearby pharmacies currently stocking "{medicineName}". <br/>Try expanding your search criteria or looking a bit further away.</p>
        </div>
      ) : (
        <div className="row g-4 animate-fade-in">
          {/* Left Column: List of Pharmacies */}
          <div className="col-12 col-xl-5 order-2 order-xl-1" style={{ maxHeight: 'calc(100vh - 180px)', overflowY: 'auto' }}>
            <div className="d-flex flex-column gap-3 pe-md-2">
              {results.map((item, index) => (
                <div 
                  className={`card p-3 bg-white position-relative transition-all cursor-pointer ${selectedMarker?.pharmacyId === item.pharmacyId ? 'border-primary border-2 shadow' : 'border'}`} 
                  key={index}
                  onClick={() => setSelectedMarker(item)}
                  style={{ cursor: 'pointer' }}
                >
                  {/* Distance Badge */}
                  <div className="position-absolute top-0 end-0 mt-3 me-3 bg-primary text-white px-2 py-1 rounded small fw-bold shadow-sm z-1 d-flex align-items-center">
                    <Navigation size={14} className="me-1"/> {item.distanceInKm.toFixed(1)} km
                  </div>

                  <div className="card-body p-1">
                    <h5 className="card-title text-dark fw-bold mb-1">{item.medicineName}</h5>
                    <p className="small text-secondary fw-medium mb-3">
                      {item.strength} • {item.form}
                    </p>

                    <div className="d-flex justify-content-between align-items-center mb-0 px-3 py-2 bg-light rounded border border-white">
                      <div className="text-start">
                        <span className="small text-muted d-block">Available</span>
                        <strong className={item.quantityAvailable > 10 ? "text-success" : "text-warning"}>
                          {item.quantityAvailable} units
                        </strong>
                      </div>
                      <div className="text-end">
                        <span className="small text-muted d-block">Price</span>
                        <strong className="text-dark">${item.price.toFixed(2)}</strong>
                      </div>
                    </div>

                    <hr className="text-muted opacity-25 my-3" />
                    
                    <div className="d-flex align-items-center mb-2">
                      <div className="bg-primary bg-opacity-10 p-2 rounded-circle me-3">
                        <Building2 size={20} className="text-primary" />
                      </div>
                      <div>
                        <h6 className="mb-0 fw-bold">{item.pharmacyName}</h6>
                        <small className="text-muted d-block text-truncate" style={{maxWidth: '220px'}}>{item.address}</small>
                      </div>
                    </div>
                    
                    <div className="d-flex align-items-center mt-3">
                      <a href={`tel:${item.phone}`} className="btn btn-sm btn-outline-primary rounded-pill px-3 py-1 flex-grow-1" onClick={(e) => e.stopPropagation()}>
                        <Phone size={14} className="me-2" /> Call Pharmacy
                      </a>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Right Column: Google Map */}
          <div className="col-12 col-xl-7 order-1 order-xl-2 mb-4 mb-xl-0">
             {loadError ? (
                <div className="alert alert-warning">Error loading maps.</div>
             ) : !isLoaded ? (
                <div className="d-flex justify-content-center align-items-center h-100 bg-light rounded" style={{ minHeight: '400px'}}>
                   <div className="spinner-border text-primary" role="status"></div>
                </div>
             ) : (
                <div className="position-sticky" style={{ top: '100px' }}>
                  <GoogleMap
                    mapContainerStyle={mapContainerStyle}
                    zoom={12}
                    center={mapCenter}
                    options={{
                      disableDefaultUI: false,
                      zoomControl: true,
                      streetViewControl: false,
                      mapTypeControl: false,
                      fullscreenControl: true,
                    }}
                  >
                    {/* Marker for User Search Location */}
                    <Marker
                      position={mapCenter}
                      icon={{
                        url: "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
                      }}
                      title="Your Search Center"
                    />

                    {/* Markers for Pharmacies */}
                    {results.map((pharmacy) => (
                      <Marker
                        key={pharmacy.pharmacyId}
                        position={{ lat: pharmacy.latitude, lng: pharmacy.longitude }}
                        onClick={() => setSelectedMarker(pharmacy)}
                        title={pharmacy.pharmacyName}
                        icon={{
                            url: results[0].pharmacyId === pharmacy.pharmacyId 
                                  ? "http://maps.google.com/mapfiles/ms/icons/green-dot.png" // Closest is green
                                  : "http://maps.google.com/mapfiles/ms/icons/red-dot.png"
                        }}
                      />
                    ))}

                    {/* InfoWindow Popup when a marker is clicked */}
                    {selectedMarker && (
                      <InfoWindow
                        position={{ lat: selectedMarker.latitude, lng: selectedMarker.longitude }}
                        onCloseClick={() => setSelectedMarker(null)}
                      >
                        <div style={{ padding: '4px', maxWidth: '200px' }}>
                          <h6 className="fw-bold mb-1 text-primary">{selectedMarker.pharmacyName}</h6>
                          <p className="small text-muted mb-2 border-bottom pb-1">{selectedMarker.address}</p>
                          <div className="d-flex justify-content-between small fw-medium mb-1">
                            <span>{selectedMarker.medicineName}</span>
                            <span>${selectedMarker.price.toFixed(2)}</span>
                          </div>
                          <div className="d-flex justify-content-between small">
                            <span>Stock:</span>
                            <span className={selectedMarker.quantityAvailable > 10 ? 'text-success' : 'text-danger'}>
                              {selectedMarker.quantityAvailable} units
                            </span>
                          </div>
                          <a href={`tel:${selectedMarker.phone}`} className="btn btn-sm btn-primary w-100 mt-2 py-0" style={{fontSize: '0.8rem'}}>
                            Call
                          </a>
                        </div>
                      </InfoWindow>
                    )}
                  </GoogleMap>
                </div>
             )}
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchResults;

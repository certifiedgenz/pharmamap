import axios from 'axios';

// Create a global Axios instance
const API = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // Use relative path for production Nginx proxying, or build-time ENV
  headers: {
    'Content-Type': 'application/json',
  },
});

// Configure Request Interceptor to attach Bearer Tokens
API.interceptors.request.use(
  (config) => {
    // Check if there's a token stored in localStorage
    const token = localStorage.getItem('token');
    
    // If token exists, attach it to the request authorization header
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default API;

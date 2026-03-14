-- Create database if not exists
CREATE DATABASE IF NOT EXISTS pharmamap;
USE pharmamap;

-- 1. Users Table
-- Stores all platform users (Customers, Pharmacy Owners, Admins)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'PHARMACY', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Index for faster login lookups
    INDEX idx_email (email)
);

-- 2. Pharmacies Table
-- Stores information about each registered pharmacy
CREATE TABLE pharmacies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL UNIQUE, -- Ensures 1 User -> 1 Pharmacy relationship
    license_number VARCHAR(100) NOT NULL UNIQUE,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    pincode VARCHAR(20) NOT NULL,
    latitude DECIMAL(10, 8), -- Storing geographic coordinates for distance calculation
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key to link pharmacy to its owner
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes for location-based search and faster filtering
    INDEX idx_location (latitude, longitude),
    INDEX idx_city_pincode (city, pincode)
);

-- 3. Medicines Table
-- Master database of medicines
CREATE TABLE medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,          -- e.g., "Dolo 650 Tablet"
    brand_name VARCHAR(255),             -- e.g., "Dolo 650"
    salt_name VARCHAR(255),              -- e.g., "Paracetamol"
    strength VARCHAR(50),                -- e.g., "650mg"
    form VARCHAR(50),                    -- e.g., "Tablet"
    manufacturer VARCHAR(255),           -- e.g., "Micro Labs Ltd"
    prescription_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes for faster text/name searches
    INDEX idx_medicine_name (name),
    INDEX idx_salt_name (salt_name)
);

-- 4. Inventory Table
-- Tracks medicine stock per pharmacy
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign keys back to Pharmacy and Medicine
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE CASCADE,
    
    -- Ensure a pharmacy can only have one inventory record per medicine
    UNIQUE KEY unique_pharmacy_medicine (pharmacy_id, medicine_id),
    
    -- Composite index to quickly find if a specific medicine is IN STOCK (quantity > 0)
    INDEX idx_medicine_availability (medicine_id, quantity)
);

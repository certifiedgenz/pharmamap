import React, { useState, useEffect, useRef } from 'react';
import API from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Package, Plus, Edit2, Trash2, Search, X, Upload } from 'lucide-react';

const Dashboard = () => {
  const { user } = useAuth();
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [showModal, setShowModal] = useState(false);
  const [modalMode, setModalMode] = useState('add'); // 'add' or 'edit'
  const [currentStock, setCurrentStock] = useState({ id: '', medicineId: '', quantity: '', price: '' });
  
  // CSV Upload State
  const fileInputRef = useRef(null);
  const [uploadingCsv, setUploadingCsv] = useState(false);

  // Load Inventory
  const fetchInventory = async () => {
    try {
      setLoading(true);
      const response = await API.get('/inventory');
      setInventory(response.data.data || []);
    } catch (error) {
      console.error("Failed to load inventory:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  const handleOpenModal = (mode, stock = null) => {
    setModalMode(mode);
    if (stock) {
      setCurrentStock({ 
        id: stock.inventoryId, 
        medicineId: stock.medicineId, 
        quantity: stock.quantity, 
        price: stock.price 
      });
    } else {
      setCurrentStock({ id: '', medicineId: '', quantity: '', price: '' });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleSaveItem = async (e) => {
    e.preventDefault();
    try {
      if (modalMode === 'add') {
        const payload = {
          medicineId: parseInt(currentStock.medicineId),
          quantity: parseInt(currentStock.quantity),
          price: parseFloat(currentStock.price)
        };
        await API.post('/inventory', payload);
      } else {
        const payload = {
          medicineId: parseInt(currentStock.medicineId),
          quantity: parseInt(currentStock.quantity),
          price: parseFloat(currentStock.price)
        };
        await API.put(`/inventory/${currentStock.id}`, payload);
      }
      handleCloseModal();
      fetchInventory(); // Map will instantly reload natively
    } catch (error) {
      alert("Error saving inventory item. " + (error.response?.data?.message || ''));
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to permanently delete this medicine stock?")) {
      try {
        await API.delete(`/inventory/${id}`);
        fetchInventory(); // Reload
      } catch (error) {
        alert("Failed to delete stock.");
      }
    }
  };

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    // Reset input so the same file can be uploaded again if needed
    if (fileInputRef.current) {
        fileInputRef.current.value = "";
    }

    if (!file.name.endsWith('.csv')) {
        alert("Please select a valid .csv file format.");
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        setUploadingCsv(true);
        const response = await API.post('/inventory/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        
        const { processed, medicinesNotFound, formatErrors } = response.data.data;
        alert(`CSV Upload Complete!\n\nImported/Updated: ${processed}\nNot Found: ${medicinesNotFound}\nSkipped Data Errors: ${formatErrors}`);
        
        fetchInventory();
    } catch (error) {
        alert("CSV Server Upload Failed: " + (error.response?.data?.message || error.message));
    } finally {
        setUploadingCsv(false);
    }
  };

  return (
    <div className="container mt-5 pb-5">
      <div className="d-flex justify-content-between align-items-end border-bottom pb-4 mb-4">
        <div>
          <h2 className="fw-bold mb-1">Stock Controller</h2>
          <p className="text-secondary mb-0">Total Active Items: {inventory.length}</p>
        </div>
        <div className="d-flex gap-2">
            <input 
              type="file" 
              accept=".csv"
              ref={fileInputRef}
              style={{ display: 'none' }}
              onChange={handleFileUpload}
            />
            <button 
                className="btn btn-outline-primary d-flex align-items-center bg-white" 
                onClick={() => fileInputRef.current.click()}
                disabled={uploadingCsv}
            >
              {uploadingCsv ? (
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              ) : (
                  <Upload size={18} className="me-2" /> 
              )}
              {uploadingCsv ? 'Processing...' : 'Bulk CSV Sync'}
            </button>
            <button className="btn btn-primary d-flex align-items-center shadow-sm" onClick={() => handleOpenModal('add')}>
              <Plus size={18} className="me-2" /> Add Stock
            </button>
        </div>
      </div>

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status"></div>
          <div className="mt-3 text-secondary">Synchronizing Data...</div>
        </div>
      ) : (
        <div className="glass-panel overflow-hidden">
          <div className="table-responsive">
            <table className="table table-hover mb-0 align-middle">
              <thead className="table-light text-secondary small">
                <tr>
                  <th className="ps-4 fw-medium text-uppercase py-3">Medicine Info</th>
                  <th className="text-uppercase fw-medium py-3">Strength & Form</th>
                  <th className="text-uppercase text-end fw-medium py-3">Units in Stock</th>
                  <th className="text-uppercase text-end fw-medium py-3">Price (Unit)</th>
                  <th className="text-uppercase text-end pe-4 fw-medium py-3">Actions</th>
                </tr>
              </thead>
              <tbody className="border-top-0">
                {inventory.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="text-center py-5 text-muted">
                      <Package size={32} className="mb-3 opacity-50" />
                      <h5>Your inventory is empty</h5>
                      <p>Start tracking your assets by adding new stock manually.</p>
                    </td>
                  </tr>
                ) : (
                  inventory.map((item) => (
                    <tr key={item.inventoryId} className="bg-white">
                      <td className="ps-4 py-3">
                        <span className="fw-bold d-block text-dark">{item.medicineName}</span>
                        <small className="text-muted">{item.brandName}</small>
                      </td>
                      <td className="py-3">
                        <span className="badge bg-light text-dark border me-1">{item.strength}</span>
                        <span className="badge bg-light text-dark border">{item.form}</span>
                      </td>
                      <td className="py-3 text-end">
                        <strong className={item.quantity > 10 ? "text-success" : "text-danger"}>
                          {item.quantity}
                        </strong>
                      </td>
                      <td className="py-3 text-end fw-medium text-dark">
                        ${item.price.toFixed(2)}
                      </td>
                      <td className="py-3 text-end pe-4">
                        <button 
                          className="btn btn-sm btn-light border shadow-sm rounded p-2 me-2 text-primary"
                          onClick={() => handleOpenModal('edit', item)}
                          title="Update Item"
                        >
                          <Edit2 size={16} />
                        </button>
                        <button 
                          className="btn btn-sm btn-light border shadow-sm rounded p-2 text-danger"
                          onClick={() => handleDelete(item.inventoryId)}
                          title="Delete Item"
                        >
                          <Trash2 size={16} />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Manual Bootstrap Modal handling since we aren't using heavy dependencies */}
      {showModal && (
        <>
          <div className="modal-backdrop fade show shadow"></div>
          <div className="modal fade show d-block" tabIndex="-1">
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
                <div className="modal-header bg-light border-0 px-4 py-3">
                  <h5 className="modal-title fw-bold">
                    {modalMode === 'add' ? 'Add New Medicine Stock' : 'Update Inventory Quantities'}
                  </h5>
                  <button type="button" className="btn-close shadow-none" onClick={handleCloseModal}></button>
                </div>
                
                <form onSubmit={handleSaveItem}>
                  <div className="modal-body px-4 py-4">
                    
                    {modalMode === 'add' && (
                       <div className="mb-4">
                         <label className="form-label text-secondary small fw-medium">Medicine ID Registry</label>
                         <input 
                           type="number" 
                           className="form-control" 
                           required 
                           value={currentStock.medicineId}
                           onChange={e => setCurrentStock({...currentStock, medicineId: e.target.value})}
                           placeholder="Ex: 1"
                         />
                         <div className="form-text">System entity ID linking to the global medicine list.</div>
                       </div>
                    )}
                   
                    <div className="row">
                      <div className="col-12 col-md-6 mb-3">
                        <label className="form-label text-secondary small fw-medium">Units In Stock</label>
                        <div className="input-group">
                          <input 
                            type="number" 
                            className="form-control" 
                            required 
                            min="0"
                            value={currentStock.quantity}
                            onChange={e => setCurrentStock({...currentStock, quantity: e.target.value})}
                          />
                          <span className="input-group-text bg-white text-muted">units</span>
                        </div>
                      </div>
                      <div className="col-12 col-md-6 mb-3">
                        <label className="form-label text-secondary small fw-medium">Price Per Unit</label>
                        <div className="input-group">
                          <span className="input-group-text bg-white text-muted">$</span>
                          <input 
                            type="number" 
                            step="0.01"
                            className="form-control" 
                            required 
                            min="0"
                            value={currentStock.price}
                            onChange={e => setCurrentStock({...currentStock, price: e.target.value})}
                          />
                        </div>
                      </div>
                    </div>

                  </div>
                  <div className="modal-footer border-0 px-4 pb-4 px-3 pt-0 bg-white">
                    <button type="button" className="btn btn-light fw-medium px-4" onClick={handleCloseModal}>Cancel</button>
                    <button type="submit" className="btn btn-primary fw-bold px-4 shadow-sm">
                      {modalMode === 'add' ? 'Save Stock' : 'Apply Changes'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default Dashboard;

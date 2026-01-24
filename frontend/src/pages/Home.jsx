import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from 'oidc-react';
import { load } from "@cashfreepayments/cashfree-js";

const Home = () => {
    const auth = useAuth();
    const [products, setProducts] = useState([]);
    const [showOrderModal, setShowOrderModal] = useState(false);
    const [showProductModal, setShowProductModal] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [orderQty, setOrderQty] = useState(1);
    const [newProduct, setNewProduct] = useState({ name: '', price: '' });

    const headers = { Authorization: `Bearer ${auth.userData?.access_token}` };

    const fetchProducts = async () => {
        try {
            const res = await axios.get('/api/product', { headers });
            // const res = await axios.get('http://localhost:8087/api/product', { headers });
            setProducts(res.data);
        } catch (err) { console.error("Fetch products failed", err); }
    };

    useEffect(() => { fetchProducts(); }, []);

    const handlePlaceOrder = async (e) => {
        e.preventDefault();
        try {
            const {
                preferred_username,
                given_name,
                family_name,
                email,
                phone_number
            } = auth.userData.profile;

            const res = await axios.post('/api/order', {
            // const res = await axios.post('http://localhost:8088/api/order', {
                skuCode: selectedProduct.skuCode,
                price: selectedProduct.price * Number(orderQty),
                quantity: Number(orderQty),
                returnUrl: `${window.location.origin}/payment-result?order_id={order_id}`,
                userDetails: {
                    username: preferred_username,
                    firstName: given_name,
                    lastName: family_name,
                    email: email,
                    phoneNumber: phone_number
                }
            }, { headers });

            setShowOrderModal(false);
            await startCheckout(res?.data?.payment_session_id);
        } catch (err) {
            const errMsg = err?.response?.data || err.message || "Order failed";
            console.error("Place order failed", err);
            alert(errMsg);
        }
    };

    const startCheckout = async (sessionId) => {
        if(sessionId) {
            const cashfree = await load({ mode: "sandbox" });
            cashfree.checkout({
                paymentSessionId: sessionId,
            }).then(result => {
                if (result.error) console.error(result.error);
            });
        } else {
            alert("Invalid payment session. Cannot proceed to checkout.");
        }
    };

    const handleAddProduct = async (e) => {
        e.preventDefault();
        await axios.post('/api/product', newProduct, { headers });
        // await axios.post('http://localhost:8087/api/product', newProduct, { headers });
        setShowProductModal(false);
        fetchProducts();
    };

    return (
        <div>
            <h2>Product List</h2>
            <button className="btn btn-success mb-3" onClick={() => setShowProductModal(true)}>Add New Product</button>
            <table className="table table-striped table-bordered">
                <thead className="table-dark">
                <tr><th>Sku Code</th><th>Name</th><th>Description</th><th>Price</th><th>Action</th></tr>
                </thead>
                <tbody>
                {products.map(p => (
                    <tr key={p.skuCode}>
                        <td>{p.skuCode}</td>
                        <td>{p.name}</td>
                        <td>{p.description}</td>
                        <td>${p.price}</td>
                        <td>
                            <button className="btn btn-primary btn-sm" onClick={() => { setSelectedProduct(p); setShowOrderModal(true); }}>Order</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* Place Order Modal */}
            {showOrderModal && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <form onSubmit={handlePlaceOrder}>
                                <div className="modal-header"><h5>Order {selectedProduct?.name}</h5></div>
                                <div className="modal-body">
                                    <label>Quantity:</label>
                                    <input type="number" className="form-control" value={orderQty} onChange={(e) => setOrderQty(e.target.value)} required />
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowOrderModal(false)}>Cancel</button>
                                    <button type="submit" className="btn btn-primary">Submit Order</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}

            {/* Add Product Modal */}
            {showProductModal && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <form onSubmit={handleAddProduct}>
                                <div className="modal-header"><h5>Add New Product</h5></div>
                                <div className="modal-body">
                                    <input type="text" className="form-control mb-2" placeholder="Sku Code" onChange={(e) => setNewProduct({...newProduct, skuCode: e.target.value})} required />
                                    <input type="text" className="form-control mb-2" placeholder="Name" onChange={(e) => setNewProduct({...newProduct, name: e.target.value})} required />
                                    <textarea className="form-control mb-2" placeholder="Description" onChange={(e) => setNewProduct({...newProduct, description: e.target.value})} required />
                                    <input type="number" className="form-control" placeholder="Price" onChange={(e) => setNewProduct({...newProduct, price: e.target.value})} required />
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowProductModal(false)}>Cancel</button>
                                    <button type="submit" className="btn btn-success">Save Product</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Home;
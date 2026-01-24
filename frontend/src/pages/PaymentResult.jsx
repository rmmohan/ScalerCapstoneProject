import React, { useEffect, useState } from "react";
import axios from 'axios';
import { useLocation } from "react-router-dom";
import {useAuth} from "oidc-react";

function PaymentResult() {
    const auth = useAuth();
    const [status, setStatus] = useState(null);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState("");

    const query = new URLSearchParams(useLocation().search);
    const orderId = query.get("order_id");

    const headers = { Authorization: `Bearer ${auth.userData?.access_token}` };

    useEffect(() => {
        if (!orderId) {
            setMessage("No order ID found in redirect.");
            setLoading(false);
            return;
        }

        // Call your backend to verify order status
        async function fetchStatus() {
            try {
                const res = await axios.get(`/api/order/status/${orderId}`, { headers });
                const status = await res?.data?.cashfreeStatus || "UNKNOWN";
                setStatus(status);
            } catch (err) {
                setMessage("Network error when checking payment status.");
            } finally {
                setLoading(false);
            }
        }

        fetchStatus();
    }, [orderId]);

    if (loading) return <div>Checking payment status…</div>;

    return (
        <div>
            <h2>Payment Result</h2>
            {status ? (
                <>
                    <p>Order ID: {orderId}</p>
                    <p>Status: {status}</p>
                    {status === "PAID" ? (
                        <p>🎉 Your payment was successful!</p>
                    ) : (
                        <p>❌ Payment did not succeed.</p>
                    )}
                </>
            ) : (
                <p>{message}</p>
            )}
        </div>
    );
}

export default PaymentResult;

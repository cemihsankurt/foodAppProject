import React, { useState, useEffect } from 'react';
import apiClient from '../api.js';
import { Link } from 'react-router-dom';

function MyOrdersPage() {
    
    // 1. HAFIZA: Sipariş listesi, yüklenme durumu ve hata
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 2. EFEKT: Sayfa ilk yüklendiğinde SADECE 1 KEZ çalış
    useEffect(() => {
        const fetchMyOrders = async () => {
            try {
                // Backend'deki korumalı '/my-orders' endpoint'ini çağır
                // (apiClient buna 'Authorization' token'ını otomatik ekleyecek)
                const response = await apiClient.get('/orders/my-orders');
                
                setOrders(response.data); // Gelen sipariş listesini hafızaya al
                
            } catch (err) {
                console.error("Siparişler çekilirken hata:", err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchMyOrders();
    }, []); // Boş dizi '[]' -> Sadece 1 kez çalışır

    // 3. GÖRÜNÜM (Render)
    if (loading) return <div>Siparişleriniz Yükleniyor...</div>;
    if (error) return <div style={{ color: 'red' }}>Hata: {error}</div>;

    return (
        <div>
            <h2>Siparişlerim</h2>
            <div className="order-list">
                {orders.length > 0 ? (
                    orders.map(order => (
                        <div key={order.orderId} style={{ border: '1px solid black', margin: '10px', padding: '10px' }}>
                            <h4>Restoran: {order.restaurantName}</h4>
                            <p>Durum: <strong>{order.orderStatus}</strong></p>
                            <p>Tarih: {new Date(order.orderTime).toLocaleString('tr-TR')}</p>
                            <p>Toplam Tutar: {order.totalPrice} TL</p>
                            
                            {/* Buraya da tıklayınca sipariş detayına giden
                                bir link ekleyebiliriz (daha sonra) */}
                            <Link to={`/orders/${order.orderId}`}>Detayları Gör</Link>
                        </div>
                    ))
                ) : (
                    <p>Henüz hiç sipariş vermemişsiniz.</p>
                )}
            </div>
        </div>
    );
}

export default MyOrdersPage;
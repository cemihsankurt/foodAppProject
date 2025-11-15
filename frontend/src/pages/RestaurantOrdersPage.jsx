import React, { useState, useEffect } from 'react';
import apiClient from '../api.js';
import { Link } from 'react-router-dom';

function RestaurantOrdersPage() {
    
    // 1. HAFIZA: Sipariş listesi, yüklenme ve hata
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 2. EFEKT: Sayfa ilk yüklendiğinde SADECE 1 KEZ çalış
    useEffect(() => {
        const fetchRestaurantOrders = async () => {
            try {
                // Backend'deki yeni korumalı endpoint'i çağır
                const response = await apiClient.get('/restaurant-panel/orders');
                setOrders(response.data); // Gelen listeyi hafızaya al
                
            } catch (err) {
                console.error("Restoran siparişleri çekilirken hata:", err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchRestaurantOrders();
    }, []); // Boş dizi '[]' -> Sadece 1 kez çalışır

    // 3. GÖRÜNÜM (Render)
    if (loading) return <div>Gelen Siparişler Yükleniyor...</div>;
    if (error) return <div style={{ color: 'red' }}>Hata: {error}</div>;

    return (
        <div>
            <h2>Gelen Siparişler</h2>
            <div className="order-list">
                {orders.length > 0 ? (
                    orders.map(order => (
                        <div key={order.orderId} style={{ border: '1px solid black', margin: '10px', padding: '10px' }}>
                            <h4>Sipariş ID: #{order.orderId}</h4>
                            {/* DTO'da müşteri adı yok, onu eklememiz lazım
                                (Şimdilik boş bırakalım) */}
                            {/* <p>Müşteri: {order.customerName}</p> */}
                            <p>Durum: <strong>{order.orderStatus}</strong></p>
                            <p>Tarih: {new Date(order.orderTime).toLocaleString('tr-TR')}</p>
                            <p>Toplam Tutar: {order.totalPrice} TL</p>
                            
                            {/* Buraya tıklayınca sipariş detayına gidip
                                durumu (PENDING -> APPROVED) değiştireceğimiz
                                linki koyacağız */}
                            <Link to={`/restaurant-panel/orders/${order.orderId}`}>
                                Siparişi Yönet
                            </Link>
                        </div>
                    ))
                ) : (
                    <p>Henüz hiç sipariş almamışsınız.</p>
                )}
            </div>
        </div>
    );
}

export default RestaurantOrdersPage;
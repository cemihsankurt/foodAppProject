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

    const handleCancelOrder = async (orderId) => {
        // Kullanıcıya onayla
        if (!window.confirm("Bu siparişi iptal etmek istediğinize emin misiniz?")) {
            return;
        }

        try {
            // Backend'deki müşteriye özel 'cancel' endpoint'ini çağır
            const response = await apiClient.post(`/orders/${orderId}/cancel`);
            
            // Başarılı! Backend'den güncel sipariş (OrderDetailsResponseDto) döndü.
            // Şimdi ekrandaki listeyi (hafızayı) güncelleyelim.
            setOrders(currentOrders =>
                currentOrders.map(order => 
                    order.orderId === orderId ? 
                    { ...order, orderStatus: 'CANCELLED' } : // Sadece durumu "CANCELLED" yap
                    order
                )
            );
            alert("Sipariş başarıyla iptal edildi.");

        } catch (err) {
            // (örn: "Sipariş durumu 'PREPARING' olduğu için artık iptal edilemez.")
            console.error("Sipariş iptal edilirken hata:", err);
            alert("Hata: " + (err.response?.data?.message || err.message));
        }
    };

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
                            {/* ... (Tarih, Toplam Tutar) ... */}
                            
                            <Link to={`/orders/${order.orderId}`}>Detayları Gör</Link>
                            
                            {/* --- 4. YENİ KOŞULLU BUTON --- */}
                            {/* Sadece 'PENDING' ise İPTAL ET butonunu göster */}
                            {order.orderStatus === 'PENDING' && (
                                <button 
                                    onClick={() => handleCancelOrder(order.orderId)}
                                    style={{ background: 'red', color: 'white', marginLeft: '10px' }}
                                >
                                    İptal Et
                                </button>
                            )}
                            {/* --- BİTTİ --- */}
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
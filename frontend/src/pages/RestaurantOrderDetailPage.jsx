import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../api.js';

function RestaurantOrderDetailPage() {
    
    const { orderId } = useParams(); // URL'den (örn: /orders/3) '3'ü al
    const navigate = useNavigate();

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- 1. VERİ ÇEKME (useEffect) ---
    // Sayfa ilk yüklendiğinde SADECE 1 KEZ çalışır
    useEffect(() => {
        const fetchOrderDetails = async () => {
            try {
                // Backend'deki *ortak* (müşteri/restoran) sipariş detay endpoint'ini çağır
                const response = await apiClient.get(`/orders/${orderId}`);
                setOrder(response.data);
            } catch (err) {
                console.error("Sipariş detayı çekilirken hata:", err);
                if (err.response && err.response.status === 403) {
                    setError("Bu siparişi görüntüleme yetkiniz yok.");
                } else {
                    setError(err.response?.data?.message || err.message);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchOrderDetails();
    }, [orderId]); // 'orderId' değişirse bu efekti tekrar çalıştır

    // --- 2. EYLEM (Durumu Güncelle) ---
    const handleUpdateStatus = async (newStatus) => {
        try {
            // Backend'deki 'restaurant-panel' endpoint'ini (PUT) çağır
            const response = await apiClient.post(
                `/restaurant-panel/orders/${orderId}/status`,
                { newStatus: newStatus } // Örn: { "newStatus": "PREPARING" }
            );

            // Başarılı! Dönen güncel 'OrderDetailsResponseDto' ile
            // ekrandaki veriyi (frontend hafızasını) anında güncelle
            setOrder(response.data);
            alert(`Sipariş durumu "${newStatus}" olarak güncellendi!`);

        } catch (err) {
            console.error("Sipariş durumu güncellenirken hata:", err);
            alert("Hata: " + (err.response?.data?.message || err.message));
        }
    };

    // --- 3. GÖRÜNÜM (Render) ---
    if (loading) return <div>Sipariş Detayları Yükleniyor...</div>;
    if (error) return <div style={{ color: 'red' }}>Hata: {error}</div>;
    if (!order) return <div>Sipariş bulunamadı.</div>;

    // Her şey yolundaysa, detayı göster:
    return (
        <div>
            <h2>Sipariş Yönetimi: #{order.orderId}</h2>
            <p><strong>Müşteri:</strong> {order.customerName}</p>
            <p><strong>Teslimat Adresi:</strong> {order.deliveryAddress}</p>
            <p><strong>Tarih:</strong> {new Date(order.orderTime).toLocaleString('tr-TR')}</p>
            <p><strong>Toplam Tutar:</strong> {order.totalPrice} TL</p>
            <h3 style={{ color: 'blue' }}>MEVCUT DURUM: {order.orderStatus}</h3>

            {/* Sipariş Kalemleri */}
            <h4 style={{marginTop: '20px'}}></h4>
            {order.items && order.items.map((item, index) => (
                <div key={index} style={{ borderBottom: '1px solid #eee', padding: '5px' }}>
                    <p><strong>{item.productName}</strong> - {item.quantity} Adet</p>
                </div>
            ))}

            {/* --- 4. EYLEM BUTONLARI --- */}
            <div className="action-buttons" style={{ marginTop: '30px', display: 'flex', gap: '10px' }}>
                
                {/* Sadece PENDING (Onay Bekliyor) ise bu butonu göster */}
                {order.orderStatus === 'PENDING' && (
                    <button 
                        onClick={() => handleUpdateStatus('PREPARING')} 
                        style={{ background: 'green', color: 'white', padding: '10px' }}
                    >
                        Siparişi Onayla (Hazırlanıyor)
                    </button>
                )}
                
                {/* Sadece PREPARING (Hazırlanıyor) ise bu butonu göster */}
                {order.orderStatus === 'PREPARING' && (
                    <button 
                        onClick={() => handleUpdateStatus('DELIVERING')} 
                        style={{ background: 'blue', color: 'white', padding: '10px' }}
                    >
                        Yola Çıktı (Teslim Ediliyor)
                    </button>
                )}

                {/* Sadece DELIVERING (Yolda) ise bu butonu göster */}
                {order.orderStatus === 'DELIVERING' && (
                    <button 
                        onClick={() => handleUpdateStatus('COMPLETED')} 
                        style={{ background: 'gray', color: 'white', padding: '10px' }}
                    >
                        Teslim Edildi (Tamamla)
                    </button>
                )}

                {/* Sipariş PENDING veya PREPARING ise İPTAL ET butonu da görünsün */}
                {(order.orderStatus === 'PENDING' || order.orderStatus === 'PREPARING') && (
                    <button 
                        onClick={() => handleUpdateStatus('CANCELLED')} 
                        style={{ background: 'red', color: 'white', padding: '10px' }}
                    >
                        Siparişi İptal Et
                    </button>
                )}
                
                {/* Tamamlanmış veya İptal edilmiş siparişler için mesaj */}
                {(order.orderStatus === 'COMPLETED' || order.orderStatus === 'CANCELLED') && (
                    <p>Bu sipariş tamamlanmış veya iptal edilmiştir. Durum değiştirilemez.</p>
                )}
            </div>
        </div>
    );
}

export default RestaurantOrderDetailPage;
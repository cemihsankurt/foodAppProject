import React, { useState, useEffect } from 'react';
import apiClient from '../api.js';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function RestaurantOrdersPage() {
    
    // 1. HAFIZA: Sipariş listesi, yüklenme ve hata
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { user } = useAuth();

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
    }, []);
    
    // Boş dizi '[]' -> Sadece 1 kez çalışır

    useEffect(() => {
        
        // Eğer kullanıcı restoran değilse veya restaurantId'si yoksa bağlanma
        if (!user || !user.roles.includes('ROLE_RESTAURANT') || !user.restaurantId) {
            return;
        }

        // WebSocket bağlantısı için STOMP client'ı ayarla
        const sock = new SockJS('http://localhost:8080/ws'); 
        const stompClient = Stomp.over(sock);


        // Bağlantı başarılı olduğunda...
        stompClient.onConnect = (frame) => {
            console.log('WebSocket\'e bağlanıldı: ' + frame);
            
            // 5. KENDİ ÖZEL KANALIMIZA ABONE OL
            // (Backend'de 'messagingTemplate.convertAndSend' ile yolladığımız yer)
            const topic = `/topic/orders/restaurant/${user.restaurantId}`;
            
            stompClient.subscribe(topic, (message) => {
                // KANALDAN YENİ MESAJ GELDİĞİNDE:
                const newOrder = JSON.parse(message.body); // Gelen sipariş (JSON)
                console.log('Yeni sipariş alındı!', newOrder);
                
                // Listeyi güncelle: Yeni siparişi listenin en başına ekle
                setOrders((currentOrders) => [newOrder, ...currentOrders]);
            });
        };

        // Bağlantı hatası olursa...
        stompClient.onStompError = (frame) => {
            console.error('STOMP hatası: ' + frame.headers['message']);
        };

        // Bağlantıyı aktifleştir
        stompClient.activate();

        // 6. TEMİZLİK FONKSİYONU
        // Bu sayfadan ayrıldığımızda (component unmount), bağlantıyı kapat
        return () => {
            if (stompClient.connected) {
                stompClient.deactivate();
                console.log('WebSocket bağlantısı kapatıldı.');
            }
        };

    }, [user]);

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
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom'; // <-- Router'dan ID'yi okumak için
import apiClient from '../api.js';

function RestaurantMenuPage() {
    
    // 1. URL'den restoran ID'sini al
    // (main.jsx'te yolu '/restaurants/:restaurantId' yapacağız,
    //  bu 'useParams', o ':restaurantId' değişkenini yakalar)
    const { restaurantId } = useParams();
    const { name } = useParams();

    const [menu, setMenu] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 2. Sayfa ilk yüklendiğinde SADECE 1 KEZ çalış
    useEffect(() => {
        const fetchMenu = async () => {
            try {
                // 3. Backend'e o ID ile istek at
                const response = await apiClient.get(`/restaurants/${restaurantId}/menu`);
                setMenu(response.data);
            } catch (err) {
                setError(err.message);
                console.error("Menü çekilirken hata:", err);
            } finally {
                setLoading(false);
            }
        };

        fetchMenu();
    }, [restaurantId]); // 'restaurantId' değişirse bu efekti tekrar çalıştır

    // 4. Görünüm (Render)
    if (loading) return <div>Menü Yükleniyor...</div>;
    if (error) return <div style={{ color: 'red' }}>Hata: {error}</div>;

    return (
        <div>
            <h2>Restoran Menüsü (ID: {restaurantId}), (Name: {name})</h2>
            <div className="menu-list">
                {menu.length > 0 ? (
                    menu.map(product => (
                        <div key={product.id} style={{ border: '1px solid gray', margin: '5px', padding: '5px' }}>
                            <h4>{product.name}</h4>
                            <p>{product.description}</p>
                            <p>Fiyat: {product.price} TL</p>
                            {/* Buraya yakında "Sepete Ekle" butonu gelecek */}
                        </div>
                    ))
                ) : (
                    <p>Bu restoranın menüsünde henüz ürün bulunmamaktadır.</p>
                )}
            </div>
        </div>
    );
}

export default RestaurantMenuPage;
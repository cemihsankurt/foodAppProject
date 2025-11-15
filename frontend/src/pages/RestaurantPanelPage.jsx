import React, { useState, useEffect } from 'react';
import apiClient from '../api.js';

function RestaurantPanelPage() {
    
    // --- 1. HAFIZA (State) ---
    // Panel verisini (restoran adı, menü, durum) tutmak için
    const [panelData, setPanelData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Yeni ürün ekleme formu için ayrı hafıza
    const [newProductName, setNewProductName] = useState('');
    const [newProductPrice, setNewProductPrice] = useState('');
    const [newProductDescription, setNewProductDescription] = useState('');
    const [formError, setFormError] = useState(null);


    // --- 2. VERİ ÇEKME (useEffect) ---
    // Bu, sayfa ilk yüklendiğinde çalışır ve mevcut durumu (menü, dükkan durumu) çeker
    useEffect(() => {
        fetchPanelData();
    }, []);

    const fetchPanelData = async () => {
    try {
        setLoading(true);
        const response = await apiClient.get('/restaurant-panel/my-details');

        // Backend field ile frontend field eşleştirme
        setPanelData(response.data);

    } catch (err) {
        setError(err.message);
    } finally {
        setLoading(false);
    }
    };


    // --- 3. EYLEM: DÜKKANI AÇ/KAPAT (SENİN İSTEDİĞİN) ---
    const handleToggleAvailability = async () => {
    const newStatus = !panelData.available;

    try {
        await apiClient.post('/restaurant-panel/status', {
            available: newStatus
        });

        setPanelData(prevData => ({
            ...prevData,
            available: newStatus
        }));

    } catch (err) {
        console.error("Dükkan durumu güncellenirken hata:", err);
    }
};


    // --- 4. EYLEM: YENİ ÜRÜN EKLE (SENİN İSTEDİĞİN) ---
    const handleAddProduct = async (e) => {
        e.preventDefault(); // Formun sayfayı yenilemesini engelle
        setFormError(null);

        try {
            // Backend'e 'POST /api/restaurant-panel/menu/products' isteği at
            const response = await apiClient.post('/restaurant-panel/menu/products', {
                name: newProductName,
                price: newProductPrice,
                description: newProductDescription
            });

            // Başarılı! Backend'den dönen yeni ürünü (ProductDto) al
            const newProduct = response.data;

            // Frontend'in hafızasındaki menü listesini güncelle
            setPanelData(prevData => ({
                ...prevData,
                menu: [...prevData.menu, newProduct] // Listenin sonuna ekle
            }));

            // Formu temizle
            setNewProductName('');
            setNewProductPrice('');
            setNewProductDescription('');

        } catch (err) {
            console.error("Ürün eklerken hata:", err);
            setFormError(err.response?.data?.message || "Bir hata oluştu.");
        }
    };


    // --- 5. GÖRÜNÜM (Render) ---
    if (loading) return <div>Restoran Paneliniz Yükleniyor...</div>;
    if (error) return <div style={{ color: 'red' }}>Hata: {error}</div>;
    if (!panelData) return <div>Restoran bilgisi bulunamadı.</div>;

    // Veri geldiyse:
    return (
        <div>
            <h2>{panelData.restaurantName} - Yönetim Paneli</h2>
            
            {/* --- Dükkan Durumu (Status) --- */}
            <div style={{ padding: '10px', background: '#f4f4f4', marginBottom: '20px' }}>
                <p>Onay Durumu: <strong>{panelData.approvalStatus}</strong></p>
                <p>Dükkan Durumu: <strong>{panelData.available ? "AÇIK (Sipariş Alıyor)" : "KAPALI (Sipariş Almıyor)"}</strong></p>
                
                {panelData.approvalStatus === 'APPROVED' ? (
                    // Butonu 'handleToggleAvailability' fonksiyonuna bağladık
                    <button onClick={handleToggleAvailability}>
                        {panelData.available ? "Dükkanı KAPAT" : "Dükkanı AÇ"}
                    </button>
                ) : (
                    <p style={{color: 'red'}}>Restoranınız henüz admin tarafından onaylanmadı.</p>
                )}
            </div>

            {/* --- Menü Yönetimi --- */}
            <div>
                <h3>Menü Yönetimi</h3>
                
                {/* YENİ ÜRÜN EKLEME FORMU */}
                <form onSubmit={handleAddProduct} style={{border: '1px solid black', padding: '10px', marginBottom: '10px'}}>
                    <h4>Yeni Ürün Ekle</h4>
                    <div>
                        <label>Ürün Adı:</label>
                        <input type="text" value={newProductName} onChange={(e) => setNewProductName(e.target.value)} required />
                    </div>
                    <div>
                        <label>Fiyat (örn: 150.00):</label>
                        <input type="number" step="0.01" value={newProductPrice} onChange={(e) => setNewProductPrice(e.target.value)} required />
                    </div>
                    <div>
                        <label>Açıklama:</label>
                        <input type="text" value={newProductDescription} onChange={(e) => setNewProductDescription(e.target.value)} />
                    </div>
                    <button type="submit">Ürünü Ekle</button>
                    {formError && <p style={{ color: 'red' }}>{formError}</p>}
                </form>
                
                <h4>Mevcut Ürünleriniz:</h4>
                {panelData.menu.length === 0 && <p>Menünüzde hiç ürün yok.</p>}
                
                {panelData.menu.map(product => (
                    <div key={product.id} style={{ borderBottom: '1px solid #ccc', padding: '5px' }}>
                        <p><strong>{product.name}</strong> - {product.price} TL</p>
                        {/* (Buraya 'Sil' ve 'Güncelle' butonları gelecek) */}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default RestaurantPanelPage;
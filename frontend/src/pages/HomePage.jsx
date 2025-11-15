import React, { useState, useEffect } from 'react'; // React'in "Hafıza" (useState) ve "Efekt" (useEffect) kancalarını import et
import apiClient from '../api.js'; // Backend ile konuşan "telefonumuzu" (axios) import et
import { Link } from 'react-router-dom'; // Sayfalar arası geçiş için Link bileşenini import et

function HomePage() {

    // --- 1. HAFIZA (State) ---
    // Backend'den gelen restoran listesini saklamak için bir "kutu" oluştur.
    // Başlangıçta bu liste boştur: []
    const [restaurants, setRestaurants] = useState([]);
    
    // Yükleme durumunu saklamak için (Kullanıcıya "Yükleniyor..." göstermek için)
    const [loading, setLoading] = useState(true);
    
    // Hata durumunu saklamak için
    const [error, setError] = useState(null);

    // --- 2. EYLEM (useEffect) ---
    // Bu 'useEffect' bloğu, sayfa İLK AÇILDIĞINDA SADECE BİR KEZ çalışır.
    useEffect(() => {
        
        // Asenkron (async) bir fonksiyon tanımlayıp,
        // bu fonksiyonun içinde backend'den veriyi çekiyoruz.
        const fetchRestaurants = async () => {
            try {
                // Backend'imizin public (herkese açık) endpoint'ine GET isteği at
                const response = await apiClient.get('/restaurants');
                
                // Gelen veriyi (response.data) hafızaya (state) kaydet
                setRestaurants(response.data);
                
            } catch (err) {
                // Bir hata olursa (backend çalışmıyorsa vb.)
                setError(err.message);
                console.error("Restoranlar çekilirken hata oluştu:", err);
            } finally {
                // Her durumda (başarılı veya hatalı) yüklemeyi bitir
                setLoading(false);
            }
        };

        fetchRestaurants(); // Fonksiyonu çağır

    }, []); // Sonundaki '[]' -> "Bu efekti sadece 1 kez çalıştır" demektir.

    // --- 3. GÖRÜNÜM (Render) ---
    
    // Eğer hâlâ yükleniyorsa...
    if (loading) {
        return <div>Restoranlar Yükleniyor...</div>;
    }

    // Eğer bir hata oluştuysa...
    if (error) {
        return <div style={{ color: 'red' }}>Hata: {error}</div>;
    }

    // Yükleme bittiyse ve hata yoksa, listeyi göster:
    return (
        <div>
            <h2>Siparişe Açık Restoranlar</h2>
            <div className="restaurant-list">
                {restaurants.length > 0 ? (
                    restaurants.map(restaurant => (
                        
                        // --- GÜNCELLEME BURADA ---
                        // Artık 'div' değil, tıklanabilir bir 'Link'
                        <Link 
                            key={restaurant.id} 
                            to={`/restaurants/${restaurant.id}`} // Tıklayınca bu adrese git
                            state={{restaurantName: restaurant.name}}
                            style={{ textDecoration: 'none', color: 'black' }} // (Çirkin link alt çizgisini kaldır)
                        >
                            <div style={{ border: '1px solid black', margin: '10px', padding: '10px' }}>
                                <h3>{restaurant.name}</h3>
                                <p>{restaurant.description}</p>
                            </div>
                        </Link>
                        // --- GÜNCELLEME BİTTİ ---
                        
                    ))
                ) : (
                    <p>Şu anda açık restoran bulunmamaktadır.</p>
                )}
            </div>
        </div>
    );
}

export default HomePage;
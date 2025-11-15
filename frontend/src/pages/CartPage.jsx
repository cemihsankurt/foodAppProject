import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext'; // Global hafızamızı import et
import { Link, useNavigate } from 'react-router-dom'; // Yönlendirme için
import apiClient from '../api.js'; // "Siparişi Tamamla" için

function CartPage() {
    
    // 1. Global hafızadan 'cart' (sepet) bilgisini çek
    const { cart, setCart } = useAuth(); // (setCart'ı da al, silme işlemi için lazım)
    const navigate = useNavigate();

    const [addresses, setAddresses] = useState([]); // Adres listesini tut
    const [selectedAddressId, setSelectedAddressId] = useState(''); // Seçilen adresi tut
    const [error, setError] = useState(null);
    const [loadingAddresses, setLoadingAddresses] = useState(true);

    useEffect(() => {
        const fetchAddresses = async () => {
            try {
                const response = await apiClient.get('/customer/addresses');
                setAddresses(response.data);
            } catch (err) {
                console.error("Adresler çekilirken hata:", err);
                setError("Adresleriniz yüklenirken bir hata oluştu.");
            } finally {
                setLoadingAddresses(false);
            }
        };
        
        fetchAddresses();
    }, []);

    console.log(addresses);

    const handleCreateOrder = async () => {
        
        // Güvenlik kontrolü: Adres seçilmiş mi?
        if (!selectedAddressId) {
            alert("Lütfen bir teslimat adresi seçin.");
            return;
        }
        
        try {
            // Backend'in 'createOrderFromCart' endpoint'ini çağır
            // Body olarak seçilen 'addressId'yi yolla
            console.log("Gönderilen body:", { addressId: selectedAddressId });
            const response = await apiClient.post('/orders/create-from-cart', {
                addressId: selectedAddressId
            });

            

            // BAŞARILI!
            console.log("Sipariş oluşturuldu:", response.data);
            alert("Siparişiniz başarıyla alındı!");

            // Sepeti temizle (global hafızada)
            setCart(null); 
            
            // Kullanıcıyı "Siparişlerim" sayfasına yönlendir (yakında yapacağız)
            // navigate('/my-orders');
            navigate('/'); // Şimdilik ana sayfaya atalım

        } catch (err) {
            console.error("Sipariş oluşturulurken hata:", err);
            // (Belki token süresi dolmuştur?)
            if (err.response && err.response.status === 401) {
                logout(); // Otomatik çıkış yaptır
                navigate('/login');
            } else {
                alert("Sipariş oluşturulurken bir hata oluştu: " + (err.response?.data?.message || err.message));
            }
        }
    };

    const handleRemoveItem = async (productId) => {
        
        // (Kullanıcıya "Emin misin?" diye sorabiliriz ama şimdilik direkt silelim)
        console.log("Siliniyor:", productId); // Hata ayıklama için

        try {
            // Backend'deki 'removeFromCart' endpoint'ini (DELETE) çağır
            const response = await apiClient.delete(`/cart/remove/${productId}`);
            
            // Backend, sepetin son halini (güncel CartDto) döndürecek.
            // Bu yeni DTO'yu alıp global hafızayı güncelle.
            setCart(response.data); 
            console.log("Ürün silindi, sepet güncellendi:", response.data);

        } catch (err) {
            console.error("Sepetten silerken hata:", err);
            alert("Ürün sepetten silinirken bir hata oluştu: " + (err.response?.data?.message || err.message));
        }
    };

    // (Buraya 'handleCreateOrder' (Siparişi Tamamla) fonksiyonu gelecek)

    // 2. GÖRÜNÜM (Render)

    // Sepet yüklenmemişse veya boşsa
    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div>
                <h2>Sepetiniz</h2>
                <p>Sepetiniz şu anda boş.</p>
                <Link to="/">Restoranlara Göz At</Link>
            </div>
        );
    }

    // Sepet DOLUYSA:
    return (
        <div>
            <h2>Sepetiniz</h2>
            
            {/* Sepetteki Ürünleri Listele */}
            <div className="cart-items-list">
                {cart.items.map(item => (
                    <div key={item.productId} style={{ borderBottom: '1px solid #ccc', padding: '10px' }}>
                        <h4>{item.productName}</h4>
                        <p>Adet: {item.quantity}</p>
                        <p>Birim Fiyat: {item.unitPrice} TL</p>
                        <p>Satır Toplamı: {item.lineTotalPrice} TL</p>
                        {/* (Buraya "Adedi Artır/Azalt" ve "Sil" butonları gelecek) */}
                        {/* --- 4. YENİ BUTON: SİL --- */}
                        <button 
                            onClick={() => handleRemoveItem(item.productId)}
                            style={{ color: 'red', background: 'none', border: '1px solid red', cursor: 'pointer' }}
                        >
                            Sil
                        </button>
                        {/* --- BİTTİ --- */}
                    </div>
                ))}
            </div>

            {/* Sepet Toplamı */}
            <div className="cart-summary" style={{ marginTop: '20px', borderTop: '2.px solid black' }}>
                <h3>Toplam Ürün: {cart.totalItemCount}</h3>
                <h2>Genel Toplam: {cart.cartTotal} TL</h2>
            </div>
            
            {/* Adres Seçimi ve Siparişi Tamamlama */}
            <div className="checkout-section" style={{ marginTop: '30px' }}>
                
                <h3>Teslimat Adresi Seçin</h3>
                
                {loadingAddresses && <p>Adresler yükleniyor...</p>}
                {error && <p style={{ color: 'red' }}>{error}</p>}
                
                {!loadingAddresses && addresses.length === 0 && (
                    <div>
                        <p style={{ color: 'red' }}>Kayıtlı adresiniz bulunmamaktadır.</p>
                        <Link to="/my-addresses">Lütfen önce bir adres ekleyin.</Link>
                    </div>
                )}

                {addresses.length > 0 && (
                    <>
                        {/* Adres Seçim Menüsü */}
                        <select 
                        value={selectedAddressId} 
                        onChange={(e) => setSelectedAddressId(e.target.value)}
                        style={{ width: '100%', padding: '10px' }}
                    >
                        <option value="">-- Lütfen bir adres seçin --</option>
                        
                        {/* * HATA BURADAYDI. 
                          * 'value' attribute'u ID'yi (rakamı) içermeli.
                          * Etiketlerin ARASI ise metni içermeli.
                        */}
                        {addresses.map(address => (
                            <option key={address.id} value={address.id}> 
                                {address.addressTitle} ({address.fullAddress})
                            </option>
                        ))}
                    </select>
                        
                        {/* Sipariş Butonu (Kodu aynı) */}
                        <button 
                            onClick={handleCreateOrder} 
                            style={{ padding: '10px 20px', fontSize: '1.2em', marginTop: '20px' }}
                            // Artık 'selectedAddressId' boş string ('') olmadığı an
                            // bu buton aktifleşecek.
                            disabled={!selectedAddressId}
                        >
                            Siparişi Tamamla
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}

export default CartPage;
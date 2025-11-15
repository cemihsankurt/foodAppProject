import React, { useState } from 'react';
import apiClient from '../api.js'; // Backend ile konuşan 'telefonumuz'
import { useNavigate } from 'react-router-dom'; // Yönlendirme için

function RegisterRestaurantPage() {
    
    // --- 1. HAFIZA (State) ---
    // Backend'deki 'RegisterRestaurantRequestDto'nun beklediği alanlar
    // (Restorana özel alanları da ekliyoruz)
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [restaurantName, setRestaurantName] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    // (Backend DTO'nda 'taxNumber' gibi başka zorunlu alanlar varsa
    //  onları da buraya 'useState' olarak eklemen gerekir)
    
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    
    const navigate = useNavigate(); // Yönlendirici

    // --- 2. EYLEM (Form Gönderme) ---
    const handleSubmit = async (e) => {
        e.preventDefault(); // Sayfanın yenilenmesini engelle
        setError(null); 
        setSuccessMessage(null);

        try {
            // Backend'in 'register-restaurant' endpoint'ine POST isteği at
            const response = await apiClient.post('/auth/register-restaurant', {
                email: email,
                password: password,
                restaurantName: restaurantName,
                phoneNumber: phoneNumber
                // (diğer DTO alanları)
            });

            // --- BAŞARILI! ---
            setSuccessMessage(response.data.message); // "Kayıt başarılı! Lütfen e-postanızı kontrol edin."
            
            // Kullanıcıyı 3 saniye sonra login sayfasına yönlendir
            setTimeout(() => {
                navigate('/login');
            }, 3000);

        } catch (err) {
            // --- HATA! ---
            // (Örn: "Bu e-posta zaten kullanılıyor")
            console.error("Restoran kaydı olurken hata:", err);
            setError(err.response?.data?.message || 'Bir hata oluştu.');
        }
    };

    // --- 3. GÖRÜNÜM (Render) ---
    return (
        <div>
            <h2>Restoran Olarak Kayıt Ol</h2>
            
            {/* Henüz bir form gönderilmediyse (başarı mesajı yoksa) formu göster */}
            {!successMessage ? (
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Restoran Adı:</label>
                        <input 
                            type="text" 
                            value={restaurantName} 
                            onChange={(e) => setRestaurantName(e.target.value)} 
                            required 
                        />
                    </div>
                    <div>
                        <label>Restoran E-posta (Giriş için):</label>
                        <input 
                            type="email" 
                            value={email} 
                            onChange={(e) => setEmail(e.target.value)} 
                            required 
                        />
                    </div>
                    <div>
                        <label>Şifre:</label>
                        <input 
                            type="password" 
                            value={password} 
                            onChange={(e) => setPassword(e.target.value)} 
                            required 
                        />
                    </div>
                     <div>
                        <label>Restoran Telefon Numarası:</label>
                        <input 
                            type="tel" 
                            value={phoneNumber} 
                            onChange={(e) => setPhoneNumber(e.target.value)} 
                            required 
                        />
                    </div>
                    {/* (Backend DTO'n 'taxNumber' vb. istiyorsa, o input'ları da buraya ekle) */}
                    <button type="submit">Restoran Kaydını Tamamla</button>
                    
                    {error && <p style={{ color: 'red' }}>Hata: {error}</p>}
                </form>
            ) : (
                // Kayıt başarılıysa, formu sakla ve başarı mesajını göster
                <div style={{ color: 'green', fontSize: '1.2em', padding: '20px' }}>
                    <p>{successMessage}</p>
                    <p>Onay ve doğrulama için e-postanızı kontrol edin.</p>
                    <p>Giriş sayfasına yönlendiriliyorsunuz...</p>
                </div>
            )}
        </div>
    );
}

export default RegisterRestaurantPage;
import React, { useState } from 'react';
import apiClient from '../api.js'; // Backend ile konuşan 'telefonumuz'
import { useNavigate } from 'react-router-dom'; // Yönlendirme için

function RegisterCustomerPage() {
    
    // --- 1. HAFIZA (State) ---
    // Backend'deki 'RegisterCustomerRequestDto'nun beklediği tüm alanlar için
    // hafıza kutuları oluşturalım.
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    
    // Hata ve başarı mesajlarını göstermek için
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    
    const navigate = useNavigate(); // Yönlendirici

    // --- 2. EYLEM (Form Gönderme) ---
    const handleSubmit = async (e) => {
        e.preventDefault(); // Sayfanın yenilenmesini engelle
        setError(null); // Eski hataları temizle
        setSuccessMessage(null);

        try {
            // Backend'in 'register-customer' endpoint'ine POST isteği at
            const response = await apiClient.post('/auth/register-customer', {
                // DTO'yu hafızadaki verilerle doldur
                firstName: firstName,
                lastName: lastName,
                email: email,
                password: password,
                phoneNumber: phoneNumber
            });

            // --- BAŞARILI! ---
            // Backend'den 200 OK ve mesaj geldi
            setSuccessMessage(response.data.message); // "Kayıt başarılı! Lütfen e-postanızı kontrol edin."
            
            // Kullanıcıyı 3 saniye sonra login sayfasına yönlendir
            setTimeout(() => {
                navigate('/login');
            }, 3000);

        } catch (err) {
            // --- HATA! ---
            // (Örn: "Bu e-posta zaten kullanılıyor" - 400 Bad Request)
            console.error("Kayıt olurken hata:", err);
            setError(err.response?.data?.message || 'Bir hata oluştu.');
        }
    };

    // --- 3. GÖRÜNÜM (Render) ---
    return (
        <div>
            <h2>Müşteri Olarak Kayıt Ol</h2>
            
            {/* Henüz bir form gönderilmediyse (başarı mesajı yoksa) formu göster */}
            {!successMessage ? (
                <form onSubmit={handleSubmit}>
                    <div>
                        <label>Ad:</label>
                        <input 
                            type="text" 
                            value={firstName} 
                            onChange={(e) => setFirstName(e.target.value)} 
                            required 
                        />
                    </div>
                    <div>
                        <label>Soyad:</label>
                        <input 
                            type="text" 
                            value={lastName} 
                            onChange={(e) => setLastName(e.target.value)} 
                            required 
                        />
                    </div>
                    <div>
                        <label>E-posta:</label>
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
                        <label>Telefon Numarası:</label>
                        <input 
                            type="tel" 
                            value={phoneNumber} 
                            onChange={(e) => setPhoneNumber(e.target.value)} 
                            required 
                        />
                    </div>
                    <button type="submit">Kayıt Ol</button>
                    
                    {/* Hata varsa (örn: email zaten var) göster */}
                    {error && <p style={{ color: 'red' }}>Hata: {error}</p>}
                </form>
            ) : (
                // Kayıt başarılıysa, formu sakla ve başarı mesajını göster
                <div style={{ color: 'green', fontSize: '1.2em', padding: '20px' }}>
                    <p>{successMessage}</p>
                    <p>Giriş sayfasına yönlendiriliyorsunuz...</p>
                </div>
            )}
        </div>
    );
}

export default RegisterCustomerPage;
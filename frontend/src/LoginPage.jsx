import React, { useState } from 'react'; // React'in "hafızasını" (useState) import et
import axios from 'axios'; // 1. Adımda kurduğumuz "telefonu" import et

// Bu bir React Bileşenidir (Component)
function LoginPage() {

    // --- 1. HAFIZA (State) ---
    // React'e, kullanıcının yazdığı e-posta ve şifreyi "hatırlamasını" söyler.
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null); // Hata mesajlarını tutmak için

    // --- 2. EYLEM (Form Gönderme) ---
    // "Giriş Yap" butonuna basıldığında bu fonksiyon çalışır.
    const handleLogin = async (e) => {
        // e.preventDefault(), formun sayfayı yenilemesini engeller. Bu React için şarttır.
        e.preventDefault(); 
        setError(null); // Eski hataları temizle

        try {
            // --- 3. BACKEND BAĞLANTISI ---
            // 'axios' ile backend'imizin 'authenticate' endpoint'ine POST isteği at.
            const response = await apiClient.post( 
                '/auth/authenticate', 
                {
                    email: email,
                    password: password
                }
            );

            // 4. BAŞARI!
            // Tarayıcının Konsoluna (F12) gelen token'ı yazdır
            const token = response.data.token;
            console.log('GİRİŞ BAŞARILI! Token:', token);

            localStorage.setItem('authToken', token);

            //ileride buraya 'react-router-dom' ile yönlendirme gelecek)
            alert('Giriş başarılı! Ana sayfaya yönlendiriliyorsunuz...');
            // Şimdilik sayfayı manuel olarak yenileyerek "giriş yapıldığını" varsayalım.
            window.location.reload();
            
            

        } catch (err) {
            // 5. HATA!
            // Eğer 403 (Yanlış Şifre) veya 400 (Doğrulanmamış) hatası alırsak...
            console.error('GİRİŞ HATASI!', err.response.data);
            
            // GlobalExceptionHandler'dan gelen mesajı ekrana yazdır
            setError(err.response.data.message || 'Bir hata oluştu.');
        }
    };

    // --- 4. GÖRÜNÜM (HTML/JSX) ---
    // Ekranda ne görüneceği
    return (
        <div>
            <h2>Giriş Yap</h2>
            {/* 'onSubmit' ile bu formu 'handleLogin' fonksiyonuna bağlarız */}
            <form onSubmit={handleLogin}>
                <div>
                    <label>E-posta:</label>
                    <input 
                        type="email" 
                        value={email} // Gördüğü değeri "hafızadan" (state) alır
                        onChange={(e) => setEmail(e.target.value)} // Klavyeden her tuşa basıldığında "hafızayı" günceller
                    />
                </div>
                <div>
                    <label>Şifre:</label>
                    <input 
                        type="password" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <button type="submit">Giriş Yap</button>
            </form>
            
            {/* Hata varsa, onu kırmızı renkte göster (Basit hata yönetimi) */}
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
}

export default LoginPage;
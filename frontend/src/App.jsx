import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom'; // <-- Link'i IMPORT 
import { useAuth } from './context/AuthContext.jsx';

function App() {

  const { isAuthenticated, logout, cart } = useAuth(); // <-- Hafızadan durumu ve 'logout'u al
  const navigate = useNavigate();

  // Çıkış yap butonuna basılınca...
  const handleLogout = () => {
    logout(); // Hafızayı temizle
    navigate('/login'); // Login sayfasına yönlendir
  };

  return (
    <div>
      <nav style={{ padding: '10px', background: '#eee', display: 'flex', gap: '20px' }}>
        <h1>Yemek Uygulaması</h1>
        
        <Link to="/">Ana Sayfa</Link>

        {/* --- MANTIK GÜNCELLEMESİ BURADA --- */}
        {isAuthenticated ? (
          <>
            <Link to="/my-orders">Siparişlerim</Link>
            
            {/* YENİ SEPET LİNKİ */}
            <Link to="/cart">
              Sepetim 
              {/* Eğer sepet yüklendiyse ve içinde ürün varsa sayısını göster */}
              {cart && cart.totalItemCount > 0 && (
                <span style={{ background: 'red', color: 'white', borderRadius: '50%', padding: '2px 6px', marginLeft: '5px' }}>
                  {cart.totalItemCount}
                </span>
              )}
            </Link>
            
            <button onClick={handleLogout}>Çıkış Yap</button>
          </>
        ) : (
          <>
            {/* (Giriş yapmamışsa menüsü aynı) */}
            <Link to="/login">Giriş Yap</Link>
            <Link to="/register-customer">Müşteri Kayıt</Link>
            <Link to="/register-restaurant">Restoran Kayıt</Link>
            {/* ... */}
          </>
        )}
      </nav>

      <hr />

      <main style={{ padding: '20px' }}>
        {/*
          *
          * 2. KONTROL (KATİL MUHTEMELEN BURADA):
          * Bu <Outlet /> satırı burada yazıyor mu?
          * 'Outlet', router'a "Ana Sayfa'yı veya Login'i buraya yerleştir" der.
          * Bu satır eksikse, sayfan bembeyaz görünür.
          *
        */}
        <Outlet /> 
      </main>
    </div>
  );
}

export default App;
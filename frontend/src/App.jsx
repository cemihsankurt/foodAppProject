import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom'; // <-- Link'i IMPORT 
import { useAuth } from './context/AuthContext.jsx';

function App() {

  const { isAuthenticated, logout } = useAuth(); // <-- Hafızadan durumu ve 'logout'u al
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

        {/* --- İŞTE MANTIK BURADA --- */}
        {isAuthenticated ? (
          // Eğer giriş yapmışsa (token varsa):
          <>
            <Link to="/my-orders">Siparişlerim</Link> {/* (Bu sayfayı sonra yapacağız) */}
            <button onClick={handleLogout}>Çıkış Yap</button>
          </>
        ) : (
          // Eğer giriş yapmamışsa (token yoksa):
          <>
            <Link to="/login">Giriş Yap</Link>
            <Link to="/register-customer">Müşteri Kayıt</Link>
            <Link to="/register-restaurant">Restoran Kayıt</Link>
          </>
        )}
        {/* --- MANTIK BİTTİ --- */}
      </nav>

      <hr />

      <main style={{ padding: '20px' }}>
        <Outlet />
      </main>
    </div>
  );
}

export default App;
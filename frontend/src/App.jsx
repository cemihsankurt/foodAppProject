import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom'; // <-- Link'i IMPORT 
import { useAuth } from './context/AuthContext.jsx';

function App() {

  const { user, logout, cart } = useAuth(); // <-- Hafızadan durumu ve 'logout'u al
  const navigate = useNavigate();

  // Çıkış yap butonuna basılınca...
  const handleLogout = () => {
    logout(); // Hafızayı temizle
    navigate('/login'); // Login sayfasına yönlendir
  };

  const isCustomer = user && user.roles.includes('ROLE_CUSTOMER');
  const isRestaurant = user && user.roles.includes('ROLE_RESTAURANT');
  const isAdmin = user && user.roles.includes('ROLE_ADMIN');

  return (
    <div>
      <nav style={{ padding: '10px', background: '#eee', display: 'flex', gap: '20px' }}>
        <h1>Yemek Uygulaması</h1>
        <Link to="/">Ana Sayfa</Link>

        {/* --- YENİ ROL BAZLI MANTIK --- */}
        
        {isCustomer && (
          // EĞER MÜŞTERİ GİRMİŞSE:
          <>
            <Link to="/my-orders">Siparişlerim</Link> 
            <Link to="/cart">
              Sepetim 
              {cart && cart.totalItemCount > 0 && (
                <span>({cart.totalItemCount})</span>
              )}
            </Link>
            <Link to="/my-addresses">Adreslerim</Link>
            <button onClick={handleLogout}>Çıkış Yap</button>
          </>
        )}

        {isRestaurant && (
          // EĞER RESTORAN GİRMİŞSE:
          <>
            <Link to="/restaurant-panel">Restoran Panelim</Link> 
            <Link to="/restaurant-orders">Gelen Siparişler</Link>
            <button onClick={handleLogout}>Çıkış Yap</button>
          </>
        )}
        
        {isAdmin && (
          // EĞER ADMİN GİRMİŞSE:
          <>
            <Link to="/admin">Admin Paneli</Link> {/* (Bu sayfayı sonra yapacağız) */}
            <button onClick={handleLogout}>Çıkış Yap</button>
          </>
        )}

        {!user && (
          // EĞER KİMSE GİRMEMİŞSE:
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
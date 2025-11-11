import React from 'react';
import LoginPage from './LoginPage';

// Bu, bizim ana "uygulama" bileşenimiz.
// Tüm diğer sayfalarımız (Login, Ana Sayfa) bunun içine gelecek.
function App() {
  return (
    <div>
      <h1>Yemek Uygulaması</h1>
      <hr />
      <LoginPage /> {/* <-- LOGIN SAYFASINI BURADA ÇAĞIR */}
    </div>
  );
}

export default App;
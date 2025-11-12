import React, { createContext, useState, useContext } from 'react';

// 1. "Global Hafıza"nın (Context) kendisini oluştur
const AuthContext = createContext(null);

/**
 * Bu, 'main.jsx'te tüm uygulamamızı saracak olan 'Sağlayıcı'dır (Provider).
 * Hafızayı bu bileşen tutar.
 */
export function AuthProvider({ children }) {
    
    // 2. Hafızanın ilk durumu: localStorage'daki token'ı oku
    const [token, setToken] = useState(localStorage.getItem('authToken'));

    // (Gelecekte buraya 'user' nesnesini de ekleyeceğiz)

    // 3. Hafızayı güncelleyecek fonksiyonlar
    
    // 'login' fonksiyonu (LoginPage'den çağrılacak)
    const login = (newToken) => {
        localStorage.setItem('authToken', newToken); // Hafızaya kaydet
        setToken(newToken); // React'in hafızasını güncelle (ekran yenilensin)
    };

    // 'logout' fonksiyonu (Logout butonu için)
    const logout = () => {
        localStorage.removeItem('authToken'); // Hafızadan sil
        setToken(null); // React'in hafızasını güncelle
    };

    // 4. Bu 'değerleri' (token, login, logout) alt bileşenlere 'sağla'
    const value = {
        token,
        login,
        logout,
        isAuthenticated: !!token // (token varsa true, yoksa false döner)
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

/**
 * Bu, 'App.jsx' veya 'HomePage.jsx' gibi bileşenlerin
 * o hafızaya kolayca erişmesini sağlayan kısa bir yoldur.
 */
export function useAuth() {
    return useContext(AuthContext);
}
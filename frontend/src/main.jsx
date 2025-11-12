import { AuthProvider } from './context/AuthContext.jsx';
import React from 'react';
import ReactDOM from 'react-dom/client';


import {
  createBrowserRouter, // Tarayıcı router'ını oluşturan fonksiyon
  RouterProvider,     // O router'ı uygulamaya sağlayan bileşen
} from 'react-router-dom';

import App from './App.jsx'; // Bizim ana "layout" (çerçeve) dosyamız
import HomePage from './pages/HomePage.jsx'; // Ana sayfamız
import LoginPage from './pages/LoginPage.jsx'; // Login sayfamız
import ProtectedRoute from './auth/ProtectedRoute.jsx';
import RegisterCustomerPage from './pages/RegisterCustomerPage.jsx';
import RegisterRestaurantPage from './pages/RegisterRestaurantPage.jsx';
import RestaurantMenuPage from './pages/RestaurantMenuPage.jsx';
// (CSS importları da burada kalabilir, örn: import './index.css')

// --- YOL HARİTASI ---
const router = createBrowserRouter([
  {
    path: '/',
    element: <App />, // Ana çerçeve
    children: [ 
      {
        path: '/', 
        element: <HomePage />, // <-- ARTIK KORUMALI DEĞİL. HERKESE AÇIK!
      },
      {
        path: '/login',
        element: <LoginPage />,
      },
      {
        path: '/register-customer',
        element: <RegisterCustomerPage />,
      },
      {
        path: '/register-restaurant',
        element: <RegisterRestaurantPage />,
      },
      {
        path: '/restaurants/:restaurantId',
        element: <RestaurantMenuPage />,
      }
    ],
  },
]);
// --- YOL HARİTASI BİTTİ ---

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* Artık <App />'ı değil, 'router'ı render ediyoruz */}
    <AuthProvider>
      <RouterProvider router={router} /> 
    </AuthProvider>
  </React.StrictMode>
);
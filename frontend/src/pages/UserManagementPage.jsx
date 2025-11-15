import React, { useState, useEffect } from 'react';
import apiClient from '../api.js';
import { useAuth } from '../context/AuthContext.jsx'; // Admin'in kendi ID'si için

function UserManagementPage() {
    
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { user } = useAuth(); // Giriş yapmış olan Admin'in bilgileri

    // 1. Sayfa yüklenince tüm kullanıcıları çek
    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await apiClient.get('/admin/users'); // Yeni endpoint
            setUsers(response.data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    // 2. Banlama / Banı Açma Fonksiyonu
    const handleToggleBan = async (userIdToBan) => {
        // Adminin kendini banlamasını engelle
        if (userIdToBan === user.id) {
            alert("Kendinizi banlayamazsınız.");
            return;
        }

        try {
            // Backend'e isteği at
            const response = await apiClient.post(`/admin/users/${userIdToBan}/ban-status`);
            alert(response.data); // "Kullanıcı banlandı" mesajını göster
            
            // Ekrandaki listeyi anında güncelle
            setUsers(currentUsers =>
                currentUsers.map(u => {
                    // Eğer ID'si, az önce banladığımız/banını açtığımız ID ile eşleşiyorsa...
                    if (u.id === userIdToBan) {
                        // ...o kullanıcının 'banned' durumunu tersine çevir ('true' ise 'false' yap)
                        return { ...u, banned: !u.banned };
                    }
                    // Eşleşmiyorsa, kullanıcıya dokunma, aynen geri döndür
                    return u;
                })
            );
        } catch (err) {
            alert("İşlem başarısız: " + (err.response?.data?.message || err.message));
        }
    };

    if (loading) return <div>Kullanıcılar yükleniyor...</div>;
    if (error) return <div style={{ color: 'red' }}>Hata: {error}</div>;

    // 3. Tabloyu Ekrana Çiz
    return (
        <div>
            <h2>Kullanıcı Yönetimi</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{ background: '#eee' }}>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>ID</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>Email</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>Rol</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>Durum</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>Eylem</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(u => (
                        <tr key={u.id}>
                            <td style={{ padding: '8px', border: '1px solid #ddd' }}>{u.id}</td>
                            <td style={{ padding: '8px', border: '1px solid #ddd' }}>{u.email}</td>
                            <td style={{ padding: '8px', border: '1px solid #ddd' }}>{u.role}</td>
                            <td style={{ padding: '8px', border: '1px solid #ddd', color: u.banned ? 'red' : 'green' }}>
                                {u.banned ? "Banlı" : (u.isVerified ? "Aktif" : "Onay Bekliyor")}
                            </td>
                            <td style={{ padding: '8px', border: '1px solid #ddd', textAlign: 'center' }}>
                                {/* Admin'i (ve kendini) banlama butonunu gösterme */}
                                {u.role !== 'ROLE_ADMIN' ? (
                                    <button 
                                        onClick={() => handleToggleBan(u.id)}
                                        style={{ background: u.banned ? 'green' : 'red', color: 'white', cursor: 'pointer' }}
                                    >
                                        {u.banned ? "Banı Aç" : "Banla"}
                                    </button>
                                ) : (
                                    <small>(Admin)</small>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default UserManagementPage;
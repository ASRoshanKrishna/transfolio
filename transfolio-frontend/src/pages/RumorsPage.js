import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const RumorsPage = () => {
  const [rumors, setRumors] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('jwtToken');
    navigate('/login');
  };

  useEffect(() => {
    const fetchRumors = async () => {
      const user = JSON.parse(localStorage.getItem('user'));
      const token = localStorage.getItem('jwtToken');

      if (!user || !token) {
        setRumors([{ summary: '❌ You must be logged in to view rumors.' }]);
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get(
          `https://transfolio-backend.onrender.com/api/personalized/rumors/${user.id}`,
          {
              headers: { Authorization: `Bearer ${token}` },
              withCredentials: true
          }
        );
        const data = response.data;

        if (data.length === 0) {
          setRumors([{ summary: '🙈 No rumors yet. Try tracking more clubs!' }]);
        } else {
          setRumors(data);
        }
      } catch (err) {
        console.error("Error fetching rumors", err);
        setRumors([{ summary: '⚠️ Failed to load rumors.' }]);
      } finally {
        setLoading(false);
      }
    };

    fetchRumors();
  }, []);

  return (
    <div className="news-container">
      <h2>📣 Transfer Rumors</h2>

      <div className="nav-buttons">
        <button onClick={() => navigate('/news')}>📢 News</button>
        <button onClick={() => navigate('/search')}>🔍 Search</button>
        <button onClick={logout}>🚪 Logout</button>
      </div>

      {loading && <p>🌀 Loading rumors...</p>}

      {rumors.map((rumor, idx) => (
        <div key={idx} className="rumor-card">
          <p className="summary-text">
            {rumor.summary || '🤖 Summary not available.'}
          </p>
          {rumor.threadUrl && rumor.threadUrl.includes("http") && (
            <a
              href={rumor.threadUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="thread-link"
            >
              🔗 View Full Thread
            </a>
          )}
        </div>
      ))}
    </div>
  );
};

export default RumorsPage;

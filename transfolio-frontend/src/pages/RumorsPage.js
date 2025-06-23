import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const RumorsPage = () => {
  const [rumors, setRumors] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchRumors = async () => {
      const user = JSON.parse(localStorage.getItem('user'));
      if (!user || !user.id) {
        setRumors([{ summary: '❌ You must be logged in to view rumors.' }]);
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/personalized/rumors/${user.id}`);
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
      </div>

      {loading && <p>🌀 Loading rumors...</p>}

      {rumors.map((rumor, idx) => (
        <div key={idx} className="rumor-card">
          <p className="summary-text">
            {rumor.summary || '🤖 Generating summary...'}
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

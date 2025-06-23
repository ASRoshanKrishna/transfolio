import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const NewsPage = () => {
  const [news, setNews] = useState([]);
  const [message, setMessage] = useState('Loading your transfer updates...');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchNews = async () => {
      const user = JSON.parse(localStorage.getItem('user'));
      if (!user || !user.id) {
        setMessage('❌ You must be logged in to view personalized news.');
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/personalized/news/${user.id}`);
        setNews(response.data);
        setMessage(response.data.length === 0
          ? '📭 No transfer updates yet. Try scouting some other clubs!' : '');
      } catch (error) {
        setMessage('⚠️ Failed to fetch news. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchNews();
  }, []);

  return (
    <div className="news-container">
      <h2>📢 Transfer News</h2>

      <div className="nav-buttons">
        <button onClick={() => navigate('/search')}>🔍 Search Preferences</button>
        <button onClick={() => navigate('/rumors')}>📣 Rumors</button>
      </div>

      {loading ? (
        <p>{message}</p>
      ) : message ? (
        <p>{message}</p>
      ) : (
        news.map((entry) => (
          <div key={entry.id} className="news-entry">
            <img src={entry.playerImage} alt={entry.playerName} width={50} />
            <div>
              <strong>{entry.playerName}</strong> ({entry.age}) - {entry.position}
              <br />
              <span>➡️ {entry.transferType.toUpperCase()} to {entry.clubName}</span>
              <br />
              <small>💰 {entry.transferFee} | 📅 {entry.transferDate}</small>
              <p className="summary-text">🧠 {entry.summary}</p>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default NewsPage;

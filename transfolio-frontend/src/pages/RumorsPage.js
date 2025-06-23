import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../App.css'; // Assuming you're using common styles like in NewsPage

const RumorsPage = () => {
  const [rumors, setRumors] = useState([]);
  const [message, setMessage] = useState('Loading transfer rumors...');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRumors = async () => {
      const user = JSON.parse(localStorage.getItem('user'));

      if (!user || !user.id) {
        setMessage('❌ You must be logged in to view transfer rumors.');
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/personalized/rumors/${user.id}`);
        setRumors(response.data);
        setMessage(response.data.length === 0 ? '📭 No rumors found for your tracked clubs.' : '');
      } catch (error) {
        console.error("❌ Error fetching rumors:", error);
        setMessage('⚠️ Failed to fetch rumors. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchRumors();
  }, []);

  if (loading) return <p>{message}</p>;

  return (
    <div className="news-container">
      <h2>📣 Transfer Rumors</h2>
      {loading ? (
        <p>{message}</p>
      ) : message ? (
        <p>{message}</p>
      ) : (
        rumors.map((rumor, index) => (
          <div key={index} className="rumor-card">
            <p className="summary-text">🧠 {rumor.summary}</p>
            {rumor.threadUrl && (
              <a href={rumor.threadUrl} target="_blank" rel="noopener noreferrer" className="thread-link">
                🔗 View Full Thread
              </a>
            )}
          </div>
        ))
      )}
    </div>
  );
};

export default RumorsPage;

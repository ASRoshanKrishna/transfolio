import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../App.css';

const SearchPreferencePage = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [clubs, setClubs] = useState([]);
  const [message, setMessage] = useState('');
  const [cooldown, setCooldown] = useState(0);

  // ⏱ Decrease cooldown every second
  useEffect(() => {
    let timer;
    if (cooldown > 0) {
      timer = setInterval(() => {
        setCooldown(prev => {
          if (prev === 1) clearInterval(timer);
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [cooldown]);

  const handleSearch = async () => {
    const user = JSON.parse(localStorage.getItem('user'));

    if (!user || !user.id) {
      setMessage("❌ User not logged in.");
      return;
    }

    if (!searchTerm.trim()) {
       setMessage("⚠️ Please enter a club name to search.");
       return;
    }

    if (cooldown > 0) {
      setMessage(`⏳ Please wait ${cooldown}s before searching again.`);
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8080/api/search/clubs?query=${searchTerm}&userId=${user.id}`);
      setClubs(response.data);
      setMessage('');
      setCooldown(30); // Start cooldown after search
    } catch (error) {
      setMessage('⚠️ Error fetching clubs');
    }
  };

  const handleSave = async (club) => {
    const user = JSON.parse(localStorage.getItem('user'));

    const payload = {
      userId: user.id,
      clubIdApi: club.id,
      clubName: club.name,
      competitionId: club.competitionId,
      competitionName: club.competitionName,
      logoUrl: club.logoUrl
    };

    try {
      await axios.post('http://localhost:8080/api/user/preferences', payload);
      setMessage(`✅ ${club.name} added to preferences!`);
    } catch (error) {
      if (error.response && error.response.status === 409) {
        setMessage(`⚠️ ${club.name} already in your preferences.`);
      } else {
        setMessage('❌ Failed to save preference');
      }
    }
  };

  return (
    <div className="search-pref-container">
      <h2>Search Your Favorite Club</h2>
      <input
        placeholder="Type club name..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
      <button onClick={handleSearch} disabled={cooldown > 0}>
        {cooldown > 0 ? `Wait ${cooldown}s` : 'Search'}
      </button>

      {clubs.map((club) => (
        <div key={club.id} className="club-result">
          <img src={club.logoUrl} alt={club.name} width={50} />
          <span>{club.name}</span>
          <button onClick={() => handleSave(club)}>Save Preference</button>
        </div>
      ))}

      <p>{message}</p>
    </div>
  );
};

export default SearchPreferencePage;

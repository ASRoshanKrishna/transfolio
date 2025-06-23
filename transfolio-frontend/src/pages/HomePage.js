// src/pages/HomePage.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../App.css';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <div className="home-container">
      <h1 className="title">⚽ Welcome to TransFolio!</h1>
      <p className="subtitle">Your AI-powered football transfer tracker 🔍🤖</p>

      <div className="nav-buttons">
        <button onClick={() => navigate('/register')}>📝 Register</button>
        <button onClick={() => navigate('/login')}>🔐 Login</button>
      </div>
    </div>
  );
};

export default HomePage;

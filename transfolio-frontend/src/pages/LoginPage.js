import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import '../App.css';

const LoginPage = () => {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // 👇 Send username + password instead of email
      const response = await axios.post('https://transfolio-backend.onrender.com/api/auth/login', formData);
      const { token, username, email, id } = response.data;

      localStorage.setItem('jwtToken', token);
      localStorage.setItem('user', JSON.stringify({ id, username, email }));

      setMessage(`✅ Welcome, ${username}! Redirecting...`);
      setTimeout(() => navigate('/search'), 1500);
    } catch (err) {
      if (err.response?.status === 404) setMessage('🙁 User not found.');
      else if (err.response?.status === 401) setMessage('❌ Invalid password.');
      else setMessage('⚠️ Login failed.');
    }
  };

  return (
    <div className="register-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <input
          name="username"
          placeholder="Username"
          onChange={handleChange}
          required
        />
        <input
          name="password"
          placeholder="Password"
          type="password"
          onChange={handleChange}
          required
        />
        <button type="submit">Login</button>
      </form>
      <p>{message}</p>
      <p>Don't have an account? <Link to="/register">Register here</Link></p>
    </div>
  );
};

export default LoginPage;
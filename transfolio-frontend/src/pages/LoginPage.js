import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import '../App.css';

const LoginPage = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
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
      const response = await axios.post('http://localhost:8080/api/auth/login', formData);
      const user = response.data;
      localStorage.setItem('user', JSON.stringify(user));
      setMessage(`✅ Welcome, ${user.username}! Redirecting...`);
      setTimeout(() => navigate('/search'), 1500);
    } catch (err) {
      if (err.response?.status === 404) {
        setMessage('🙁 User not found.');
      } else if (err.response?.status === 401) {
        setMessage('❌ Invalid password.');
      } else {
        setMessage('⚠️ Login failed.');
      }
    }
  };

  return (
    <div className="register-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <input name="email" placeholder="Email" onChange={handleChange} required />
        <input name="password" placeholder="Password" type="password" onChange={handleChange} required />
        <button type="submit">Login</button>
      </form>
      <p>{message}</p>
      <p>Don't have an account? <Link to="/register">Register here</Link></p>
    </div>
  );
};

export default LoginPage;

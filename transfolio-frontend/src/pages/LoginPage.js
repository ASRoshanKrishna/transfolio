// src/pages/LoginPage.js
import React, { useState } from 'react';
import axios from 'axios';
import '../App.css'; // reuse existing styling

const LoginPage = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

  const [message, setMessage] = useState('');

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
      setMessage(`✅ Welcome, ${user.username}!`);
    } catch (err) {
      if (err.response) {
        if (err.response.status === 404) {
          setMessage('🙁 User not found.');
        } else if (err.response.status === 401) {
          setMessage('❌ Invalid password.');
        } else {
          setMessage('⚠️ Login failed.');
        }
      } else {
        setMessage('❌ Server error.');
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
    </div>
  );
};

export default LoginPage;

import React, { useState } from 'react';
import axios from 'axios';
import '../App.css';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    username: '',
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
      const response = await axios.post('http://localhost:8080/api/auth/register', formData);
      setMessage('🎉 Registered Successfully!');
      console.log(response.data); // you can route to login now
    } catch (err) {
      if (err.response && err.response.status === 409) {
        setMessage('⚠️ Email already exists.');
      } else {
        setMessage('❌ Registration failed.');
      }
    }
  };

  return (
    <div className="register-container">
      <h2>Register</h2>
      <form onSubmit={handleSubmit}>
        <input name="username" placeholder="Name" onChange={handleChange} required />
        <input name="email" placeholder="Email" onChange={handleChange} required />
        <input name="password" placeholder="Password" type="password" onChange={handleChange} required />
        <button type="submit">Register</button>
      </form>
      <p>{message}</p>
    </div>
  );
};

export default RegisterPage;

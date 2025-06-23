import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import RegisterPage from './pages/RegisterPage';
import LoginPage from './pages/LoginPage';
import SearchPreferencePage from './pages/SearchPreferencePage';
import NewsPage from './pages/NewsPage';
import RumorsPage from './pages/RumorsPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/search" element={<SearchPreferencePage />} />
        <Route path="/news" element={<NewsPage />} />
        <Route path="/rumors" element={<RumorsPage />} />
        {/* Add other routes here later */}
      </Routes>
    </Router>
  );
}

export default App;

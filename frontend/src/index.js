import './index.css';
import React, { useEffect, useState } from 'react';
import ReactDOM from 'react-dom';
import SearchEngine from './components/SearchEngine/SearchEngine';

function App() {
  const [mode, setMode] =  useState(1);
  console.log(mode);
  useEffect(() => {
    if(mode){
      document.body.classList.add('dark-mode');
      document.body.classList.remove('light-mode');

    } else {
      document.body.classList.remove('dark-mode');
      document.body.classList.add('light-mode');

    }
  }, [mode]);

  const toggleDarkMode = () => {
    setMode(1-mode);
  };

  return (
    <React.StrictMode>
      <div className="dark-mode-toggle">
        <SearchEngine />
        <button onClick={toggleDarkMode}>Toggle Dark Mode</button>
      </div>
    </React.StrictMode>
  );
}

ReactDOM.render(<App />, document.getElementById('root'));

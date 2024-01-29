import React, { useState } from "react";
import SearchBar from "../SearchBar/SearchBar";
import SearchResults from "../SearchResults/SearchResults";

const SearchEngine = () => {
    const [results, setResults] = useState([]);
    const [searchTime, setSearchTime] = useState('');

    const performSearch = (query) => {
        if (!query) return;
        
    };

    return (
        <div>
            <SearchBar onSearch={performSearch} setSearchTime={setSearchTime} />
            <p>Search time: {searchTime}</p>
        </div>
    );
};

export default SearchEngine;

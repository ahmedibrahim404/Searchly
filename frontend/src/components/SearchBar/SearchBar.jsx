import React, { useState } from "react";

const SearchBar = ({ onSearch, setSearchTime }) => {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const resultsPerPage = 10;

    const indexOfLastResult = currentPage * resultsPerPage;
    const indexOfFirstResult = indexOfLastResult - resultsPerPage;
    const currentResults = results.slice(indexOfFirstResult, indexOfLastResult);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };
    


    const handleQueryChange = (event) => {
        setQuery(event.target.value);
    };

    const handleQuerySubmit = (event) => {
        event.preventDefault();
        onSearch(query);
    };

    const fetchData = (e) => {
        const currentTime = new Date().getUTCMilliseconds();
        console.log(currentTime);
        e.preventDefault();        
        fetch(`http://localhost:8080/search?query=${query}`)
            .then((response) => response.json())
            .then((data) => {
                setResults(data);
            })
            .catch((error) => {
            console.error(error);
        });

        setSearchTime((new Date().getUTCMilliseconds() - currentTime)/1000);

    };
      
    return (
        <div>
        <h1 style={{textAlign:'center'}}>Searchly</h1>
        <form className="search-form" onSubmit={handleQuerySubmit}
            style = {{
                padding:'20px',
                display:'flex',
                flexDirection:'row',
                justifyContent:'center',
                alignContent:'center'
            }}
        >
            <input
                type="text"
                className="search-input"
                value={query}
                onChange={handleQueryChange}
                placeholder="Search..."
            />
            <button type="submit" className="search-button" onClick={fetchData}>
                Search
            </button>
        </form>
        {currentResults.map((link) => (
      <div style={{ background: "#FFF", marginBottom:'5px', minHeight:'20px', margin:'15px 25px', fontSize:'25px' }} key={link}>
        <a href={link} target={"_blank"}>{link}</a>
      </div>
    ))}

        <div className="pagination" style={{
            marginLeft:'25px'
        }}>
                {Array.from({ length: Math.ceil(results.length / resultsPerPage) }, (_, i) => i + 1).map((pageNumber) => (
                    <button
                        key={pageNumber}
                        className={currentPage === pageNumber ? 'active' : ''}
                        onClick={() => handlePageChange(pageNumber)}
                    >
                        {pageNumber}
                    </button>
                ))}
            </div>

        </div>
    );
};

export default SearchBar;
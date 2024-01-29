import React, { useState } from "react";
import SearchResult from "../SearchResult/SearchResult";

const SearchResults = ({ results }) => {
    const [currentPage, setCurrentPage] = useState(1);
    const resultsPerPage = 10;

    const indexOfLastResult = currentPage * resultsPerPage;
    const indexOfFirstResult = indexOfLastResult - resultsPerPage;
    const currentResults = results.slice(indexOfFirstResult, indexOfLastResult);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    return (
        <div className="search-results">
            {currentResults.map((result, index) => (
                <SearchResult
                    key={index}
                    title={result.title}
                    url={result.url}
                    snippet={result.snippet}
                />
            ))}
            <div className="pagination">
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

export default SearchResults;
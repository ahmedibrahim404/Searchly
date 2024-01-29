# Searchly
This project aims to develop a basic search engine demonstrating web crawling, indexing, ranking, and query processing using Java. 

## Getting Started

For running the crawler
```sh
make crawler
```

For running the indexer
```sh
make indexer
```

For running the query test
```sh
make query-test
```

## Components

### Web Crawler
- Collects documents starting with seed URLs.
- Ensures crawling etiquette and multithreading.
- Collects 6000 pages for the project.

### Indexer
- Indexes documents for fast retrieval.
- Maintains index in secondary storage.
- Supports incremental updates.

### Query Processor
- Receives and processes user queries.
- Supports stem matching.

### Phrase Searching
- Supports phrase searching with quotation marks.
- Maintains word order in results.

### Ranker
- Ranks documents based on relevance and popularity.
- Considers various relevance calculation methods.
- Uses algorithms like PageRank for popularity.

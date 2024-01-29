# Makefile

# Crawler module
build-crawler:
	javac -cp ./src/jsoup-1.16.1.jar;./src/mongo-java-driver-3.12.10.jar ./src/Crawler.java ./src/TestCrawler.java ./src/word_par.java ./src/MongoDB.java ./src/RobotObject.java

run-crawler:
	java -cp "./src/jsoup-1.16.1.jar;./src/mongo-java-driver-3.12.10.jar;./src" TestCrawler
	
crawler: build-crawler run-crawler

# Indexer module
build-indexer:
	javac -cp ./src/lucene-analyzers-common-8.8.2.jar;./src/jsoup-1.16.1.jar;./src/mongo-java-driver-3.12.10.jar ./src/word_par.java ./src/QueryProcessor.java ./src/MongoDB.java ./src/Indexer.java

run-indexer:
	java -cp "./src/lucene-analyzers-common-8.8.2.jar;./src/jsoup-1.16.1.jar;./src/mongo-java-driver-3.12.10.jar;./src" Indexer

indexer: build-indexer run-indexer

# MongoDB module
build-mongodb:
	javac -cp ./src/mongo-java-driver-3.12.10.jar ./src/MongoDB.java

run-mongodb:
	java -cp "./src/mongo-java-driver-3.12.10.jar;./src" MongoDB

mongodb: build-mongodb run-mongodb

# QueryProcessor module
build-queryprocessor:
	javac -cp "./src/lucene-analyzers-common-8.8.2.jar" ./src/QueryProcessor.java

run-queryprocessor:
	java -cp "./src;./src/lucene-analyzers-common-8.8.2.jar" QueryProcessor

query-processor: build-queryprocessor run-queryprocessor

crawler-indexer: crawler indexer

build-query-test:
	javac -cp ./src/lucene-analyzers-common-8.8.2.jar;./src/jsoup-1.16.1.jar;./src/mongo-java-driver-3.12.10.jar ./src/word_par.java ./src/QueryProcessor.java ./src/MongoDB.java ./src/Ranker.java ./src/SearchTest.java

run-query-test:
	java -cp "./src/lucene-analyzers-common-8.8.2.jar;./src/jsoup-1.16.1.jar;./src/mongo-java-driver-3.12.10.jar;./src" SearchTest

query-test: build-query-test run-query-test

build-api:
	javac -cp ".;C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\lib\lucene-analyzers-common-8.8.2.jar;C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\lib\jsoup-1.16.1.jar;C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\lib\mongo-java-driver-3.12.10.jar;C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\lib\servlet-api.jar;C:\Program Files (x86)\Apache Software Foundation\Tomcat 9.0\lib\gson-2.8.2.jar" ./src/word_par.java ./src/QueryProcessor.java ./src/MongoDB.java ./src/Ranker.java ./src/SearchTest.java ./src/SearchAPI.java

clean:
	rm -rf *.class

all: crawler indexer mongodb queryprocessor

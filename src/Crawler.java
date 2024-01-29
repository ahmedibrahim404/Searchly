
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Set;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import java.util.HashMap;

import java.net.MalformedURLException;
import java.net.URL;


public class Crawler implements Runnable {

    private final int MAX_WEB_PAGES = 200;

    private Set<String> pagesVisited = new ConcurrentSkipListSet<>();
    private Queue<String> pagesToVisit = new ConcurrentLinkedQueue<>();

    private Object getContentLock = new Object();
    private Object pagesVisitedLock = new Object();
    private Object pagesToVisitLock = new Object();
    private Object pagesPopularityLock = new Object();

    private HashMap<String, Integer> PagesPopularity = new HashMap<>();
   
    MongoDB mongoDBClient = new MongoDB();
    RobotObject robotObject = new RobotObject();

    public Crawler() {
        mongoDBClient.connectToDatabase();
    }

    private String removePath(String urlString) {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            String result = protocol + "://" + host;
            if (port != -1) {
                result += ":" + port;
            }
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return urlString; 
        }
    }


    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " has started..");
        while (true) {
            synchronized (pagesVisitedLock) {
                if (pagesVisited.size() >= MAX_WEB_PAGES) {
                    break;
                }
            }

            String url;
            synchronized (pagesToVisitLock) {
                url = pagesToVisit.poll();
                // mongoDBClient.updatePagesToVisit(url, false);
            }

            try {
                // Validate the URL
                if (!isValid(url)) {
                    System.out.println(Thread.currentThread().getName() + ":");
                    System.out.println("Invalid URL: " + url);
                    continue;
                }

                // Skip URLs starting with 'javascript:'
                if (url.startsWith("javascript:")) {
                    System.out.println(Thread.currentThread().getName() + ":");
                    System.out.println("Skipping JavaScript URL: " + url);
                    continue;
                }

                // Normalize the URL
                URI uri = new URI(url);
                String normalizedUrl = uri.normalize().toString();
                
                if(mongoDBClient.isPageVisited(normalizedUrl)) {
                    System.out.println(normalizedUrl + " is already visited");
                    continue;
                }

                // Connect to the URL
                Connection con = Jsoup.connect(normalizedUrl);
                Document doc = con.get();
                if (con.response().statusCode() == 200) { // 200 is the HTTP OK status code
                    // Check if the URL is allowed to be crawled
                    if (!robotObject.isURLAllowed(normalizedUrl)) {
                        System.out.println(Thread.currentThread().getName() + ":");
                        System.out.println("URL is not allowed: " + normalizedUrl);
                        continue;
                    }
                    
                    synchronized (pagesVisitedLock) {
                        if (pagesVisited.size() >= MAX_WEB_PAGES) {
                            break;
                        }

                        pagesVisited.add(normalizedUrl);
                        System.out.println("Visited " + normalizedUrl);
                        mongoDBClient.updatePagesVisited(normalizedUrl, doc.html(), doc.title());

                        synchronized (getContentLock) {
                            getPageContent(doc, normalizedUrl);
                        }
                        
                        System.out.println(Thread.currentThread().getName() + ":");
                        System.out.println("Link: " + normalizedUrl);
                        System.out.println("Title: " + doc.title());
                        
                        if (pagesVisited.size() >= MAX_WEB_PAGES) {
                            break;
                        }
                    }

                    for (Element link : doc.select("a[href]")) {
                        String nextUrl = link.attr("abs:href");

                        try {
                            // Normalize the next URL
                            URI nextUri = new URI(nextUrl);
                            String normalizedNextUrl = nextUri.normalize().toString();

                            // Add the next URL to the queue
                            synchronized (pagesVisitedLock) {
                                if (pagesVisited.contains(normalizedNextUrl)) {
                                    continue;
                                }
                                
                                synchronized (pagesPopularityLock) {
                                    if (PagesPopularity.containsKey(removePath(normalizedNextUrl))) {
                                        PagesPopularity.put(removePath(normalizedNextUrl), PagesPopularity.get(removePath(normalizedNextUrl)) + 1);
                                    } else {
                                        PagesPopularity.put(removePath(normalizedNextUrl), 1);
                                    }
                                }


                                synchronized (pagesToVisitLock) {
                                    pagesToVisit.add(normalizedNextUrl);
                                    // mongoDBClient.updatePagesToVisit(normalizedNextUrl, true);
                                }
                            }
                        } catch (URISyntaxException e) {
                            System.out.println(Thread.currentThread().getName() + ":");
                            System.out.println("Invalid URL: " + nextUrl);
                        }
                    }
                }
            } catch (IOException | URISyntaxException e) {
                System.out.println(Thread.currentThread().getName() + ":");
                System.out.println("Error while crawling URL: " + url);
                System.out.println("Error message: " + e.getMessage());
            }
        }
        System.out.println(Thread.currentThread().getName() + " has finished..");
    }




    public void crawl() {
        if (mongoDBClient.getState().equals("crawling")) {
            mongoDBClient.getPagesVisited(pagesToVisit, pagesVisited);
        }

        if (pagesToVisit.isEmpty()) {
            getSeed();
        }

        mongoDBClient.setState("crawling");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter number of threads: ");
            int numThreads = scanner.nextInt();
            
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            
            for (int i = 0; i < numThreads; i++) {
                executor.execute(this);
            }
            executor.shutdown();
            
            while (!executor.isTerminated()) {
                // Wait for all threads to finish
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        mongoDBClient.InsertPagePopularity(PagesPopularity, pagesVisited);
        mongoDBClient.setState("idle");

        System.out.println("Overall pages visited = " + pagesVisited.size());
        System.out.println("Crawling finished..");
    }



    private void getSeed() {
        try {
            File file = new File("src/seed.txt");
            Scanner scanner = new Scanner(file);
            
            while (scanner.hasNextLine()) {
                String url = scanner.nextLine();
                pagesToVisit.add(url);
            }
            
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error while reading seed file: " + e.getMessage());
        }
    }

    private void getPageContent(Document doc, String url) {
        final String path = "downloadedPages/";
        
        String fileName = url.substring(0, url.length());
        fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        try {
            File file = new File(path + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            writer.write(url + "\n");
            writer.write(doc.html());
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean isValid(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}


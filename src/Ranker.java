import java.io.*;
import java.util.*;
import org.bson.Document;

import com.mongodb.client.FindIterable;

import javax.sound.sampled.EnumControl;
import org.tartarus.snowball.ext.PorterStemmer;

import java.net.MalformedURLException;
import java.net.URL;

public class Ranker {

    static QueryProcessor qp = new QueryProcessor();
    MongoDB mongoDBClient = new MongoDB();

    Ranker(){
        mongoDBClient.connectToDatabase();
    }

    public List<String> generateScore(String query) {
        Boolean quote = false;
        if (query.startsWith("\"") && query.endsWith("\"")) {
            quote = true;
        }

        
        List<String> sortedDocNames = new ArrayList<>();
        if(quote){
            String queryFinal = query.substring(1, query.length() - 1);
            System.out.println(queryFinal);
            sortedDocNames = mongoDBClient.getPages(queryFinal);
            return sortedDocNames;
        }

        String[] words = query.split("\\s+");
        List<String> stemmedWords = new ArrayList<>();
        for (String word : words) {
            word = qp.ProcessQuery(word);
            stemmedWords.add(word);
        }


        Set<String> relevantDocs = new HashSet<>();
        Map<String, Double> docScores = new HashMap<>();

        for (String stemmedWord : stemmedWords) {
            Document queryDoc = new Document("word", stemmedWord);
            FindIterable<Document> result = mongoDBClient.findDoc(queryDoc);
            if (result.first() != null) {
                Document document = result.first();
                Object[] docs = ((ArrayList) document.get("docs")).toArray();
                for (Object docEntry : docs) {
                    Document doc = (Document) docEntry;
                    String docName = doc.getString("url");
                    double TF = doc.getDouble("TF");
                    double TF_IDF = mongoDBClient.calculateTF_IDF(stemmedWord, TF);
                    if (docScores.containsKey(docName)) {
                        double score = docScores.get(docName) + TF_IDF;
                        docScores.put(docName, score);
                    } else {
                        docScores.put(docName, TF_IDF);
                    }
                }
            }
        }


        for (Map.Entry<String, Double> entry : docScores.entrySet()) {
            String docName = entry.getKey();
            double score = entry.getValue();
            double popularity = mongoDBClient.getPagePopularity(removePath(docName));
            docScores.put(docName, score * popularity);
        }

        List<Map.Entry<String, Double>> sortedDocs = new ArrayList<>(docScores.entrySet());
        sortedDocs.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (Map.Entry<String, Double> entry : sortedDocs) {
            sortedDocNames.add(entry.getKey());
        }

        return sortedDocNames;

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

}

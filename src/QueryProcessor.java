
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import org.tartarus.snowball.ext.PorterStemmer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;


public class QueryProcessor {

    static final String[] stopWords = {"a", "an", "the", "is", "are", "am", "was", "were", "has", "have", "had", "been", "will", "shall", "be", "do", "does", "did", "can", "could", "may", "might", "must", "should", "of", "in", "on", "at", "to", "from", "by", "for", "about", "with", "without", "not", "no", "yes", "or", "and", "but", "if", "else", "then", "than", "else", "when", "where", "what", "who", "how", "which", "whom", "whose", "why", "because", "however", "therefore", "thus", "so", "such", "this", "that", "these", "those", "their", "his", "her", "its", "our", "your", "their", "any", "some", "many", "much", "few", "little", "own", "other", "another", "each", "every", "all", "both", "neither", "either", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth"};
    static final Character[] punctuations = {'.', ',', ':', ';', '?', '!', '\'', '\"', '(', ')', '{', '}', '[', ']', '<', '>', '/', '\\', '|', '-', '_', '+', '=', '*', '&', '^', '%', '$', '#', '@', '`', '~', '“', '”', '‘', '’', '–', '—', '…'};

    String query;
    String[] phrases;
    
    static final Set<String> stopWordsSet = new HashSet<>(Arrays.asList(stopWords));
    static final Set<Character> punctuationsSet = new HashSet<>(Arrays.asList(punctuations));
    
    QueryProcessor(){
        
    }


    public String ProcessQuery(String query) {
        return PreProcessQuery(query);
    }

    private String PreProcessQuery (String query) {
        query = query.toLowerCase();
        query = removeStopWords(query);
        query = removePunctuation(query);
        query = stem(query);
        return query;
    }

    private String removeStopWords (String query) {
        String[] words = query.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (stopWordsSet.contains(word)) {
                continue;
            }
            result.append(word).append(" ");
        }
        return result.toString().trim();
    }

    private String removePunctuation (String query) {
        String[] words = query.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            StringBuilder wordBuilder = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                if (!punctuationsSet.contains(word.charAt(i))) {
                    wordBuilder.append(word.charAt(i));
                }
            }
            word = wordBuilder.toString();
            result.append(word).append(" ");
        }
        return result.toString().trim();
    }
    
    public static String stem(String word){
        PorterStemmer porterStemmer = new PorterStemmer();
        porterStemmer.setCurrent(word);
        porterStemmer.stem();
        return porterStemmer.getCurrent();
    }


}



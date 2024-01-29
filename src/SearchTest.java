import java.util.List;
import java.util.Scanner;

public class SearchTest {

    private String query;
    private Ranker rank;
    MongoDB mongoDBClient = new MongoDB();


    public static void main(String[] args) {
        SearchTest searchTest = new SearchTest();
        searchTest.run();
    }

    private void run() {
        mongoDBClient.connectToDatabase();
        rank = new Ranker();
        readQuery();
        List<String> searchResults = rank.generateScore(query);
        printResults(searchResults);
    }

    private void readQuery() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your search query: ");
        query = scanner.nextLine();
    }

    private void printResults(List<String> results) {
        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            System.out.println("Search Results:");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
                System.out.println(mongoDBClient.getTitleFromUrl(results.get(i)));
            }
        }
    }
}

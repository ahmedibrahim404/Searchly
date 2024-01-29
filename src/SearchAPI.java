import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

public class SearchAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");

        String query = request.getParameter("query");

        // Create an instance of Ranker
        Ranker ranker = new Ranker();
        
        // Call the fetch method in Ranker and get the result
        List<String> result = ranker.generateScore(query);
        
        // Create a Gson instance
        Gson gson = new Gson();
        
        // Convert the result list to JSON string
        String jsonResponse = gson.toJson(result);
        
        // Set the content type to plain text
        response.setContentType("text/plain");
        
        // Write the JSON response to the response writer
        response.getWriter().write(jsonResponse);
    }
}

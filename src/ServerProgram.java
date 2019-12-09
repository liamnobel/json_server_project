import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

//import org.json.JSONObject; (will ask about proper implementation of this)
//import org.json.JSONTokener;

public class ServerProgram {
    //start server and route calls to corresponding functions
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        //server.createContext("/", new GetHandler()); // unused simple GET call
        server.createContext("/lcs", new LCSHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}

class GetHandler implements HttpHandler {
    // handle get call to "/" resource (not used)
    public void handle(HttpExchange t) throws IOException {
        System.out.println("Site requested.");
        System.out.println(convertStreamToString(t.getRequestBody()));

        // add header for html
        Headers h = t.getResponseHeaders();
        h.add("Content-Type", "text/html");

        // web file to provide
        File file = new File("index.html");
        byte[] bytearray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(bytearray, 0, bytearray.length);
        bis.close();

        // send
        t.sendResponseHeaders(200, file.length());
        OutputStream os = t.getResponseBody();
        os.write(bytearray, 0, bytearray.length);
        os.close();
    }

    // helper function to convert streams into strings
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

// class for creating a response to LCS call to server
class LCSHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        // console output and response init
        System.out.println("LCS requested.");
        String bodystring = convertStreamToString(t.getRequestBody());
        System.out.println(bodystring);

        boolean format_correct = true;
        boolean contains_strings = false;
        boolean found_set_of_strings = false;
        boolean no_duplicates = true;

        // get all value tags within JSON object (this is very prone to error)
        List < String > values = new ArrayList < String > ();

        // find first setOfStings entry
        int index = bodystring.indexOf("\"setOfStrings\"");
        if (index != -1) {
            found_set_of_strings = true;
            index = bodystring.indexOf("\"value\"", index + 1);
            while (index != -1) {
                try {
                    index = bodystring.indexOf("\"", index + 1);
                    index = bodystring.indexOf("\"", index + 1);
                    int beginIndex = index + 1;
                    index = bodystring.indexOf("\"", index + 1);
                    int endIndex = index;

                    String string_to_add = bodystring.substring(beginIndex, endIndex);

                    if (string_to_add.compareTo("") != 0) {
                        contains_strings = true;
                        System.out.println("Adding: " + string_to_add);

                        if (no_duplicates) {
                            for (String tempstr: values) {
                                if (string_to_add.compareTo(tempstr) == 0) {
                                    no_duplicates = false;
                                    System.out.println("First duplicate found.");
                                }
                            }
                        }

                        values.add(string_to_add);
                    }

                    index = bodystring.indexOf("\"value\"", index);
                } catch (Exception c) {
                    System.out.println("Format was interpretted incorrectly.");
                    format_correct = false;
                    break;
                }
            }
        }

        System.out.println("Sending back response!");

        // return proper responses to each case based on flags (likely could be rewritten into states)
        if (found_set_of_strings) {
            if (contains_strings) {
                if (no_duplicates) {
                    if (format_correct) {
                        // solve the lcs
                        ArrayList < String > stems = LongestCommonSubstring.solve_list(values);
                        for (String tempstr: stems) {
                            System.out.println(tempstr);
                        }

                        // construct and send JSON styled response
                        Headers h = t.getResponseHeaders();
                        h.add("Content-Type", "application/json");

                        int i = 0;
                        String inner = "{\"value\": \"" + stems.get(i) + "\"}";
                        for (i = 1; i < stems.size(); i++) {
                            inner += ", {\"value\": \"" + stems.get(i) + "\"}";
                        }

                        String response = "{\"lcs\" : [" + inner + "]}";
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        // incorrect format
                        Headers h = t.getResponseHeaders();
                        h.add("Content-Type", "text/plain");
                        String response = "The format of the request could not be understood. This could indicate that the supplied format is incorrect. Please try again.";
                        t.sendResponseHeaders(400, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } else {
                    // duplicate found
                    Headers h = t.getResponseHeaders();
                    h.add("Content-Type", "text/plain");
                    String response = "It would appear that there is a duplicate inside the \"setOfStrings\". The \"setOfStrings\" must be a set where all entries are unique.";
                    t.sendResponseHeaders(400, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                // no strings inside setOfStrings
                Headers h = t.getResponseHeaders();
                h.add("Content-Type", "text/plain");
                String response = "It appears that there are no formatted strings inside the \"setOfStrings\".";
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            // could not find setOfStrings
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "text/plain");
            String response = "The format of the request could not be understood. This could either mean the format is incorrect or the request is empty. Please try again.";
            t.sendResponseHeaders(400, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // helper function to convert streams into strings
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
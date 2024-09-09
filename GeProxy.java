package http_proxy_sorta;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class GeProxy {

    public static void main(String[] args) throws Exception {
    	System.out.println("Listening on port 8000");
    	
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler()); //don't touch, it somehow works
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	String temp = t.getRequestURI().getPath();
        	String urli="";
        	
        	for(int i=1; i<temp.length(); i++)
        		urli += temp.charAt(i);
        	//now urli contains the url we want to visit through this not-so-proxy
        	
        	String response="";
        	
        	HttpClient client = HttpClientBuilder.create().build();
        	HttpGet request = new HttpGet(urli);
        	try{
        		HttpResponse hresponse = client.execute(request);
        		HttpEntity entity = hresponse.getEntity();
        		response += EntityUtils.toString(entity);
        	}catch (IOException e){
        		e.printStackTrace();
        	}
        	
        	//response now contains the html
        	
        	//modify body
        	response = response.replace("DEADNAME","CHOSENNAME"); //works
        	
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}

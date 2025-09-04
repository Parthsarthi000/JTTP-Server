package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server extends Thread {

    Socket client;

    public Server(Socket client) {
        this.client = client;
    }
    
    @Override
    public void run() {

        System.out.println("Accepted new Connection");

        OutputStream output=null;
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            HttpRequest request = new HttpRequest(input);
            System.out.println(request.getRequest());
            output = client.getOutputStream();
            RequestHandler handler = new RequestHandler(request, output);
            handler.sendResponse();

        } catch (IOException e) {
            e.printStackTrace();

            try {
                if (output == null) {
                    output = client.getOutputStream(); // fallback if not already set
                }
                HttpResponse response = new HttpResponse()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR);
                response.write(output);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
    }
}
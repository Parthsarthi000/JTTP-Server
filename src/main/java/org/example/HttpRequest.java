package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RequestMethod fromString(String value) {
        for (RequestMethod method : RequestMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown method: " + value);
    }
}

enum CommonHeaderType {
    HOST("Host"),
    USER_AGENT("User-Agent"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection");

    private final String value;

    CommonHeaderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CommonHeaderType fromString(String value) {
        for (CommonHeaderType method : CommonHeaderType.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown method: " + value);
    }
}

public class HttpRequest {

    private RequestMethod requestMethod;// GET/POST/PUT/DELETE
    private String uri;// /..../.../ part

    // Common headers
    private String host;
    private String userAgent;
    private String contentType;
    private int contentLength;
    private String connection;

    private final HashMap<String, String> headers = new HashMap<>(); // for uncommon headers
    private String body;// after \r\n\r\n encountered
    // private final String request;

    public HttpRequest(BufferedReader input) throws IOException {
        parseRequestLine(input.readLine());
        parseHeaders(input);
        parseBody(input);
    }

    private void parseRequestLine(String line) {
        String[] requestContent = line.split(" ");
        setRequestMethod(requestContent[0]);
        setUri(requestContent[1]);
    }

    private void setRequestMethod(String method) {
        this.requestMethod = RequestMethod.fromString(method);
    }

    private void setUri(String uri) {
        this.uri = uri;
    }

    public void parseBody(BufferedReader input){
        char[]body=new char[this.contentLength];
        try{
        input.read(body, 0, contentLength);
        this.body=new String(body);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void parseHeaders(BufferedReader input) throws IOException {
        String header = null;
        while ((header = input.readLine()) != null && !header.isEmpty()) {
            setHeader(header);
        }
    }

    private void setHeader(String header) {
        String[] headerContents=header.split(":",2);
        if(headerContents.length<2) return;
        String headerType=headerContents[0].trim();
        String headerValue=headerContents[1].trim();  
        try{
            CommonHeaderType type=CommonHeaderType.fromString(headerType);
            switch (type){
                case HOST:
                    this.host=headerValue;
                    break;
                case USER_AGENT:
                    this.userAgent=headerValue;
                    break;
                case CONTENT_TYPE:
                    this.contentType = headerValue;
                    break;
                case CONTENT_LENGTH:
                    this.contentLength = Integer.parseInt(headerValue);
                    break;
                case CONNECTION:
                    this.connection= headerValue;
                    break;
                default: headers.put(headerType, headerValue);
            }
        }catch (IllegalArgumentException e) {
            // Store unknown headers
            headers.put(headerType, headerValue);
        }
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    public String getBody() {
        if (this.body==null || this.body.isEmpty()) {
            return "";
        }
        return this.body;
    }

    public String getUri() {
        return this.uri;
    }

    
    public String getRequest() {
    StringBuilder requestBuilder = new StringBuilder();

    // Request line
    requestBuilder.append(requestMethod).append(" ").append(uri).append(" HTTP/1.1\r\n");

    // Common headers
    if (host != null) requestBuilder.append("Host: ").append(host).append("\r\n");
    if (userAgent != null) requestBuilder.append("User-Agent: ").append(userAgent).append("\r\n");
    if (contentType != null) requestBuilder.append("Content-Type: ").append(contentType).append("\r\n");
    if (contentLength > 0) requestBuilder.append("Content-Length: ").append(contentLength).append("\r\n");
    if (connection != null) requestBuilder.append("Connection: ").append(connection).append("\r\n");

    // Uncommon headers
    for (Map.Entry<String, String> entry : headers.entrySet()) {
        requestBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
    }

    // End of headers
    requestBuilder.append("\r\n");

    // Body
    if (body != null && !body.isEmpty()) {
        requestBuilder.append(body);
    }

    return requestBuilder.toString();
}
}

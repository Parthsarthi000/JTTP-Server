package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405,"Mrthod Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String phrase;

    HttpStatus(int code, String phrase) {
        this.code = code;
        this.phrase = phrase;
    }

    public int getCode() {
        return this.code;
    }

    public String getPhrase() {
        return this.phrase;
    }

    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code)
                return status;
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}


public class HttpResponse {
   private String protocol = "HTTP/1.1";
   private HttpStatus status=null;
   private int contentLength;
   private String contentType=null;
   private String connection=null;
   private byte[] body=null;
    public HttpResponse status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse connection(String connection) {
        this.connection = connection;
        return this;
    }


    public HttpResponse body(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpResponse build() {
        HttpResponse response = new HttpResponse();
        response.status = this.status;
        response.contentType = this.contentType;
        response.connection = this.connection;
        response.body = this.body;
        response.contentLength = (body!=null && body.length>0)?body.length:0;
        return response;
    }

    public void write(OutputStream outputStream)throws IOException{

        BufferedWriter response=new BufferedWriter(new OutputStreamWriter(outputStream,StandardCharsets.UTF_8));
        response.write(protocol+" "+status.getCode()+" "+status.getPhrase()+"\r\n");

        if(contentType!=null) response.write("Content-Type: " + contentType + "\r\n");

        response.write("Content-Length: "+ contentLength+ "\r\n");

        if(connection!=null)   response.write("Connection: "+connection+ "\r\n");

        response.write("\r\n");
        response.flush();

        if(body!=null) 
        {
            outputStream.write(body);
            outputStream.flush();
        }
    }
}

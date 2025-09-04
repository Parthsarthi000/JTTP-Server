package org.example;

import java.io.IOException;
import java.io.OutputStream;

enum URL_CONTENT {

    EMPTY,
    INDEX_HTML,
    FILE_NAMES_INFO,
    FILE,
    ECHO,
    INVALID
}

public class RequestHandler {

    private HttpRequest request;
    private OutputStream clientOutput;
    private HttpResponse response;

    public RequestHandler(HttpRequest request, OutputStream output) throws IOException {
        this.clientOutput = output;
        this.request = request;
        this.handleMethod();
    }

    private void handleMethod() {

        RequestMethod method = this.request.getRequestMethod();
        switch (method) {
            case GET:
                this.methodGET(this.request.getUri());
                break;
            case POST:
                this.methodPOST(this.request.getUri());
                break;
            case DELETE:
                this.methodDELETE(this.request.getUri());
                break;
            default:
                break;
        }
    }

    private void methodGET(String url) {
        System.out.println("Inside methodGET");

        String[] urlContents = url.replaceFirst("^/", "").split("/");

        URL_CONTENT content = decipherURL(urlContents);

        FileHandler fileHandler = new FileHandler();
        String fileContents = "";
        String filesInfo;
        System.out.println(content.toString());
        switch (content) {
            case EMPTY:
                this.response = new HttpResponse()
                        .status(HttpStatus.OK)
                        .contentType("text/plain")
                        .body("You can try out the following Requests:GET /echo/message\nGET /index.html\nTo get the file names and info: GET /files\nTo get a specific file: GET /files/name_of_file\n"
                                .getBytes())
                        .build();

                break;

            case ECHO:
                this.response = new HttpResponse()
                        .status(HttpStatus.OK)
                        .contentType("text/plain")
                        .body(urlContents[1].getBytes())
                        .build();
                break;

            case INDEX_HTML:
                fileContents = fileHandler.getIndexHTML();
                if (fileContents.isEmpty()) {
                    this.response = new HttpResponse().status(HttpStatus.NOT_FOUND).build();
                } else {
                    this.response = new HttpResponse()
                            .status(HttpStatus.OK)
                            .contentType("text/html")
                            .body(fileContents.getBytes())
                            .build();
                }
                break;

            case FILE_NAMES_INFO:
                filesInfo = fileHandler.getFilesInfo();
                if (filesInfo.isEmpty()) {
                    this.response = new HttpResponse()
                            .status(HttpStatus.OK)
                            .contentType("text/plain")
                            .body("No files Exist!\nCreate new using POST /files/name_of_file\n".getBytes())
                            .build();
                }
                this.response = new HttpResponse()
                        .status(HttpStatus.OK)
                        .contentType("text/html")
                        .body(filesInfo.getBytes())
                        .build();
                break;

            case FILE:
                fileContents = fileHandler.getFile(urlContents[1]);
                if (fileContents.isEmpty()) {
                    this.response = new HttpResponse().status(HttpStatus.NOT_FOUND).build();
                } else {
                    this.response = new HttpResponse()
                            .status(HttpStatus.OK)
                            .contentType("text/plain")
                            .body(fileContents.getBytes())
                            .build();
                }
                break;

            case INVALID:
            default:
                this.response = new HttpResponse()
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType("text/plain")
                        .body("Invalid URL".getBytes())
                        .build();
                break;
        }

    }

    private URL_CONTENT decipherURL(String[] urlContents) {

        if (urlContents.length > 0) {
            switch (urlContents[0]) {
                case "index.html":
                    return URL_CONTENT.INDEX_HTML;
                case "echo":
                    return URL_CONTENT.ECHO;
                case "files":
                    if (urlContents.length <= 1)
                        return URL_CONTENT.FILE_NAMES_INFO;
                    else
                        return URL_CONTENT.FILE;
                default:
                    return URL_CONTENT.INVALID;
            }
        }
        return URL_CONTENT.EMPTY;
    }

    private void methodPOST(String url) {
        System.out.println("Inside methodPOST");

        String[] urlContents = url.replaceFirst("^/", "").split("/");
        URL_CONTENT content = decipherURL(urlContents);

        FileHandler fileHandler = new FileHandler();
        String fileContents;

        switch (content) {
            case FILE:
                // Expecting: /files/{filename}
                if (urlContents.length < 2 || urlContents[1].isEmpty()) {
                    this.response = new HttpResponse()
                            .status(HttpStatus.BAD_REQUEST)
                            .contentType("text/plain")
                            .body("Missing filename in URL".getBytes())
                            .build();
                    break;
                }

                String filename = urlContents[1];
                String requestBody = this.request.getBody(); // Assuming HttpRequest has getBody()

                boolean success = fileHandler.createFile(filename, requestBody);
                if (success) {
                    this.response = new HttpResponse()
                            .status(HttpStatus.CREATED)
                            .contentType("text/plain")
                            .body(("File '" + filename + "' created successfully").getBytes())
                            .build();
                } else {
                    this.response = new HttpResponse()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType("text/plain")
                            .body("Failed to write file".getBytes())
                            .build();
                }
                break;

            case INDEX_HTML:
            case FILE_NAMES_INFO:
            case ECHO:
            case EMPTY:
                this.response = new HttpResponse()
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .contentType("text/plain")
                        .body("POST not allowed on this endpoint".getBytes())
                        .build();
                break;

            case INVALID:
                this.response = new HttpResponse()
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType("text/plain")
                        .body("Invalid URL".getBytes())
                        .build();
                break;

            default:
                this.response = new HttpResponse()
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType("text/plain")
                        .body("Unhandled POST route".getBytes())
                        .build();
                break;
        }
    }

private void methodDELETE(String uri) {
    System.out.println("Inside methodDELETE");

    String[] urlContents = uri.replaceFirst("^/", "").split("/");
    URL_CONTENT content = decipherURL(urlContents);

    FileHandler fileHandler = new FileHandler();

    switch (content) {
        case FILE:
            if (urlContents.length < 2 || urlContents[1].isEmpty()) {
                this.response = new HttpResponse()
                        .status(HttpStatus.BAD_REQUEST)
                        .contentType("text/plain")
                        .body("Missing filename in URL".getBytes())
                        .build();
                break;
            }

            String filename = urlContents[1];
            boolean deleted = fileHandler.deleteFile(filename);

            if (deleted) {
                this.response = new HttpResponse()
                        .status(HttpStatus.OK)
                        .contentType("text/plain")
                        .body(("File '" + filename + "' deleted successfully").getBytes())
                        .build();
            } else {
                this.response = new HttpResponse()
                        .status(HttpStatus.NOT_FOUND)
                        .contentType("text/plain")
                        .body(("File '" + filename + "' not found").getBytes())
                        .build();
            }
            break;

        case INDEX_HTML:
        case FILE_NAMES_INFO:
        case ECHO:
        case EMPTY:
            this.response = new HttpResponse()
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .contentType("text/plain")
                    .body("DELETE not allowed on this endpoint".getBytes())
                    .build();
            break;

        case INVALID:
            this.response = new HttpResponse()
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType("text/plain")
                    .body("Invalid URL".getBytes())
                    .build();
            break;

        default:
            this.response = new HttpResponse()
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType("text/plain")
                    .body("Unhandled DELETE route".getBytes())
                    .build();
            break;
    }
}


    public void sendResponse() {
        try {
            this.response.write(this.clientOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

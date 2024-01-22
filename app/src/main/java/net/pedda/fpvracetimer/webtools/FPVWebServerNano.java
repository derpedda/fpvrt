package net.pedda.fpvracetimer.webtools;

import fi.iki.elonen.NanoHTTPD;

public class FPVWebServerNano extends NanoHTTPD {


    public FPVWebServerNano(int port) {
        super(port);
    }


    @Override
    public Response serve(IHTTPSession session) {
        String path = session.getUri();
        switch (session.getMethod()) {
            case GET:
                // GET-Request
                // we need these endpoints: /list, /clear, /results and /
                switch (path) {
                    case "/list":
                        return servePathList();
                    case "/clear":
                        return servePathClear();
                    case "/results":
                        return servePathResults();
                    case "/":
                        return servePathIndex();
                    default:
                        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/html","<html><body><h1>404 not found</h1></body></html>");
                }
            case POST:
                // POST request
                return newFixedLengthResponse(Response.Status.NOT_IMPLEMENTED, "application/text", "Currently not implemented");
            default:
                // answer 405 method not allowed
                return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "application/text", "Method not allowed");
        }

    }


    private Response servePathList(){
        return newFixedLengthResponse("");
    }
    private Response servePathResults(){
        return newFixedLengthResponse("");
    }
    private Response servePathClear(){
        return newFixedLengthResponse("");
    }
    private Response servePathIndex(){
        return newFixedLengthResponse("");
    }

}

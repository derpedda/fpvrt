package net.pedda.fpvracetimer.webtools;

import android.content.Context;
import android.content.res.Resources;

import com.hubspot.jinjava.Jinjava;

import net.pedda.fpvracetimer.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class FPVWebServerNano extends NanoHTTPD {

    Context ctx;


    public FPVWebServerNano(int port, Context ctx) {
        super(port);
        this.ctx = ctx;
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
        Resources res = ctx.getResources();
        String s = res.getString(R.string.webtemplate_index);
        Jinjava jinjava = new Jinjava();
        HashMap<String, List<String>> context = new HashMap<String, List<String>>();
        LinkedList<String> results = new LinkedList<String>();
        results.add("Testresult");
        context.put("results", results);
        String response = jinjava.render(s, context);
        return newFixedLengthResponse(response);
    }

}

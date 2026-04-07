package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import org.json.JSONArray;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebServiceDAO {
    private final String TAG = "WebServiceDAO";
    private final DAOFactory daoFactory;
    private final String HTTP_METHOD = "GET";
    private final String ROOT_URL = "http://ec2-3-142-171-53.us-east-2.compute.amazonaws.com:8080/CrosswordMagicServer/puzzle";
    private String requestUrl;
    private ExecutorService pool;

    WebServiceDAO(DAOFactory daoFactory) { this.daoFactory = daoFactory; }

    public JSONArray list() {
        requestUrl = ROOT_URL;
        JSONArray result = null;
        try {
            pool = Executors.newSingleThreadExecutor();
            Future<String> pending = pool.submit(new CallableHTTPRequest());
            String response = pending.get();
            pool.shutdown();
            result = new JSONArray(response);
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public class CallableHTTPRequest implements Callable<String> {
        @Override
        public String call() {
            StringBuilder r = new StringBuilder();
            // ... adapt Runnable code from "Web Service Demo" here
            return r.toString().trim();
        }
    }

}

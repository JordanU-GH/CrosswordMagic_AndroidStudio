package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class WebServiceDAO {
    private final String TAG = "WebServiceDAO";
    private final DAOFactory daoFactory;
    private final String HTTP_METHOD = "GET";
    private final String ROOT_URL = "http://ec2-3-137-195-31.us-east-2.compute.amazonaws.com:8080/CrosswordMagicServer/puzzle";
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

    public JSONObject getPuzzle(int puzzleId){
        requestUrl = ROOT_URL + "?id=" + puzzleId;
        JSONObject result = null;
        try {
            pool = Executors.newSingleThreadExecutor();
            Future<String> pending = pool.submit(new CallableHTTPRequest());
            String response = pending.get();
            pool.shutdown();
            result = new JSONObject(response);
        }
        catch (Exception e) { e.printStackTrace(); }
        return result;
    }

    public class CallableHTTPRequest implements Callable<String> {
        @Override
        public String call() {
            StringBuilder r = new StringBuilder();
            // ... adapt Runnable code from "Web Service Demo" here
            String line;
            HttpURLConnection conn = null;
            Object results = null;

            /* Log Request Data */
            try {
                /* Check if task has been interrupted */
                if (Thread.interrupted())
                    throw new InterruptedException();

                /* Create Request */
                URL url = new URL(requestUrl);

                conn = (HttpURLConnection)url.openConnection();

                conn.setReadTimeout(10000); /* ten seconds */
                conn.setConnectTimeout(15000); /* fifteen seconds */

                conn.setRequestMethod(HTTP_METHOD);
                conn.setDoInput(true);

                /* Send Request */
                conn.connect();

                /* Check if task has been interrupted */
                if (Thread.interrupted())
                    throw new InterruptedException();

                /* Get Reader for Results */
                int code = conn.getResponseCode();

                if (code == HttpsURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    /* Read Response Into StringBuilder */
                    do {
                        line = reader.readLine();
                        if (line != null)
                            r.append(line);
                    }
                    while (line != null);

                }

                /* Check if task has been interrupted */
                if (Thread.interrupted())
                    throw new InterruptedException();

                /* Parse Response as JSON */
                String response = r.toString().trim();
                if (response.startsWith("{")){
                    results = new JSONObject(r.toString());
                }
                else if (response.startsWith("[")){
                    results = new JSONArray(r.toString());
                }
                else{
                    Log.i("Error", "response In WebServiceDAO.call() is neither a JSONArray nor JSONObject");
                }
                r.append(results);

            }
            catch (Exception e) {
                Log.e(TAG, " Exception: ", e);
            }
            finally {
                if (conn != null) { conn.disconnect(); }
            }

            /* Finished; Log and Return Results */
            Log.d(TAG, " JSON: " + r.toString());
            return r.toString().trim();
        }
    }

}

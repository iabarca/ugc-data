
package com.github.iabarca.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector {

    public static class ConnectorException extends Exception {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private int code;

        public ConnectorException(int code) {
            super("Response status: " + code);
            this.code = code;
        }

        public int getCode() {
            return code;
        }

    }

    private static final Logger log = Logger.getLogger("stats");

    private int timeout = 10000;
    private int maxAttempts = 3;
    private int requestDelay = 250;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxRetries) {
        this.maxAttempts = maxRetries;
    }

    public int getRequestDelay() {
        return requestDelay;
    }

    public void setRequestDelay(int requestDelay) {
        this.requestDelay = requestDelay;
    }

    public String getJson(String url) throws ConnectorException {
        return getJSON(url, maxAttempts);
    }

    public String getJSON(String url, int maxRetries) throws ConnectorException {
        String result = null;
        int retryCount = 0;
        while (result == null && retryCount < maxRetries) {
            log.info("GET " + url
                    + (retryCount > 0 ? " (" + (retryCount + 1) + "/" + maxRetries + ")" : ""));
            result = getJSONAttempt(url);
            retryCount++;
        }
        return result;
    }

    private String getJSONAttempt(String url) throws ConnectorException {
        try {
            Thread.sleep(requestDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
                case 403:
                case 404:
                    throw new ConnectorException(status);
            }
        } catch (MalformedURLException ex) {
            log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

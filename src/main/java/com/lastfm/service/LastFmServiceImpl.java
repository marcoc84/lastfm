package com.lastfm.service;

import com.lastfm.properties.LastFmProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LastFmServiceImpl implements LastFmService {

    private final static Logger logger = Logger.getLogger(LastFmServiceImpl.class);

    private static final String API_ARTISTS_URL_KEY = "api.artists.url";
    private static final String API_TRACKS_URL_KEY = "api.tracks.url";
    private static final String API_KEY_KEY = "api.key";
    private static final int CONNECTION_TIMEOUT_LIMIT = 5000;
    private static final int MAX_CONNECTIONS = 10;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 5;
    private static final int MAX_REQUESTS_TRIES = 3;
    private static final int HTTP_WAIT = 1000;

    private HttpClient client;

    private LastFmProperties properties;

    public LastFmServiceImpl() {
        properties = LastFmProperties.getInstance();
    }

    public LastFmServiceImpl(LastFmProperties properties, HttpClient client) {
        this.properties = properties;
        this.client = client;
    }

    @Override
    public final String getPopularArtists(String country, String page) {

        String apiUrl = properties.get(API_ARTISTS_URL_KEY);
        String apiKey = properties.get(API_KEY_KEY);
        String formattedApiUrl = String.format(apiUrl, country, apiKey, page);


        return getJsonResponse(formattedApiUrl);
    }

    @Override
    public final String getTopTracks(String name) {
        String apiUrl = properties.get(API_TRACKS_URL_KEY);
        String apiKey = properties.get(API_KEY_KEY);
        String formattedApiUrl = null;
        try {
            formattedApiUrl = String.format(apiUrl, URLEncoder.encode(name, "UTF-8"), apiKey);
        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException: " + e.getMessage(), e);
        }

        return getJsonResponse(formattedApiUrl);
    }

    private final String getJsonResponse(String urlString) {
        HttpGet get = new HttpGet(urlString);

        HttpResponse response = executeHttpMethod(get);

        if (response != null) {
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                logger.error("IOException: " + e.getMessage(), e);
            } finally {
                get.releaseConnection();
            }
        }

        return null;
    }

    private HttpResponse executeHttpMethod(HttpRequestBase method) {
        int tries = MAX_REQUESTS_TRIES;

        while (tries > 0) {
            try {
                return getHttpClient().execute(method);
            } catch (IOException e) {
                if (e instanceof ConnectTimeoutException || e instanceof NoHttpResponseException) {
                    tries--;
                    try {
                        Thread.sleep(HTTP_WAIT);
                    } catch (InterruptedException exp) {
                        logger.error("Failed to interrupt the thread: " + exp.getMessage(), exp);
                    }
                }
                if ((!(e instanceof ConnectTimeoutException) && !(e instanceof NoHttpResponseException))
                        || tries == 0) {
                    logger.error("Failed to execute HTTP method.", e);
                }
            }
        }

        return null;
    }

    private HttpClient getHttpClient() {
        if (client == null) {
            client = buildHttpClient();
        }

        return client;
    }

    private HttpClient buildHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(CONNECTION_TIMEOUT_LIMIT).
                setConnectTimeout(CONNECTION_TIMEOUT_LIMIT).setConnectionRequestTimeout(CONNECTION_TIMEOUT_LIMIT).build();

        HttpClientBuilder clientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
        clientBuilder.setMaxConnTotal(MAX_CONNECTIONS);
        clientBuilder.setMaxConnPerRoute(MAX_CONNECTIONS_PER_ROUTE);

        return clientBuilder.build();
    }
}

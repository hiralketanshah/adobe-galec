package com.societecooperativegroupements.core.schedulers;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.societecooperativegroupements.core.models.alkemics.Alkemics;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  Bilal T
 */
public class AlkemicsAssetImporterUtils {

    private static final Logger logger = LoggerFactory.getLogger(AlkemicsAssetImporterUtils.class);

    private static final Gson gson = new Gson();

    /**
     * Method will connect to Alkemics over HTTP API and get list of Products for the gievn filter.
     *
     * @param alkemicsProductUrl
     * @param additionalParams
     * @param accessToken
     * @param httpClient
     * @return Alkemics Product List
     */
    public static Alkemics retrieveAlkemicsProductList(String alkemicsProductUrl, Map<String, Object> additionalParams, String accessToken, CloseableHttpClient httpClient) {
        try {
            CloseableHttpResponse response = null;
            HttpGet httpGet = new HttpGet(alkemicsProductUrl);
            List nameValuePairs = new ArrayList();
            nameValuePairs.add(new BasicNameValuePair("filter_source_include", "gtin,uuid,assets.pictures,assets.lastUpdatedAt.pictures,supplierId,isDisplayUnit,isConsumerUnit,namePublicLong.data"));
            if ((additionalParams != null) && (!additionalParams.isEmpty())) {
                for (Map.Entry<String, Object> entry : additionalParams.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
            }
            URI uri = new URIBuilder(httpGet.getURI())
                    .addParameters(nameValuePairs)
                    .build();
            ((HttpRequestBase) httpGet).setURI(uri);
            httpGet.addHeader("Authorization", "Bearer " + accessToken);
            response = httpClient.execute(httpGet);
            return (Alkemics) gson.fromJson(EntityUtils.toString(response.getEntity()), Alkemics.class);
        } catch (URISyntaxException | IOException e) {
            logger.error("Error while connecting to Alkemices for Product List", e);
        }
        return null;
    }

    /**
     * Method will retrive access token from Alkemics.
     *
     * @param alkemicsTokenUrl
     * @param clientId
     * @param clientSecret
     * @param httpClient
     * @return Access token.
     */
    public static String getAccessToken(String alkemicsTokenUrl, String clientId, String clientSecret, CloseableHttpClient httpClient) {
        HttpPost post = new HttpPost(alkemicsTokenUrl);
        HashMap<String, String> map = new HashMap();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", "client_credentials");
        try {
            post.setEntity(new StringEntity(gson.toJson(map)));
            CloseableHttpResponse response = httpClient.execute(post);
            HashMap<String, String> resultMap = (HashMap) gson.fromJson(EntityUtils.toString(response.getEntity()), HashMap.class);
            return (String) resultMap.get("access_token");
        } catch (UnsupportedEncodingException e) {
            logger.error("Error when getting the secret", e.getMessage());
        } catch (ClientProtocolException e) {
            logger.error("Error when getting the secret", e.getMessage());
        } catch (IOException e) {
            logger.error("Error when getting the secret", e.getMessage());
        } catch (JsonSyntaxException e) {
            logger.error("Error when getting the secret", e.getMessage());
        } catch (org.apache.http.ParseException e) {
            logger.error("Error when getting the secret", e.getMessage());
        } finally {
            logger.info("End");
        }
        return null;
    }
}

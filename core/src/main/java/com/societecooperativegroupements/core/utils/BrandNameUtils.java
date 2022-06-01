package com.societecooperativegroupements.core.utils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.societecooperativegroupements.core.models.alkemics.AkDatum;
import com.societecooperativegroupements.core.models.alkemics.Alkemics;
import com.societecooperativegroupements.core.models.alkemics.Document;
import com.societecooperativegroupements.core.models.alkemics.DocumentTypeCode;
import com.societecooperativegroupements.core.models.alkemics.NamePublicLong;
import com.societecooperativegroupements.core.models.alkemics.Picture;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class BrandNameUtils {

    private static final Logger logger = LoggerFactory.getLogger(BrandNameUtils.class);

    private static final Gson gson = new Gson();
    
    public static String getBrandName(String accessToken, String gtin, CloseableHttpClient httpClient) {
        return getBrandCode(accessToken, gtin, httpClient);
    }

    public static String getBrandCode(String accessToken, String gtin, CloseableHttpClient httpClient) {
        HttpPost post = new HttpPost("https://apis.alkemics.com/public/v1/products/list");
        String code = StringUtils.EMPTY;
        HashMap<String, Object> map = new HashMap();
        map.put("filter_gtins_in", "03178041265140");
        map.put("allow_not_consumer_units", true);
        map.put("limit", "200");
        try {
            post.setEntity(new StringEntity(gson.toJson(map)));
            post.addHeader("Authorization", "Bearer " + accessToken);
            CloseableHttpResponse response = httpClient.execute(post);
            HashMap<String, List<Object>> resultMap = (HashMap) gson.fromJson(EntityUtils.toString(response.getEntity()),
                    HashMap.class);
            ObjectMapper oMapper = new ObjectMapper();
            List<Object> obj = resultMap.get("data");
            for(Object test : obj) {
                HashMap<String, HashMap<String, String>> parent = oMapper.convertValue(test, HashMap.class);
                HashMap<String, String> brand = parent.get("brand");
                code = String.valueOf(brand.get("code"));
            }
            
        } catch (IOException | JsonSyntaxException e) {
            logger.error("Error when getting the brand code", e.getMessage());
        } finally {
            logger.info("End");
        }
        return code;
    }
}

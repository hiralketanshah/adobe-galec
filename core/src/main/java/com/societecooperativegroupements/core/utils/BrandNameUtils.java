package com.societecooperativegroupements.core.utils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    public static String getBrandName(Integer code, String gtin, CloseableHttpClient httpClient) {
        return getBrandDataValue(code, gtin, httpClient);
    }

    private static String getBrandDataValue(Integer code, String gtin, CloseableHttpClient httpClient) {
        CloseableHttpResponse response = null;
        String val = StringUtils.EMPTY;
        try {

            HttpGet httpGet = new HttpGet(
                    "https://api-codification.referentiel.galec.fr/referentiel/v3/codification/transcodification?filter[transcodification.valeur_1]=ega!"
                            + code
                            + "instance=dd77461f-e12c-40de-83c8-3166ef1a70cd&langue=dd77461f-e12c-40de-83c8-3166ef1a70cd");
            URI uri = new URIBuilder(httpGet.getURI()).build();
            ((HttpRequestBase) httpGet).setURI(uri);

            response = httpClient.execute(httpGet);
            

            JsonObject mapOfObjects = gson.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
            JsonArray data = mapOfObjects.getAsJsonArray("data");

            for (JsonElement element : data) {
                JsonObject brandObj = element.getAsJsonObject();
                if (brandObj.has("valeur_2")) {
                    val = brandObj.get("valeur_2").getAsString();
                }
            }
            String libelle = getLibelle(val, httpClient);

            return libelle;
        } catch (IOException | URISyntaxException e) {
            logger.error("Error while fetching brand name, {}", e.getMessage());
        }
        return val;
    }

    private static String getLibelle(String val, CloseableHttpClient httpClient)
            throws URISyntaxException, ClientProtocolException, IOException {
        String libelle = StringUtils.EMPTY;
        CloseableHttpResponse response = null;
        if (null != val && !val.equalsIgnoreCase(StringUtils.EMPTY)) {

            HttpGet httpGetLibelle = new HttpGet(
                    "https://api-codification.referentiel.galec.fr/referentiel/v3/codification/marque?filter[marque.identifiant_unique_marque]=ega!"
                            + val
                            + "&instance=dd77461f-e12c-40de-83c8-3166ef1a70cd&langue=dd77461f-e12c-40de-83c8-3166ef1a70cd");
            URI uriLibelle = new URIBuilder(httpGetLibelle.getURI()).build();
            ((HttpRequestBase) httpGetLibelle).setURI(uriLibelle);

            response = httpClient.execute(httpGetLibelle);

            JsonObject mapOfObjectsLibelle = gson.fromJson(EntityUtils.toString(response.getEntity()),
                    JsonObject.class);
            JsonArray dataLibelle = mapOfObjectsLibelle.getAsJsonArray("data");

            for (JsonElement element : dataLibelle) {
                JsonObject libelleObj = element.getAsJsonObject();
                if(libelleObj.has("libelle")) {
                    libelle = libelleObj.get("libelle").getAsString();
                }
            }
        }
        return libelle;
    }
}

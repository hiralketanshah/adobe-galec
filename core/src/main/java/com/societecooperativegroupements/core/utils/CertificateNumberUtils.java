package com.societecooperativegroupements.core.utils;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
import java.text.DateFormat;
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

public class CertificateNumberUtils {

    private static final Logger logger = LoggerFactory.getLogger(CertificateNumberUtils.class);

    private static final Gson gson = new Gson();

    public static String getCertificateDetails(String accessToken, String gtin, CloseableHttpClient httpClient) {
        HttpPost post = new HttpPost("https://apis.alkemics.com/public/v1/products/list");
        String code = StringUtils.EMPTY;
        String certificateValue = StringUtils.EMPTY;
        String certificateEndDateString = StringUtils.EMPTY;
        HashMap<String, Object> map = new HashMap();
        map.put("filter_gtins_in", gtin);
        map.put("allow_not_consumer_units", true);
        map.put("limit", "200");
        try {
            post.setEntity(new StringEntity(gson.toJson(map)));
            post.addHeader("Authorization", "Bearer " + accessToken);
            CloseableHttpResponse response = httpClient.execute(post);

            JsonObject mapOfObjects = gson.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);

            boolean error = true;
            if (null != mapOfObjects && null != mapOfObjects.getAsJsonArray("data")) {
                JsonArray data = mapOfObjects.getAsJsonArray("data");
                for (JsonElement dataElement : data) {
                    JsonObject certificateInfoListObj = dataElement.getAsJsonObject();
                    JsonArray certificateInfoListArray = certificateInfoListObj
                            .getAsJsonArray("certificationInformationList");
                    if (null != certificateInfoListArray) {
                        for (JsonElement certificateInfoListElement : certificateInfoListArray) {
                            JsonObject certificateDetailListObj = certificateInfoListElement.getAsJsonObject();
                            JsonArray certificateDetailListArray = certificateDetailListObj
                                    .getAsJsonArray("certificationDetailsList");
                            if (null != certificateDetailListArray) {
                                for (JsonElement certificateDetailListElement : certificateDetailListArray) {
                                    JsonObject certificateObj = certificateDetailListElement.getAsJsonObject();
                                    if (certificateObj.has("certificationValueText")
                                            && certificateObj.has("certificationEffectiveEndDate")) {
                                        certificateValue = certificateObj.get("certificationValueText") == JsonNull.INSTANCE ? null : certificateObj.get("certificationValueText").getAsString();
                                        long certificateEndDate = certificateObj.get("certificationEffectiveEndDate") == null ? 0L : certificateObj.get("certificationEffectiveEndDate").getAsLong();
                                        if (null != certificateValue) {
                                            certificateValue = getUpdatedCertificateValue(certificateValue);
                                            certificateEndDateString = getUpdatedCertificateEndDate(certificateEndDate);
                                            error = false;
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }
            if (error) {
                code = "error";
            } else {
                code = certificateValue + "_" + certificateEndDateString;
            }

        } catch (IOException | JsonSyntaxException e) {
            logger.error("Error when getting the brand code", e.getMessage());
        } finally {
            logger.info("End");
        }
        return code;
    }

    private static String getUpdatedCertificateEndDate(long certificateEndDate) {
        if(certificateEndDate == 0L) {
            return "01011900";
        }
        Date d = new Date((long)certificateEndDate*1000);
        DateFormat f = new SimpleDateFormat("ddMMYYYY");
        return f.format(d);
    }

    private static String getUpdatedCertificateValue(String certificateValue) {
        if (!certificateValue.equalsIgnoreCase(StringUtils.EMPTY)) {
            certificateValue = certificateValue.replace("'", "_").replace("/", "").replace("&", "").replace(":", "").replace("°", "")
                    .replace(",", "").replace("#", "").replace(".", "").replace("-", "").replace(" ", "_")
                    .replace("Æ", "AE").replace("æ", "AE").replace("Œ", "OE").replace("œ", "OE");
        }
        return certificateValue;
    }
}

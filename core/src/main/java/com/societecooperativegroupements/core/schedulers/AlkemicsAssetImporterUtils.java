package com.societecooperativegroupements.core.schedulers;


import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.societecooperativegroupements.core.models.alkemics.AkDatum;
import com.societecooperativegroupements.core.models.alkemics.Alkemics;
import com.societecooperativegroupements.core.models.alkemics.NamePublicLong;
import com.societecooperativegroupements.core.models.alkemics.Picture;

import org.apache.commons.io.FilenameUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
            nameValuePairs.add(new BasicNameValuePair("filter_source_include", "gtin,uuid,assets.pictures,assets.lastUpdatedAt.pictures,supplierId,assets.documents,isDisplayUnit,isConsumerUnit,namePublicLong.data"));
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
    
    /**
     * Asset import process for the day.
     *
     * @param jsonPath
     * @param resolver 
     * @param httpClient 
     * @param clientSecret 
     * @param clientId 
     * @param alkemicsTokenUrl 
     * @param alkemicsProductUrl 
     * @param init 
     * @param endDateEntry 
     * @param startDateEntry 
     * @param dryRun 
     * @param activeAssetResources 
     * @param batchSize 
     * @param waitTime 
     */
    public static void importAsset(String jsonPath, ResourceResolver resolver, String alkemicsTokenUrl, String clientId,
            String clientSecret, CloseableHttpClient httpClient, String alkemicsProductUrl, boolean init,
            String startDateEntry, String endDateEntry, boolean dryRun, List<Resource> activeAssetResources,
            int batchSize, int waitTime) {
        List<AkDatum> listeCurrentProduct = new ArrayList();
        try {
            if (jsonPath != null) {
                readProduct(resolver, jsonPath, listeCurrentProduct);
            } else {
                String accessToken = AlkemicsAssetImporterUtils.getAccessToken(alkemicsTokenUrl, clientId, clientSecret, httpClient);
                Map<String, Object> additionalParams = null;
                //TODO this call seems unwanted.
                Alkemics alkemics = getProductList(accessToken, additionalParams, alkemicsProductUrl, clientId, clientSecret, httpClient);
                if (null != alkemics)
                    logger.info("TOTAL NUMBER OF PRODUCT:" + alkemics.getTotalResults());

                String page = "";

                Calendar calendar = Calendar.getInstance();

                long numberProcessed = 0L;
                long numberAsset = 0L;

                logger.info("STARTING TO READ PRODUCT DAY BY DAY");


                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date endDate = null;
                Date startDate = null;
                if (init) {
                    if (endDateEntry == null)
                        //TODO use from config
                        endDateEntry = "01-07-2020";
                    if (startDateEntry == null)
                        startDateEntry = "28-06-2019";

                    endDate = format.parse(endDateEntry);

                    startDate = format.parse(startDateEntry);

                } else {
                    endDate = new java.util.Date();

                    calendar.setTime(endDate);
                    calendar.add(Calendar.DATE, -1);
                    startDate = calendar.getTime();
                }

                while (endDate.compareTo(startDate) > 0) {
                    List<AkDatum> listeProduit = new ArrayList();
                    page = "";
                    String productAccessToken = AlkemicsAssetImporterUtils.getAccessToken(alkemicsTokenUrl, clientId, clientSecret, httpClient);
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, -1);
                    Date currentDate = calendar.getTime();

                    logger.info("PRODUCT UNDER PROCESSING BETWEEN:" + startDate + "ET LE" + currentDate);
                    readProductByDay(startDate, productAccessToken, page, numberProcessed, listeProduit, alkemicsProductUrl, clientId, clientSecret, httpClient);
                    numberAsset += listeProduit.size();

                    if (!dryRun) {
                        int max = listeProduit.size();
                        Map<String, String> hm = null;
                        for (int i = 0; i < max; i++) {
                            AkDatum currentProduct = (AkDatum) listeProduit.get(i);
                            hm = new HashMap();
                            if (currentProduct.getAssets() != null) {
                                hm.put("gtin", currentProduct.getGtin());
                                String gtin = currentProduct.getGtin();
                                String uuid = currentProduct.getUuid();
                                List<NamePublicLong> aname = currentProduct.getNamePublicLong();
                                String nomProduit = null;
                                String supplierId = currentProduct.getSupplierId();
                                boolean errorCase = false;
                                if (aname != null) {
                                    for (int k = 0; k < aname.size(); k++) {
                                        nomProduit = ((NamePublicLong) aname.get(k)).getData();
                                    }
                                }
                                if ((null == uuid) || (uuid.equals("")) || (null == gtin) || (gtin.equals("")) || (null == nomProduit) ||
                                        (nomProduit.equals("")) || (null == supplierId) ||
                                        (supplierId.equals(""))) {
                                    errorCase = true;
                                }
                                Boolean isConsumerUnit = currentProduct.getIsConsumerUnit();
                                Boolean isDisplayUnit = currentProduct.getIsDisplayUnit();
                                String categorie = "UC";
                                if ((null != isConsumerUnit) && (isConsumerUnit.booleanValue())) {
                                    categorie = "UC";
                                } else if ((null != isConsumerUnit) && (!isConsumerUnit.booleanValue()) && (null != isDisplayUnit) &&
                                        (isDisplayUnit.booleanValue())) {
                                    categorie = "BOX";
                                } else if ((null != isConsumerUnit) && (!isConsumerUnit.booleanValue()) && (null != isDisplayUnit) &&
                                        (!isDisplayUnit.booleanValue())) {
                                    categorie = "UL";
                                } else {
                                    errorCase = true;
                                }
                                List<Picture> assetPictureList = currentProduct.getAssets().getPictures();
                                int numberPicture = assetPictureList.size();
                                for (int j = 0; j < numberPicture; j++) {
                                    String assetUrl = ((Picture) currentProduct.getAssets().getPictures().get(j)).getUrl();
                                    if ((null != assetUrl) && (!assetUrl.equals(""))) {

                                        Picture productAsset = (Picture) currentProduct.getAssets().getPictures().get(j);

                                        String name = categorie + "_" + uuid + "_" + productAsset.getGdsnFileName();

                                        hm.put("url", assetUrl);
                                        hm.put("uuid", uuid);

                                        hm.put("supplierId", supplierId);
                                        hm.put("updatedAt", productAsset.getUpdatedAt());
                                        hm.put("categorie", categorie);
                                        hm.put("libelle-produit", nomProduit);
                                        hm.put("packshot-par-defaut", productAsset.getIsPackshot().toString());

                                        hm.put("gdsnFileName", productAsset.getGdsnFileName());
                                        if ((null == productAsset.getGdsnFileName()) ||
                                                (productAsset.getGdsnFileName().equals(""))) {
                                            errorCase = true;
                                        }
                                        hm.put("name", name);
                                        if (productAsset.getProductFace() != null) {
                                            hm.put("vue", productAsset.getProductFace().toString());
                                        }
                                        hm.put("date-debut-validite", productAsset.getFileEffectiveStartDateTime());
                                        hm.put("date-fin-validite", productAsset.getFileEffectiveEndDateTime());

                                        hm.put("source", "Alkemics");
                                        hm.put("usage-autorise", "Interne");
                                        hm.put("nom-fichier-alkemics", FilenameUtils.getName(assetUrl));

                                        logger.info(" url: " + assetUrl);

                                        try {
                                            writeToDam(resolver, name, gtin, assetUrl, hm, errorCase, activeAssetResources, batchSize, waitTime, true);
                                        } catch (InterruptedException e) {
                                            logger.error("error while writing to dam" + e.getMessage());
                                        }

                                    } else {
                                        logger.warn("File URL Error " + assetUrl);
                                    }
                                }
                            }
                        }


                    } else {
                        logger.info("DRY RUN: running by day");

                    }
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, 1);
                    startDate = calendar.getTime();
                    logger.info("TOTAL NUMBER OF ASSETS PROCESSED: " + numberAsset);

                }

            }
        } catch (java.text.ParseException e) {
            logger.error("Error when parsing", e.getMessage());
        }
    }
    
    /**
     * Method will form a path for asset and write to DAM.
     * @param resolver 
     *
     * @param name      the name of the asset.
     * @param gtin
     * @param path
     * @param meta
     * @param errorCase
     * @param activeAssetResources 
     * @param batchSize 
     * @param waitTime 
     * @throws InterruptedException
     */
    private static void writeToDam(ResourceResolver resolver, String name, String gtin, String path, Map<String, String> meta, boolean errorCase, List<Resource> activeAssetResources, int batchSize, int waitTime, boolean isPicture)
            throws InterruptedException {
        if (name.equals("UC_null")) {
            logger.warn("NAME OF THE ASSET ASSET UC_NULL");

        } else {
            logger.info("WRITING ASSET:" + name);
            InputStream inputStream = null;
           
            Session adminSession = null;
            try {
                inputStream = new URL(path).openStream();

                adminSession = (Session) resolver.adaptTo(Session.class);

                AssetManager assetMgr = (AssetManager) resolver.adaptTo(AssetManager.class);

                String hierarchy = "";
                if ((gtin != null) && (!gtin.equals("")) &&
                        (gtin.length() > 0)) {
                    String folderlevel1 = gtin.substring(0, 8);
                    String folderlevel2 = gtin.substring(8, 13);
                    String folderlevel3 = gtin.substring(13, 14);
                    hierarchy = folderlevel1 + "/" + folderlevel2 + "/" + folderlevel3 + "/";
                    logger.info("folder hierarchy" + hierarchy);
                }
                String newFile = "/content/dam/dam/Usage-Interne/Produits/" + hierarchy + name;
                if (errorCase) {
                    newFile = "/content/dam/dam/Usage-Interne/Erreur-Produits/" + hierarchy + name;
                }
                Resource assetExist = resolver.getResource(newFile);
                if (assetExist != null) {
                    logger.info("CET ASSET EXISTE DEJA");
                }
                long startTime = new Date().getTime();
                
                   Asset a =   assetMgr.createAsset(newFile, inputStream, "image/jpeg", true);
                

                long endTime = new Date().getTime();

                long timeElapsed = endTime - startTime;
                logger.info("TEMPS D'INJECTION DANS AEM " + timeElapsed + "ms");

                Node contentNode = adminSession.getNode(newFile + "/" + "jcr:content");
                Node metaNode = contentNode.getNode("metadata");
                Resource assetResource = resolver.getResource(newFile);

               logger.info("MISE A JOUR DESMETADONNES PRODUITS");
                for (Map.Entry<String, String> entry : meta.entrySet()) {
                    metaNode.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
                resolver.commit();
                adminSession.save();
                activeAssetResources.add(assetResource);

                if (activeAssetResources.size() >= batchSize) {
                    logger.info("WAITING WORKFLOW PROCESS");

                    Thread.sleep(waitTime);
                    activeAssetResources.clear();

                }
            } catch (InterruptedException | RepositoryException | IOException e) {
                logger.error("Error while writing assets to DAM" + e.getMessage());
               
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                       logger.error("Error while getting resolve" + e.getMessage());
                    }
                }
               
            }
        }
    }
    
    private static void readProduct(ResourceResolver resolver, String jsonpath, List<AkDatum> currentProducts) {
        ObjectMapper mapper = new ObjectMapper();   
        try {
            Resource res = resolver.getResource(jsonpath);
            if (res != null) {
                Asset asset = res.adaptTo(Asset.class);

                Rendition rendition = asset.getOriginal();
                InputStream inputStream = rendition.adaptTo(InputStream.class);

                Alkemics obj;

                obj = mapper.readValue(inputStream, Alkemics.class);
                currentProducts.addAll(obj.getData());
            }
        } catch (IOException e) {
            logger.error("Error  " + e.getMessage());
        } 
    }
    
  /*  * Method will retriev assets from Alkemics uploaded on current day.
    *
    * @param startDate
    * @param access_token
    * @param page
    * @param numberProcessed
    * @param listeProduit
    */
   private static void readProductByDay(Date startDate, String accessToken, String page, long numberProcessed,
                                 List<AkDatum> listeProduit, String alkemicsProductUrl, String clientId, String clientSecret, CloseableHttpClient httpClient) {

       int max = 0;
       List<AkDatum> returnedProduct = null;
       logger.info("Product in process" + startDate);
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(startDate);
       long startime = startDate.getTime();


       calendar.add(Calendar.DATE, 1);
       Date dayAfter = calendar.getTime();
       long endtime = dayAfter.getTime();

       do {
           int limit = 500;


           HashMap<String, Object> additionalParams = new HashMap<>();
           additionalParams.put("filter_pictures_last_updated_at_from", startime);
           additionalParams.put("filter_pictures_last_updated_at_to", endtime);
           additionalParams.put("limit", limit);
           additionalParams.put("next_page", page);

           Alkemics obj = getProductList(accessToken, additionalParams, alkemicsProductUrl, clientId, clientSecret, httpClient);
           if (null != obj) {


               if (null != obj.getData()) {
                   page = obj.getNextPage();

                   max = max + obj.getData().size();
                   returnedProduct = obj.getData();
                   if (max > 0) {

                       for (int i = 0; i < obj.getData().size(); i++) {
                           AkDatum currentProduct = obj.getData().get(i);

                           if (currentProduct.getAssets() != null) {
                               listeProduit.add(currentProduct);

                           } else {
                               logger.debug("No Asset found for the product" + currentProduct.getGtin());
                           }

                       }
                       numberProcessed = numberProcessed + max;
                       logger.debug(
                               "NUMBER OF PRODUCTS PROCESSED: " + numberProcessed + "ON A TOTAL " + obj.getTotalResults());
                       logger.debug("NUMBER OF PRODUCTS WITH IMAGES " + listeProduit.size());

                       readProductByDay(startDate, accessToken, page, numberProcessed, listeProduit, alkemicsProductUrl, clientId, clientSecret, httpClient);

                   }
               }
           }
           break;

       } while (page.equals("") && !returnedProduct.isEmpty());

   }
    
    /**
     * Get List of Asset from Alkemics.
     *
     * @param accessToken
     * @param additionalParams
     * @param httpClient 
     * @param clientSecret 
     * @param clientId 
     * @param alkemicsTokenUrl 
     * @return Alkemics.
     */
    public static Alkemics getProductList(String accessToken, Map<String, Object> additionalParams, String alkemicsProductUrl, String clientId, String clientSecret, CloseableHttpClient httpClient) {
        Alkemics result = null;

        try {
            result = AlkemicsAssetImporterUtils.retrieveAlkemicsProductList(alkemicsProductUrl, additionalParams, accessToken, httpClient);
        } catch (Exception e) {
            logger.error("Error IOException " + e.getMessage());
        } finally {
            logger.info("End GET PRODUCT BY 500");
            if (result == null) {
                try {
                    result = AlkemicsAssetImporterUtils.retrieveAlkemicsProductList(alkemicsProductUrl,
                            additionalParams, accessToken, httpClient);
                } catch (JsonSyntaxException e) {
                    logger.error("Error JsonSyntaxException " + e.getMessage());

                }

            }
        }
        return result;
    }
}

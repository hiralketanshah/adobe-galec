package com.societecooperativegroupements.core.schedulers;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.societecooperativegroupements.core.models.AlkemicsAssetImporterConfiguration;
import com.societecooperativegroupements.core.models.alkemics.AkDatum;
import com.societecooperativegroupements.core.models.alkemics.Alkemics;
import com.societecooperativegroupements.core.models.alkemics.NamePublicLong;
import com.societecooperativegroupements.core.models.alkemics.Picture;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = AlkemicsAssetImporterConfiguration.class)
@Component(service = Runnable.class)
public class AlkemicsAssetImporter
        implements Runnable {
    private String alkemicsProductUrl;
    private String alkemicsTokenUrl;
    private String clientId;
    private String clientSecret;
    private boolean init;
    private boolean dryRun;
    private int batchSize;
    private int waitTime;
    private String endDateEntry;
    private String startDateEntry;
    private List<Resource> activeAssetResources;
    private CloseableHttpClient httpClient;
    private static final Gson gson = new Gson();
    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;
    @Reference
    private ResourceResolverFactory resolverFactory;

    public void run() {
        this.logger.info("***************************************************************************");
        this.logger.info("AlkemicsAssetImporter is now running, alkemicsUrl='{}'", this.alkemicsProductUrl);
        this.logger.info("***************************************************************************");
        importAsset(null);
    }

    @Activate
    @Modified
    protected void activate(AlkemicsAssetImporterConfiguration config) {
        this.logger.info("AlkemicsAssetImporter activation");
        HttpClientBuilder builder = this.httpClientBuilderFactory.newBuilder();

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
        builder.setDefaultRequestConfig(requestConfig);
        this.httpClient = builder.build();

        this.alkemicsProductUrl = config.alkemicsProductUrl();
        this.alkemicsTokenUrl = config.alkemicsTokenUrl();
        this.clientId = config.clientId();
        this.clientSecret = config.clientSecret();
        this.init = config.init();
        this.dryRun = config.dryRun();
        this.batchSize = config.batchSize();
        this.waitTime = config.waitTime();
        this.endDateEntry = config.endDateEntry();

        this.startDateEntry = config.startDateEntry();

        this.activeAssetResources = new ArrayList();
    }


    public static void main(String[] args) {
        AlkemicsAssetImporter akm = new AlkemicsAssetImporter();
        akm.run();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private long Totaltimefor1000 = 0L;

    /**
     * Method will retriev assets from Alkemics uploaded on current day.
     *
     * @param startDate
     * @param access_token
     * @param page
     * @param numberProcessed
     * @param listeProduit
     */
    private void readProductByDay(Date startDate, String access_token, String page, long numberProcessed,
                                  List<AkDatum> listeProduit) {

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

            Alkemics obj = getProductList(access_token, additionalParams);
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

                        readProductByDay(startDate, access_token, page, numberProcessed, listeProduit);

                    }
                }
            }
            break;

        } while (page.equals("") && !returnedProduct.isEmpty());

    }

    private void readProduct(String jsonpath, List<AkDatum> currentProducts) {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "content-writer-service");

        // Invoke the adaptTo method to create a Session used to create a QueryManager
        ResourceResolver resolver = null;
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);

            Resource res = resolver.getResource(jsonpath);
            if (res != null) {
                Asset asset = res.adaptTo(Asset.class);

                Rendition rendition = asset.getOriginal();
                InputStream inputStream = rendition.adaptTo(InputStream.class);

                Alkemics obj;

                obj = mapper.readValue(inputStream, Alkemics.class);
                currentProducts.addAll(obj.getData());
            }
        } catch (JsonParseException e) {
            this.logger.error("Error  " + e.getMessage());
        } catch (JsonMappingException e) {
            this.logger.error("Error  " + e.getMessage());
        } catch (IOException e) {
            this.logger.error("Error  " + e.getMessage());
        } catch (LoginException e1) {
            this.logger.error("Error  " + e1.getMessage());
        } finally {
            if ((resolver != null) && (resolver.isLive())) {
                resolver.close();
            }
        }
    }

    /**
     * Asset import process for the day.
     *
     * @param jsonPath
     */
    public void importAsset(String jsonPath) {
        List<AkDatum> listeCurrentProduct = new ArrayList();
        try {
            if (jsonPath != null) {
                readProduct(jsonPath, listeCurrentProduct);
            } else {
                String accessToken = AlkemicsAssetImporterUtils.getAccessToken(this.alkemicsTokenUrl, this.clientId, this.clientSecret, httpClient);
                Map<String, Object> additionalParams = null;
                //TODO this call seems unwanted.
                Alkemics alkemics = getProductList(accessToken, additionalParams);
                if (null != alkemics)
                    this.logger.info("TOTAL NUMBER OF PRODUCT:" + alkemics.getTotalResults());

                String page = "";

                Calendar calendar = Calendar.getInstance();

                long numberProcessed = 0L;
                long numberAsset = 0L;

                this.logger.info("STARTING TO READ PRODUCT DAY BY DAY");


                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date endDate = null;
                Date startDate = null;
                if (this.init) {
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
                    String productAccessToken = AlkemicsAssetImporterUtils.getAccessToken(this.alkemicsTokenUrl, this.clientId, this.clientSecret, httpClient);;
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, -1);
                    Date currentDate = calendar.getTime();

                    this.logger.info("PRODUCT UNDER PROCESSING BETWEEN:" + startDate + "ET LE" + currentDate);
                    readProductByDay(startDate, productAccessToken, page, numberProcessed, listeProduit);
                    //readProductByDay(endDate, productAccessToken, page, numberProcessed, listeProduit);
                    numberAsset += listeProduit.size();

                    if (!this.dryRun) {
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

                                        String name = categorie + "_" + productAsset.getGdsnFileName();

                                        hm.put("url", assetUrl);
                                        hm.put("uuid", uuid);

                                        hm.put("supplierId", supplierId);
                                        hm.put("updatedAt", productAsset.getUpdatedAt());
                                        hm.put("categorie", categorie);
                                        hm.put("libelle-produit", nomProduit);

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

                                        this.logger.info(" url: " + assetUrl);

                                        try {
                                            writeToDam(name, gtin, assetUrl, hm, errorCase);
                                        } catch (InterruptedException e) {
                                            this.logger.error("error while writing to dam" + e.getMessage());
                                        }

                                    } else {
                                        this.logger.warn("File URL Error " + assetUrl);
                                    }
                                }
                            }
                        }


                    } else {
                        this.logger.info("DRY RUN: running by day");

                    }
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, 1);
                    startDate = calendar.getTime();
                    this.logger.info("TOTAL NUMBER OF ASSETS PROCESSED: " + numberAsset);

                }

            }
        } catch (java.text.ParseException e) {
            this.logger.error("Error when parsing", e.getMessage());
        }
    }

    /**
     * Get List of Asset from Alkemics.
     *
     * @param accessToken
     * @param additionalParams
     * @return Alkemics.
     */
    public Alkemics getProductList(String accessToken, Map<String, Object> additionalParams) {
        Alkemics result = null;

        try {
            result = AlkemicsAssetImporterUtils.retrieveAlkemicsProductList(alkemicsProductUrl, additionalParams, accessToken, httpClient);
        } catch (Exception e) {
            this.logger.error("Error IOException " + e.getMessage());
        } finally {
            this.logger.info("End GET PRODUCT BY 500");
            if (result == null) {
                try {
                    result = AlkemicsAssetImporterUtils.retrieveAlkemicsProductList(alkemicsProductUrl, additionalParams, accessToken, httpClient);
                } catch (JsonSyntaxException e) {
                    this.logger.error("Error JsonSyntaxException " + e.getMessage());

                } catch (ParseException e) {
                    this.logger.error("Error ParseException " + e.getMessage());
                }

            }
        }
        return result;
    }

    /**
     * Method will form a path for asset and write to DAM.
     *
     * @param name      the name of the asset.
     * @param gtin
     * @param path
     * @param meta
     * @param errorCase
     * @throws InterruptedException
     */
    private void writeToDam(String name, String gtin, String path, Map<String, String> meta, boolean errorCase)
            throws InterruptedException {
        if (name.equals("UC_null")) {
            this.logger.warn("NAME OF THE ASSET ASSET UC_NULL");

        } else {
            this.logger.info("WRITING ASSET:" + name);
            InputStream inputStream = null;
            Map<String, Object> param = new HashMap();
            param.put("sling.service.subservice", "content-writer-service");
            ResourceResolver resolver = null;
            Session adminSession = null;
            try {
                inputStream = new URL(path).openStream();

                resolver = this.resolverFactory.getServiceResourceResolver(param);

                adminSession = (Session) resolver.adaptTo(Session.class);

                AssetManager assetMgr = (AssetManager) resolver.adaptTo(AssetManager.class);

                String hierarchy = "";
                if ((gtin != null) && (!gtin.equals("")) &&
                        (gtin.length() > 0)) {
                    String folderlevel1 = gtin.substring(0, 8);
                    String folderlevel2 = gtin.substring(8, 13);
                    String folderlevel3 = gtin.substring(13, 14);
                    hierarchy = folderlevel1 + "/" + folderlevel2 + "/" + folderlevel3 + "/";
                    this.logger.info("folder hierarchy" + hierarchy);
                }
                String newFile = "/content/dam/dam/Usage-Interne/Produits/" + hierarchy + name;
                if (errorCase) {
                    newFile = "/content/dam/dam/Usage-Interne/Erreur-Produits/" + hierarchy + name;
                }
                Resource assetExist = resolver.getResource(newFile);
                if (assetExist != null) {
                    this.logger.info("CET ASSET EXISTE DEJA");
                    //    this.existingAsset += 1;
                    //     this.logger.info("Nombre d'asset en double :" + this.existingAsset);
                }
                long startTime = new Date().getTime();
                assetMgr.createAsset(newFile, inputStream, "image/jpeg", true);

                long endTime = new Date().getTime();

                long timeElapsed = endTime - startTime;
                this.Totaltimefor1000 += timeElapsed;

                this.logger.info("TEMPS D'INJECTION DANS AEM " + timeElapsed + "ms");

                Node contentNode = adminSession.getNode(newFile + "/" + "jcr:content");
                Node metaNode = contentNode.getNode("metadata");
                Resource assetResource = resolver.getResource(newFile);

                this.logger.info("MISE A JOUR DESMETADONNES PRODUITS");
                for (Map.Entry<String, String> entry : meta.entrySet()) {
                    metaNode.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
                resolver.commit();

                // resolver.refresh();
                adminSession.save();


                this.activeAssetResources.add(assetResource);

                if (this.activeAssetResources.size() >= this.batchSize) {
                    // waitForWorkflowsCompletion(this.waitTime, resolver);
                    this.logger.info("WAITING WORKFLOW PROCESS");

                    Thread.sleep(waitTime);
                    activeAssetResources.clear();

                }
            } catch (InterruptedException e) {
                this.logger.error("Error InterruptedException" + e.getMessage());
                throw e;
            } catch (ValueFormatException e) {
                this.logger.error("Error ValueFormatException" + e.getMessage());
            } catch (LockException e) {
                this.logger.error("Error LockException" + e.getMessage());
            } catch (ConstraintViolationException e) {
                this.logger.error("Error ConstraintViolationException" + e.getMessage());
            } catch (RepositoryException e) {
                this.logger.info("Error RepositoryException" + e.getMessage());
            } catch (LoginException e) {
                this.logger.error("Error wLoginException" + e.getMessage());
            } catch (MalformedURLException e) {
                this.logger.error("Error MalformedURLException" + e.getMessage());
            } catch (IOException e) {
                this.logger.error("Error IOException" + e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        this.logger.error("Error while getting resolve" + e.getMessage());
                    }
                }
                if ((resolver != null) && (resolver.isLive())) {
                    resolver.close();
                }
                adminSession.logout();

            }
        }
    }

    /**
     * Method will check if Asset has S7 metadata.
     *
     * @param resource
     * @param resourceResolver
     * @return boolean true if Asset has S7 metadata.
     */
    private boolean isScene7Asset(Resource resource, ResourceResolver resourceResolver) {
        resourceResolver.refresh();
        Resource metadataResource = resource.getChild("jcr:content/metadata");
        ValueMap properties = ResourceUtil.getValueMap(metadataResource);
        //   PayloadMap payloadMap = resourceResolver.adaptTo(PayloadMap.class);
        //   boolean status = payloadMap.isInWorkflow(resource.getPath() + "/jcr:content/renditions/original", true);
        final String s7sceneID = properties.get("dam:scene7ID", String.class);
        final String scene7FileStatus = properties.get("dam:scene7FileStatus", String.class);
        return (null != s7sceneID || null != scene7FileStatus);


    }

    private void waitForWorkflowsCompletion(long waitTime, ResourceResolver resourceResolver)
            throws InterruptedException {
        long startWaitingTime = System.currentTimeMillis();
        while (this.activeAssetResources.size() > 0) {
            Iterator<Resource> activeAssetResourcesIterator = this.activeAssetResources.iterator();
            while (activeAssetResourcesIterator.hasNext()) {
                Resource activeAssetResource = (Resource) activeAssetResourcesIterator.next();
                if (isScene7Asset(activeAssetResource, resourceResolver)) {
                    activeAssetResourcesIterator.remove();
                }
            }
            long waitedTime = System.currentTimeMillis() - startWaitingTime;
            if (waitedTime > 600000) {
                this.logger.info("Stop waiting for asset processing {}", Integer.valueOf(this.activeAssetResources.size()));
                this.activeAssetResources.clear();
            }
            this.logger.info("Waiting for asset processing {}", Integer.valueOf(this.activeAssetResources.size()));
            if (activeAssetResources.size() > 0) ;
            Thread.sleep(waitTime);
        }
    }
}

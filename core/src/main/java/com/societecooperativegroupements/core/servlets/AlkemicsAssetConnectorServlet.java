package com.societecooperativegroupements.core.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
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
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.societecooperativegroupements.core.models.AlkemicsAssetImporterConfiguration;
import com.societecooperativegroupements.core.schedulers.AlkemicsAssetImporterUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.societecooperativegroupements.core.models.alkemics.AkDatum;
import com.societecooperativegroupements.core.models.alkemics.Alkemics;
import com.societecooperativegroupements.core.models.alkemics.NamePublicLong;
import com.societecooperativegroupements.core.models.alkemics.Picture;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Leclerc Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/leclerc"})
@Designate(ocd = AlkemicsAssetImporterConfiguration.class)
public class AlkemicsAssetConnectorServlet extends SlingAllMethodsServlet implements Serializable {

    /**
     * serialVersionUID.
     */
    private static transient final long serialVersionUID = 8036720673306445672L;

    @Reference
    private transient ResourceResolverFactory resolverFactory;

    private CloseableHttpClient httpClient;

    private static final Gson gson = new Gson();

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private String urlToken;
    private String urlProduct;
    private boolean dryRun = false;
    private boolean init = true;

    private String clientId;

    private transient List<Resource> activeAssetResources;

    private String clientSecret;

    private int batchSize = 1000;

    private long waitTime = 120000;

    private long Totaltimefor1000 = 0;

    private String endDateEntry;

    private String startDateEntry;

    private int existingAsset;


    @Activate
    @Modified
    protected void activate(AlkemicsAssetImporterConfiguration config) {
        this.logger.info("AlkemicsAssetImporter activation");
        HttpClientBuilder builder = this.httpClientBuilderFactory.newBuilder();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
        builder.setDefaultRequestConfig(requestConfig);
        this.httpClient = builder.build();

        this.urlProduct = config.alkemicsProductUrl();
        this.urlToken = config.alkemicsTokenUrl();
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


    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "writeService");

        String jsonpath = req.getParameter("path");
        resp.getWriter().write("<p>Input JSON file " + jsonpath + " process...</p>");
        if (null != req.getParameter("init") && !req.getParameter("init").equals(""))
            init = Boolean.parseBoolean(req.getParameter("init"));
        if (null != req.getParameter("dryRun") && !req.getParameter("dryRun").equals(""))
            dryRun = Boolean.parseBoolean(req.getParameter("dryRun"));
        if (null != req.getParameter("waitTime") && !req.getParameter("waitTime").equals(""))
            waitTime = Integer.parseInt(req.getParameter("waitTime"));

        if (null != req.getParameter("batchSize") && !req.getParameter("batchSize").equals(""))
            batchSize = Integer.parseInt(req.getParameter("batchSize"));

        if (null != req.getParameter("endDateEntry") && !req.getParameter("endDateEntry").equals(""))
            endDateEntry = req.getParameter("endDateEntry");

        if (null != req.getParameter("startDateEntry") && !req.getParameter("startDateEntry").equals(""))
            startDateEntry = req.getParameter("startDateEntry");

        importAsset(jsonpath);

        resp.getWriter().write("Leclerc servlet END");
    }

    private void readProductByDay(Date startDate, String access_token, String page, long numberProcessed,
                                  List<AkDatum> listeProduit) {

        int max = 0;
        List<AkDatum> returnedProduct = null;
        logger.info("Product in process" + startDate);

        do {
            int limit = 500;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            long startime = startDate.getTime();

            calendar.add(Calendar.DATE, 1);

            Date dayAfter = calendar.getTime();
            long endtime = dayAfter.getTime();

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
                                logger.debug("Aucun Asset trouvé pour le produit: " + currentProduct.getGtin());
                            }

                        }
                        numberProcessed = numberProcessed + max;
                        logger.info(
                                "NOMBRE DE PRODUIT LUS: " + numberProcessed + "SUR  UN TOTAL " + obj.getTotalResults());
                        logger.info("NOMBRE DE PRODUIT AVEC DES IMAGES " + listeProduit.size());
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
            this.logger.error("Erreur  " + e.getMessage());
        } catch (JsonMappingException e) {
            this.logger.error("Erreur  " + e.getMessage());
        } catch (IOException e) {
            this.logger.error("Erreur  " + e.getMessage());
        } catch (LoginException e1) {
            this.logger.error("Erreur  " + e1.getMessage());
        } finally {
            if ((resolver != null) && (resolver.isLive())) {
                resolver.close();
            }
        }
    }

    public void importAsset(String jsonPath) {
        List<AkDatum> listeCurrentProduct = new ArrayList();
        try {
            if (jsonPath != null) {
                readProduct(jsonPath, listeCurrentProduct);
            } else {
                String accessToken = AlkemicsAssetImporterUtils.getAccessToken(this.urlToken, this.clientId, this.clientSecret, httpClient);

                Map<String, Object> additionalParams = null;
                Alkemics alkemics = getProductList(accessToken, additionalParams);

                this.logger.info("NOMBRE TOTAL DE PRODUIT:" + alkemics.getTotalResults());

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
                    String productAccessToken = AlkemicsAssetImporterUtils.getAccessToken(this.urlToken, this.clientId, this.clientSecret, httpClient);
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, -1);
                    Date currentDate = calendar.getTime();

                    this.logger.info("PRODUIT EN COURS DE TRAITEMENT ENTRE:" + startDate + "ET LE" + currentDate);

                    readProductByDay(startDate, productAccessToken, page, numberProcessed, listeProduit);

                    //readProductByDay(endDate, productAccessToken, page, numberProcessed, listeProduit);
                    numberAsset += listeProduit.size();
                    this.logger.info("NOMBRE TOTAL D'ASSET : " + listeProduit.size() + " A LA DATE DU:" + startDate);

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
                                if ((null == uuid) || (uuid.equals("")) || (null == gtin) || (gtin.equals(""))
                                        || (null == nomProduit) || (nomProduit.equals("")) || (null == supplierId)
                                        || (supplierId.equals(""))) {
                                    errorCase = true;
                                }
                                Boolean isConsumerUnit = currentProduct.getIsConsumerUnit();
                                Boolean isDisplayUnit = currentProduct.getIsDisplayUnit();
                                String categorie = "UC";
                                if ((null != isConsumerUnit) && (isConsumerUnit.booleanValue())) {
                                    categorie = "UC";
                                } else if ((null != isConsumerUnit) && (!isConsumerUnit.booleanValue())
                                        && (null != isDisplayUnit) && (isDisplayUnit.booleanValue())) {
                                    categorie = "BOX";
                                } else if ((null != isConsumerUnit) && (!isConsumerUnit.booleanValue())
                                        && (null != isDisplayUnit) && (!isDisplayUnit.booleanValue())) {
                                    categorie = "UL";
                                } else {
                                    errorCase = true;
                                }
                                List<Picture> assetPictureList = currentProduct.getAssets().getPictures();
                                int numberPicture = assetPictureList.size();
                                for (int j = 0; j < numberPicture; j++) {
                                    String assetUrl = ((Picture) currentProduct.getAssets().getPictures().get(j))
                                            .getUrl();
                                    if ((null != assetUrl) && (!assetUrl.equals(""))) {

                                        Picture productAsset = (Picture) currentProduct.getAssets().getPictures()
                                                .get(j);

                                        String name = categorie + "_" + productAsset.getGdsnFileName();

                                        hm.put("url", assetUrl);
                                        hm.put("uuid", uuid);

                                        hm.put("supplierId", supplierId);
                                        hm.put("updatedAt", productAsset.getUpdatedAt());
                                        hm.put("categorie", categorie);
                                        hm.put("libelle-produit", nomProduit);

                                        hm.put("gdsnFileName", productAsset.getGdsnFileName());
                                        if ((null == productAsset.getGdsnFileName())
                                                || (productAsset.getGdsnFileName().equals(""))) {
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

                                        writeToDam(name, gtin, assetUrl, hm, errorCase);

                                    } else {
                                        this.logger.info("Erreur URL Fichier " + assetUrl);
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
                    this.logger.info("NOMBRE TOTAL D'ASSET TRAITES: " + numberAsset);

                }

            }
        } catch (InterruptedException e) {
            this.logger.error("Erreur d'ecriture de Fichier " + e.getMessage());
        } catch (java.text.ParseException e) {
            this.logger.error("Error when parsin", e.getMessage());
        }
    }

    public Alkemics getProductList(String accessToken, Map<String, Object> additionalParams) {
        Alkemics result = null;
        try {
            result = AlkemicsAssetImporterUtils.retrieveAlkemicsProductList(this.urlProduct, additionalParams, accessToken, httpClient);

        } catch (Exception e) {
            this.logger.error("Error IOException " + e.getMessage());
        } finally {
            this.logger.info("End GET PRODUCT BY 500");
            if (result == null) {
                try {
                    result = AlkemicsAssetImporterUtils.retrieveAlkemicsProductList(this.urlProduct, additionalParams, accessToken, httpClient);
                } catch (JsonSyntaxException e) {
                    this.logger.error("Error JsonSyntaxException " + e.getMessage());

                }
            }
        }
        return result;
    }

    private void writeToDam(String name, String gtin, String path, Map<String, String> meta, boolean errorCase)
            throws InterruptedException {
        this.logger.info("WRITING ASSET:" + name);
        if (name.equals("UC_null")) {
            this.logger.info("NOM ASSET UC_NULL :" + name);

        } else {
            InputStream inputStream = null;
            Map<String, Object> param = new HashMap();
            param.put("sling.service.subservice", "content-writer-service");
            ResourceResolver resolver = null;
            try {
                inputStream = new URL(path).openStream();

                resolver = this.resolverFactory.getServiceResourceResolver(param);

                Session adminSession = (Session) resolver.adaptTo(Session.class);

                AssetManager assetMgr = (AssetManager) resolver.adaptTo(AssetManager.class);

                String hierarchy = "";
                if ((gtin != null) && (!gtin.equals("")) && (gtin.length() > 0)) {
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
                    this.existingAsset += 1;
                    this.logger.info("Nombre d'asset en double :" + this.existingAsset);
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

                this.logger.info("MISE A JOUR DES METADONNES PRODUITS");
                for (Map.Entry<String, String> entry : meta.entrySet()) {
                    metaNode.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
                resolver.commit();

                resolver.refresh();
                this.activeAssetResources.add(assetResource);

                if (this.activeAssetResources.size() >= this.batchSize) {
                    // waitForWorkflowsCompletion(this.waitTime, resolver);
                    this.logger.info("WAITING WORKFLOW PROCESS");

                    Thread.sleep(waitTime);
                    activeAssetResources.clear();

                }

            } catch (PersistenceException e1) {
                this.logger.error("Error while getting resolve" + e1.getMessage());
            } catch (InterruptedException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
                throw e;
            } catch (ValueFormatException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
            } catch (LockException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
            } catch (ConstraintViolationException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
            } catch (RepositoryException e) {
                this.logger.info("Error while getting resolve" + e.getMessage());
            } catch (LoginException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
            } catch (MalformedURLException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
            } catch (IOException e) {
                this.logger.error("Error while getting resolve" + e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        this.logger.error("Error while getting resolve" + e.getMessage());
                    }
                }

            }
            if ((resolver != null) && (resolver.isLive())) {
                resolver.close();
            }
        }
    }

}
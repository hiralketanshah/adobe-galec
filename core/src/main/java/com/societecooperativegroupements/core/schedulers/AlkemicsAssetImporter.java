package com.societecooperativegroupements.core.schedulers;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.day.cq.workflow.PayloadMap;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.societecooperativegroupements.core.models.alkemics.AkDatum;
import com.societecooperativegroupements.core.models.alkemics.Alkemics;
import com.societecooperativegroupements.core.models.alkemics.Assets;
import com.societecooperativegroupements.core.models.alkemics.NamePublicLong;
import com.societecooperativegroupements.core.models.alkemics.Picture;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Designate(ocd = AlkemicsAssetImporter.Config.class)
@Component(service = Runnable.class)
public class AlkemicsAssetImporter
  implements Runnable
{
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
  
  public void run()
  {
    this.logger.info("***************************************************************************");
    this.logger.info("AlkemicsAssetImporter is now running, alkemicsUrl='{}'", this.alkemicsProductUrl);
    this.logger.info("***************************************************************************");
    importAsset(null);
  }
  
  @Activate
  protected void activate(Config config)
  {
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
  
  
  
  public static void main(String[] args)
  {
    AlkemicsAssetImporter akm = new AlkemicsAssetImporter();
    akm.run();
  }
  
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private String urlToken = "https://apis.alkemics.com/auth/v2/token";
  private String urlProduct = "https://apis.alkemics.com/public/v1/products/list";
  private long Totaltimefor1000 = 0L;
  private int existingAsset;
  
  private void readProductByDay(Date endDate, String access_token, String page, long numberProcessed,
			List<AkDatum> listeProduit) {
		
			int max = 0;
			List<AkDatum> returnedProduct = null;
			logger.debug("Product in process" + endDate);

			do {
				int limit = 500;

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				long endtime = endDate.getTime();

				calendar.add(Calendar.DATE, -1);

				Date dayBefore = calendar.getTime();
				long startime = dayBefore.getTime();

				HashMap<String, Object> additionalParams = new HashMap<>();
				additionalParams.put("updated_at_from", startime);
				additionalParams.put("updated_at_to", endtime);
				additionalParams.put("limit", limit);
				additionalParams.put("next_page", page);
				

				Alkemics obj = getProductList(access_token, additionalParams);
				if(null!=obj) {
				

if(null!=obj.getData())
{
	page = obj.getNextPage();

				max = max + obj.getData().size();
				returnedProduct = obj.getData();
				if (max > 0) {

					for (int i = 0; i < obj.getData().size(); i++) {
						AkDatum currentProduct = obj.getData().get(i);

						if (currentProduct.getAssets() != null) {
							listeProduit.add(currentProduct);

						} else

						{
							logger.debug("Aucun Asset trouvé pour le produit" + currentProduct.getGtin());
						}

					}
					numberProcessed = numberProcessed + max;
					logger.debug(
							"NOMBRE DE PRODUIT LUS: " + numberProcessed + "SUR  UN TOTAL " + obj.getTotalResults());
					logger.debug("NOMBRE DE PRODUIT AVEC DES IMAGES " + listeProduit.size());

					readProductByDay(endDate, access_token, page, numberProcessed, listeProduit);

				}}
				}
				break;

			} while (page.equals("") && !returnedProduct.isEmpty());

  }
  
  private void readProduct(String jsonpath, List<AkDatum> currentProducts)
  {
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
		}
    catch (JsonParseException e)
    {
      this.logger.error("Erreur  " + e.getMessage());
    }
    catch (JsonMappingException e)
    {
      this.logger.error("Erreur  " + e.getMessage());
    }
    catch (IOException e)
    {
      this.logger.error("Erreur  " + e.getMessage());
    }
    catch (LoginException e1)
    {
      this.logger.error("Erreur  " + e1.getMessage());
    }
    finally
    {
      if ((resolver != null) && (resolver.isLive())) {
        resolver.close();
      }
    }
  }
  
  public void importAsset(String jsonPath)
  {
    List<AkDatum> listeCurrentProduct = new ArrayList();
    try
    {
      if (jsonPath != null)
      {
        readProduct(jsonPath, listeCurrentProduct);
      }
      else
      {
        String accessToken = getAccessToken();
        
        Map<String, Object> additionalParams=null;
        Alkemics alkemics = getProductList(accessToken, additionalParams);
        
        this.logger.info("NOMBRE TOTAL DE PRODUIT:" + alkemics.getTotalResults());
        
        String page = "";
        
        Calendar calendar = Calendar.getInstance();
        
        long numberProcessed = 0L;
        long numberAsset = 0L;
        
        this.logger.info("STARTING TO READ PRODUCT DAY BY DAY");
        
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date endDate = null;
        Date dateMin = null;
        if (this.init)
        {
          
           //String endDateEntry = "01-07-2020";
            
          //  String dateMinEntry = "28-06-2019";
            endDate = format.parse(endDateEntry);
            
            dateMin = format.parse(startDateEntry);
         
        } 
      	else
		{
			endDate=new java.util.Date();  

			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, -1);
			dateMin=calendar.getTime();
		}
		
          while (endDate.compareTo(dateMin) > 0)
          {
        	  this.logger.info("LA DATE :" + endDate);
            List<AkDatum> listeProduit = new ArrayList();
            page="";
            String productAccessToken = getAccessToken();
         
            readProductByDay(endDate, productAccessToken, page, numberProcessed, listeProduit);
            numberAsset += listeProduit.size();
            
			this.logger.info("NOMBRE TOTAL D'ASSET : " + listeProduit.size() + " A LA DATE DU:" + endDate);
			
            if (!this.dryRun)
            {
            int max = listeProduit.size();
            Map<String, String> hm = null;
            for (int i = 0; i < max; i++)
            {
              AkDatum currentProduct = (AkDatum)listeProduit.get(i);
              hm = new HashMap();
              if (currentProduct.getAssets() != null)
              {
                hm.put("gtin", currentProduct.getGtin());
                String gtin = currentProduct.getGtin();
                String uuid = currentProduct.getUuid();
                List<NamePublicLong> aname = currentProduct.getNamePublicLong();
                String nomProduit = null;
                String supplierId = currentProduct.getSupplierId();
                boolean errorCase = false;
                if (aname != null) {
                  for (int k = 0; k < aname.size(); k++) {
                    nomProduit = ((NamePublicLong)aname.get(k)).getData();
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
                for (int j = 0; j < numberPicture; j++)
                {
                  String assetUrl = ((Picture)currentProduct.getAssets().getPictures().get(j)).getUrl();
                  if ((null != assetUrl) && (!assetUrl.equals("")))
                  {
                    
                      Picture productAsset = (Picture)currentProduct.getAssets().getPictures().get(j);
                      
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
					this.logger.error("error writing dam"+e.getMessage());
				}
                    
                  }
                  else {
                    this.logger.info("Erreur URL Fichier " + assetUrl);
                  }
                }
              }
            }
			
            
          }
            else
            {
                this.logger.info("DRY RUN: running by day" );

            }
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, -1);
            endDate = calendar.getTime();
            this.logger.info(" CUMUL D'ASSET TRAITES: " + numberAsset );

          }
        
      }
    }

  
  catch (java.text.ParseException e)
  {
    this.logger.error("Error when parsin", e.getMessage());
  }
  }
  
  public Alkemics getProductList(String accessToken, Map<String, Object> additionalParams)
  {
    HttpPost post = new HttpPost(this.urlProduct);
    
    Alkemics result = null;
    try
    {
    	
      Map<String, Object> map = new HashMap<>();
      map.put("filter_source_include", new String[] { "gtin", "uuid", "assets.pictures", "assets.lastUpdatedAt.pictures", "supplierId", "isDisplayUnit", "isConsumerUnit", "namePublicLong.data" });
    		  
      if ((additionalParams != null) && (!additionalParams.isEmpty())) {
        map.putAll(additionalParams);
      }
      post.addHeader("Authorization", "Bearer " + accessToken);
      
      post.setEntity(new StringEntity(gson.toJson(map)));
      
      CloseableHttpResponse response = this.httpClient.execute(post);
      
      result = (Alkemics)gson.fromJson(EntityUtils.toString(response.getEntity()), Alkemics.class);
    }
    catch (UnsupportedEncodingException e)
    {
      this.logger.error("Error UnsupportedEncodingException "+ e.getMessage());
    }
    catch (ClientProtocolException e)
    {
      this.logger.error("Error ClientProtocolException" + e.getMessage());
    }
    catch (IOException e)
    {
      this.logger.error("Error IOException "+ e.getMessage());
    }
    finally
    {
      this.logger.info("End GET PRODUCT BY 500");
    }
    return result;
  }
  
  public String getAccessToken()
  {
    HttpPost post = new HttpPost(this.urlToken);
    
    String result = null;
    
    HashMap<String, String> map = new HashMap();
    map.put("client_id", this.clientId);
    map.put("client_secret", this.clientSecret);
    map.put("grant_type", "client_credentials");
    try
    {
      post.setEntity(new StringEntity(gson.toJson(map)));
      
      CloseableHttpResponse response = this.httpClient.execute(post);
      
      HashMap<String, String> resultMap = (HashMap)gson.fromJson(EntityUtils.toString(response.getEntity()), HashMap.class);
      
      result = (String)resultMap.get("access_token");
    }
    catch (UnsupportedEncodingException e)
    {
      this.logger.error("Error when getting the secret", e.getMessage());
    }
    catch (ClientProtocolException e)
    {
      this.logger.error("Error when getting the secret", e.getMessage());
    }
    catch (IOException e)
    {
      this.logger.error("Error when getting the secret", e.getMessage());
    }
    catch (JsonSyntaxException e)
    {
      this.logger.error("Error when getting the secret", e.getMessage());
    }
    catch (org.apache.http.ParseException e)
    {
      this.logger.error("Error when getting the secret", e.getMessage());
    }
    finally
    {
      this.logger.info("End");
    }
    return result;
  }
  
  private void writeToDam(String name, String gtin, String path, Map<String, String> meta, boolean errorCase)
    throws InterruptedException
  {
	  if(name.equals("UC_null"))
		{
			this.logger.info("NOM ASSET UC_NULL :" + name);

		}
	  else {
    this.logger.info("WRITING ASSET:" + name);
    InputStream inputStream = null;
    Map<String, Object> param = new HashMap();
    param.put("sling.service.subservice", "content-writer-service");
    ResourceResolver resolver = null;
    Session adminSession = null ;
    try
    {
      inputStream = new URL(path).openStream();
      
      resolver = this.resolverFactory.getServiceResourceResolver(param);
      
       adminSession = (Session)resolver.adaptTo(Session.class);
      
      AssetManager assetMgr = (AssetManager)resolver.adaptTo(AssetManager.class);
      
      String hierarchy = "";
      if ((gtin != null) && (!gtin.equals("")) && 
        (gtin.length() > 0))
      {
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
      if (assetExist != null)
      {
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
      
      this.logger.info("MISE A JOUR DESMETADONNES PRODUITS");
      for (Map.Entry<String, String> entry : meta.entrySet()) {
        metaNode.setProperty((String)entry.getKey(), (String)entry.getValue());
      }
     resolver.commit();
      
   // resolver.refresh();
    //  adminSession.save();
      
      
      this.activeAssetResources.add(assetResource);
      
      if (this.activeAssetResources.size() >= this.batchSize) {
     // waitForWorkflowsCompletion(this.waitTime, resolver);
			this.logger.info("WAITING WORKFLOW PROCESS");

       Thread.sleep(waitTime);
       activeAssetResources.clear();

      }
    }
   
    catch (InterruptedException e)
    {
      this.logger.error("Errror InterruptedException" + e.getMessage());
      throw e;
    }
    catch (ValueFormatException e)
    {
      this.logger.error("Errror ValueFormatException" + e.getMessage());
    }
    catch (LockException e)
    {
      this.logger.error("Errror LockExceptione" + e.getMessage());
    }
    catch (ConstraintViolationException e)
    {
      this.logger.error("Errror ConstraintViolationException" + e.getMessage());
    }
    catch (RepositoryException e)
    {
      this.logger.info("Errror RepositoryException" + e.getMessage());
    }
    catch (LoginException e)
    {
      this.logger.error("Errror wLoginException" + e.getMessage());
    }
    catch (MalformedURLException e)
    {
      this.logger.error("Errror MalformedURLException" + e.getMessage());
    }
    catch (IOException e)
    {
      this.logger.error("Errror IOException" + e.getMessage());
    }
    finally
    {
      if (inputStream != null) {
        try
        {
          inputStream.close();
        }
        catch (IOException e)
        {
          this.logger.error("Errror while getting resolve" + e.getMessage());
        }
      }
     if ((resolver != null) && (resolver.isLive())) {
        resolver.close();
      }
     adminSession.logout();
    }
	  }
  }

  private boolean isScene7Asset(Resource resource, ResourceResolver resourceResolver)
  {
	  
	  
	resourceResolver.refresh();
    Resource metadataResource = resource.getChild("jcr:content/metadata");
	

    ValueMap properties = ResourceUtil.getValueMap(metadataResource);
        
 //   PayloadMap payloadMap = resourceResolver.adaptTo(PayloadMap.class);
//   boolean status = payloadMap.isInWorkflow(resource.getPath() + "/jcr:content/renditions/original", true);


  final String s7sceneID = properties.get("dam:scene7ID", String.class);
    
  final String  scene7FileStatus=properties.get("dam:scene7FileStatus",String.class);
    
	return (null != s7sceneID|| null!=scene7FileStatus);

    
  }
  
  private void waitForWorkflowsCompletion(long waitTime, ResourceResolver resourceResolver)
    throws InterruptedException
  {
    long startWaitingTime = System.currentTimeMillis();
    while (this.activeAssetResources.size() > 0)
    {
      Iterator<Resource> activeAssetResourcesIterator = this.activeAssetResources.iterator();
      while (activeAssetResourcesIterator.hasNext())
      {
        Resource activeAssetResource = (Resource)activeAssetResourcesIterator.next();
        if (isScene7Asset(activeAssetResource, resourceResolver)) {
          activeAssetResourcesIterator.remove();
        }
      }
      long waitedTime = System.currentTimeMillis() - startWaitingTime;
      if (waitedTime > 60000000)
      {
        this.logger.info("Stop waiting for asset processing {}", Integer.valueOf(this.activeAssetResources.size()));
        this.activeAssetResources.clear();
      }
      this.logger.info("Waiting for asset processing {}", Integer.valueOf(this.activeAssetResources.size()));
      if(activeAssetResources.size()>0);
      Thread.sleep(waitTime);
    }
  }
  
  @ObjectClassDefinition(name="A scheduled task", description="Simple demo for cron-job like task with properties")
  public static @interface Config
  {
    @AttributeDefinition(name="Fr��quence(Cron-job expression)")
    String scheduler_expression() default "0 0 3 15 JUL ? 2020";
    
    @AttributeDefinition(name="Concurrent task", description="Whether or not to schedule this task concurrently")
    boolean scheduler_concurrent() default false;
    
    @AttributeDefinition(name="alkemicsProductUrl", description="Can be configured in /system/console/configMgr")
    String alkemicsProductUrl() default "https://apis.alkemics.com/public/v1/products/list";
    
    @AttributeDefinition(name="alkemicsTokenUrl", description="Can be configured in /system/console/configMgr")
    String alkemicsTokenUrl() default "https://apis.alkemics.com/auth/v2/token";
    
    @AttributeDefinition(name="Client ID", description="Can be configured in /system/console/configMgr")
    String clientId() default "3708d601d2806caa9045c1881f414791662440ef";
    
    @AttributeDefinition(name="Client Secret", description="Can be configured in /system/console/configMgr")
    String clientSecret() default "3541b4816f92fb5f79d518e10476f4de5b04cfde";
    
    @AttributeDefinition(name="Initialisation", description="Can be configured in /system/console/configMgr")
    boolean init() default false;
    
    @AttributeDefinition(name="dryRun", description="Can be configured in /system/console/configMgr")
    boolean dryRun() default true;
    
    @AttributeDefinition(name="Batch Size", description="Can be configured in /system/console/configMgr")
    int batchSize() default 1000;
    
    @AttributeDefinition(name="WaitTime", description="Can be configured in /system/console/configMgr")
    int waitTime() default 120000;
    
    @AttributeDefinition(name="endDateEntry", description="Can be configured in /system/console/configMgr")
    String endDateEntry() default "01-07-2020";
    
    @AttributeDefinition(name="startDateEntry", description="Can be configured in /system/console/configMgr")
    String startDateEntry() default "28-06-2019";
  }
}

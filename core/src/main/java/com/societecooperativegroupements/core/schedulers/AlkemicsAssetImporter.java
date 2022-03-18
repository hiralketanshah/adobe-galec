package com.societecooperativegroupements.core.schedulers;


import com.google.gson.Gson;

import com.societecooperativegroupements.core.models.AlkemicsAssetImporterConfiguration;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class AlkemicsAssetImporter implements Runnable {
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

        Map<String, Object> param = new HashMap();
        param.put("sling.service.subservice", "content-writer-service");
        ResourceResolver resolver = null;
        try {
            resolver = this.resolverFactory.getServiceResourceResolver(param);

            AlkemicsAssetImporterUtils.importAsset(null, resolver, this.alkemicsTokenUrl, this.clientId,
                    this.clientSecret, httpClient, this.alkemicsProductUrl, this.init, startDateEntry, endDateEntry,
                    this.dryRun, activeAssetResources, this.batchSize, this.waitTime);

        } catch (LoginException e) {
            logger.error("Error while importing assets from Alkemics : {}",e);
        } finally {
            if ((resolver != null) && (resolver.isLive())) {
                resolver.close();
            }
        }
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
        // PayloadMap payloadMap = resourceResolver.adaptTo(PayloadMap.class);
        // boolean status = payloadMap.isInWorkflow(resource.getPath() +
        // "/jcr:content/renditions/original", true);
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
                this.logger.info("Stop waiting for asset processing {}",
                        Integer.valueOf(this.activeAssetResources.size()));
                this.activeAssetResources.clear();
            }
            this.logger.info("Waiting for asset processing {}", Integer.valueOf(this.activeAssetResources.size()));
            if (activeAssetResources.size() > 0)
                ;
            Thread.sleep(waitTime);
        }
    }
}

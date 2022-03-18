package com.societecooperativegroupements.core.servlets;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Servlet;

import com.societecooperativegroupements.core.models.AlkemicsAssetImporterConfiguration;
import com.societecooperativegroupements.core.schedulers.AlkemicsAssetImporterUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
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

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Leclerc Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/leclerc"})
@Designate(ocd = AlkemicsAssetImporterConfiguration.class)
public class AlkemicsAssetConnectorServlet extends SlingAllMethodsServlet implements Serializable {

    /**
     * serialVersionUID.
     */
    private static transient final long serialVersionUID = 8036720673306445672L;

    @Reference
    private transient ResourceResolverFactory resolverFactory;

    private CloseableHttpClient httpClient;

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private String alkemicsTokenUrl;
    private String alkemicsProductUrl;
    private boolean dryRun = false;
    private boolean init = true;

    private String clientId;

    private transient List<Resource> activeAssetResources;

    private String clientSecret;

    private int batchSize = 1000;

    private int waitTime = 120000;


    private String endDateEntry;

    private String startDateEntry;

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


    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
        ResourceResolver resolver = null;
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);
       
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
        AlkemicsAssetImporterUtils.importAsset(null, resolver, this.alkemicsTokenUrl, this.clientId,
                this.clientSecret, httpClient, this.alkemicsProductUrl, this.init, startDateEntry, endDateEntry,
                this.dryRun, activeAssetResources, this.batchSize, this.waitTime);
        
        resp.getWriter().write("Leclerc servlet END");
        
        } catch (LoginException | IOException e) {
            logger.error("Error while importing assets from Alkemics : {}",e);
        }

        
    }


}
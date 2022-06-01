package com.societecooperativegroupements.core.servlets;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Leclerc Asset Selector Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/assetSelector" })
public class GalecAssetSelectorServlet extends SlingAllMethodsServlet implements Serializable {

    @Reference
    private transient ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder builder;

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
        ResourceResolver resolver = null;
        try {
            resolver = resolverFactory.getServiceResourceResolver(param);

            String gtin = req.getParameter("gtin");

            if (null != gtin) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("path", "/content/dam/dam/Usage-Interne/Produits");
                parameters.put("type", "dam:Asset");
                parameters.put("property", "jcr:content/metadata/gtin");
                parameters.put("property.operation", "equals");
                parameters.put("property.value", gtin);
                parameters.put("p.limit", "-1");

                Query query = builder.createQuery(PredicateGroup.create(parameters), resolver.adaptTo(Session.class));
                SearchResult result = query.getResult();
                StringBuilder builder = new StringBuilder();
                builder.append("List of Asset URLs : ");
                builder.append(System.lineSeparator());

                for (Hit hit : result.getHits()) {
                    builder.append(hit.getPath());
                    builder.append(System.lineSeparator());
                }
                resp.getWriter().write(builder.toString());
            } else {
                resp.getWriter().write("Kindly add the gtin parameter for further processing!");
            }

        } catch (LoginException | IOException | RepositoryException e) {
            logger.error("Error while importing assets from Alkemics : {}", e);
        }

    }

}
package com.societecooperativegroupements.core.utils;

import com.adobe.granite.asset.api.AssetVersionManager;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.day.crx.JcrConstants;
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
import org.apache.sling.api.resource.PersistenceException;
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
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class DamUtils {

    private static final Logger logger = LoggerFactory.getLogger(DamUtils.class);

    private static final Gson gson = new Gson();

    public static void createRevision(ResourceResolver resolver, AssetManager assetMgr, String newFile,
            InputStream inputStream, Map<String, String> meta) throws PersistenceException, RepositoryException {
        Session session = resolver.adaptTo(Session.class);
        if (resolver.hasChanges()) {
            resolver.commit();
        }

        Asset tempAsset = createAsset(assetMgr, session, resolver, newFile + "version", inputStream, meta);

        // Create the new version
        AssetVersionManager versionManager = resolver.adaptTo(AssetVersionManager.class);
        versionManager.createVersion(newFile, StringUtils.EMPTY);

        if (session != null) {
            resolver.delete(resolver.getResource(newFile + "/" + JcrConstants.JCR_CONTENT));
            Node originalAssetJcrContentNode = session.getNode(newFile);
            Node newAssetRenditionsNode = session.getNode(tempAsset.getPath() + "/" + JcrConstants.JCR_CONTENT);
            JcrUtil.copy(newAssetRenditionsNode, originalAssetJcrContentNode, null);
            JcrUtil.setProperty(originalAssetJcrContentNode, JcrConstants.JCR_LASTMODIFIED, new Date());
            
            
            newAssetRenditionsNode.getParent().remove();
            session.save();
        }
    }

    public static Asset createAsset(AssetManager assetMgr, Session session, ResourceResolver resolver,
            String fileName, InputStream inputStream, Map<String, String> meta)
            throws PathNotFoundException, RepositoryException, PersistenceException {
        
        long startTime = new Date().getTime();
        Asset tempAsset = assetMgr.createAsset(fileName, inputStream, "application/pdf", true);
        long endTime = new Date().getTime();
        long timeElapsed = endTime - startTime;
        logger.info("TEMPS D'INJECTION DANS AEM " + timeElapsed + "ms");

        
        Node contentNode = session.getNode(tempAsset.getPath() + "/" + "jcr:content");
        Node metaNode = contentNode.getNode("metadata");
        for (Map.Entry<String, String> entry : meta.entrySet()) {
            metaNode.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        resolver.commit();
        session.save();
        return tempAsset;
    }

  
}

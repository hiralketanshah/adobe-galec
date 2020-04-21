package com.societecooperativegroupements.core.workflow;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
//import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.DamUtil;




/**
 * @author arnaud
 * Specific Workflow service to extact metadatas

 *         
 */


@Component(service = WorkflowProcess.class, immediate=true, property = {"process.label=Galec Product Metadata Update"})
public class WorkflowUpdateMetadata implements WorkflowProcess {
	
	private final static Logger LOG = LoggerFactory.getLogger(WorkflowUpdateMetadata.class); 
	@Reference
	private ResourceResolverFactory resolverFactory;
	private final String patternfilename1 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*$";  
	private final String patternfilename2 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*$";        

	private final String userlogin="oauthservice";
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
		try {
        	WorkflowData workflowData = workItem.getWorkflowData();
			//String errorFolder = DamConstants.MOUNTPOINT_ASSETS+"/erreur/";

            if (workflowData.getPayloadType().equals("JCR_PATH")) {
	            String payloadPath = workItem.getWorkflowData().getPayload().toString();
	            payloadPath = payloadPath.split("/jcr:content/renditions/original")[0];
				ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
				Resource payload = resourceResolver.getResource(payloadPath);
				//String assetPath = payload.getPath();
				Asset asset = DamUtil.resolveToAsset(payload);
				
			
				//AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
				String assetName = asset.getName();
		        if (assetName.indexOf(".") > 0)
		        	assetName = assetName.substring(0, assetName.lastIndexOf("."));
		       
				
				boolean error = false;
				if(asset !=null) {
					boolean check = checkFilename(assetName);					
					if(!check) {
						error = true;
					}	
					if(!error) {
						Map<String, Object> params = new HashMap<String, Object>();
				      	params.put(ResourceResolverFactory.SUBSERVICE, userlogin);
				      
				        try (ResourceResolver rResolver = resolverFactory.getServiceResourceResolver(params)){
				        	if(rResolver!= null) {
				        		LOG.info("name"+assetName);
				        		String[] assetMetadata = assetName.split("-");
				        		LOG.info("meta produit"+assetMetadata.length);
				        		LOG.info("meta produit 0"+assetMetadata[0]);
				        		String metier = assetMetadata[0];
				        		LOG.info("meta produit1"+assetMetadata[1]);

				        		String semaine = assetMetadata[1];
				        		
				        		LOG.info("meta produit3"+assetMetadata[2]);
				        		String produit="";
				        		String op="";
				        		if(assetMetadata.length==4)
				        		{
				        		 op = assetMetadata[2];
				        		 produit = assetMetadata[3];

				        		}
				        		else
				        		{
					        		 produit = assetMetadata[2];

				        		}
				        		LOG.info("meta produit3"+assetMetadata[3]);

				       
				        		//Map<String, Object> Metadata = asset.getMetadata();
				        		
				        		Resource metadataRes = asset.adaptTo(Resource.class).getChild("jcr:content/metadata");
				        		ModifiableValueMap Metadata = metadataRes.adaptTo(ModifiableValueMap.class);
				        		Metadata.put("dc:sample", "test checking");
				        		LOG.info("meta"+Metadata.toString());

								//set metadata
								Metadata.put("operation", op);
								Metadata.put("libelle-produit", produit);
								Metadata.put("semaine", semaine);
								Metadata.put("metier", metier);
								
								
								if (rResolver.hasChanges()) {
									rResolver.commit();
								}
				        	}
				        }
		
					}

				}
				
			}
  
        } catch (Exception e) {
            throw new WorkflowException(e.getMessage(), e);
        } 
	}
	
	protected boolean checkFilename(String filename) {
		LOG.info("--- FILENAME : "+filename);
		return (filename.matches(patternfilename1)||filename.matches(patternfilename2));
		
	}
	
	
	
	
	 protected String moveAsset(AssetManager assetManager,String currentPath, String newPath) {
		    LOG.info("TRY TO MOVE the ASSET! {} to {}",currentPath,newPath);
			if(assetManager.assetExists(newPath)) {
		    	LOG.info("an asset already exist/ STOP");
			}
			assetManager.moveAsset(currentPath, newPath);
		    LOG.info("Moved the ASSET! ");
		    return null;
		}
	 

	 
}




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
	//private final String patternfilename1 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*$";
	private final String patternfilename1 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([0-9]{1,4}x[0-9]{1,4})*$";

	private final String patternfilename2 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([0-9]{1,4}x[0-9]{1,4})*$";        

	private final String patternfilename3 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([0-9]{1,4}x[0-9]{1,4})*-([0-9]{1,2})$";

	private final String patternfilename4 = "^([a-zA-Z0-9])*-([a-zA-Z0-9])*-([a-zA-Z0-9])*-([0-9]{1,4}x[0-9]{1,4})*-([0-9]{1,2})$";        

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
					boolean check =  (assetName.matches(patternfilename1)||assetName.matches(patternfilename2)
							||assetName.matches(patternfilename3)||assetName.matches(patternfilename4));
					
					if(!check) {
						error = true;
					}	
					if(!error) {
						Map<String, Object> params = new HashMap<String, Object>();
				      	params.put(ResourceResolverFactory.SUBSERVICE, userlogin);
				      
				        try (ResourceResolver rResolver = resolverFactory.getServiceResourceResolver(params)){
				        	if(rResolver!= null) {
				        		LOG.info("name"+assetName);
				        		String metier="";
				        				String produit="";
				        		String op="";
				        		String semaine ="";
				        		String titre=assetName.replace("-", " ");
				        		
				        		String[] assetMetadata = assetName.split("-");
				        		 metier = assetMetadata[0];
				        		 semaine = assetMetadata[1];

				        		if(assetName.matches(patternfilename1)||assetName.matches(patternfilename3))
				        		{
				        		
				        		LOG.info("meta produit"+assetMetadata.length);
				        		LOG.info("meta produit 0"+assetMetadata[0]);
				        		LOG.info("meta produit 1"+assetMetadata[1]);
				        		LOG.info("meta produit 3"+assetMetadata[2]);
				        		
				        		
				        		
				        		 op = assetMetadata[2];
				        		 produit = assetMetadata[3];

				        		}
				        		else if(assetName.matches(patternfilename2) ||assetName.matches(patternfilename4))
				        		{
					        		 produit = assetMetadata[2];

				        		}
				        		

				       
				        		//Map<String, Object> Metadata = asset.getMetadata();
				        		
				        		Resource metadataRes = asset.adaptTo(Resource.class).getChild("jcr:content/metadata");
				        		ModifiableValueMap Metadata = metadataRes.adaptTo(ModifiableValueMap.class);
				        		LOG.info("meta"+Metadata.toString());

								//set metadata
								Metadata.put("nom_ope", op);
								Metadata.put("nom_produit", produit);
								Metadata.put("semaine", semaine);
								Metadata.put("metier", metier);
								Metadata.put("dc:title", titre);

								
								
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
	
	/*protected boolean checkFilename(String filename) {
		LOG.info("--- FILENAME : "+filename);
		return (filename.matches(patternfilename1)||filename.matches(patternfilename2)
				||filename.matches(patternfilename3)||filename.matches(patternfilename4));
		
	}*/
	
	
	

	 

	 
}




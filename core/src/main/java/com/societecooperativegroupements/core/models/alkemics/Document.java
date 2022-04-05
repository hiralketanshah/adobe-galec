package com.societecooperativegroupements.core.models.alkemics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fileName",
    "uniformResourceIdentifier",
    "documentTypeCode",
    "fileEffectiveEndDateTime",
    "fileEffectiveStartDateTime"
})
public class Document {

    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("uniformResourceIdentifier")
    private String uniformResourceIdentifier;
    
    @JsonProperty("documentTypeCode")
    private DocumentTypeCode documentTypeCode;
    
    @JsonProperty("fileEffectiveEndDateTime")
    private Long fileEffectiveEndDateTime;
    
    @JsonProperty("fileEffectiveStartDateTime")
    private Long fileEffectiveStartDateTime;
        
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("fileName")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    @JsonProperty("uniformResourceIdentifier")
    public String getUrl() {
        return uniformResourceIdentifier;
    }

    @JsonProperty("uniformResourceIdentifier")
    public void setUrl(String uniformResourceIdentifier) {
        this.uniformResourceIdentifier = uniformResourceIdentifier;
    }
    
    @JsonProperty("documentTypeCode")
    public DocumentTypeCode getDocumentTypeCode() {
        return documentTypeCode;
    }
    
    @JsonProperty("fileEffectiveStartDateTime")
    public String getStartDateTime() {
        if(null!=fileEffectiveStartDateTime) {
            Date date = new Date(fileEffectiveStartDateTime*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("CET"));
            
            String formattedDate = sdf.format(date);
           return formattedDate;
            
        }
        
        return StringUtils.EMPTY;
    }
    
    @JsonProperty("fileEffectiveEndDateTime")
    public String getEndDateTime() {
        if(null!=fileEffectiveEndDateTime) {
            Date date = new Date(fileEffectiveEndDateTime*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("CET"));
            
            String formattedDate = sdf.format(date);
           return formattedDate;
            
        }
        
        return StringUtils.EMPTY;
    }

   
}

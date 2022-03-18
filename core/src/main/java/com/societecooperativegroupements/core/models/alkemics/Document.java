
package com.societecooperativegroupements.core.models.alkemics;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "fileName"
})
public class Document {

    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("uniformResourceIdentifier")
    private String uniformResourceIdentifier;
    
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
}

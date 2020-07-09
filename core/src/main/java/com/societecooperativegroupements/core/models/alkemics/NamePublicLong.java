
package com.societecooperativegroupements.core.models.alkemics;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sort",
    "expressedIn",
    "data"
})
public class NamePublicLong {

    @JsonProperty("sort")
    private String sort;
    
    @JsonProperty("expressedIn")
    private ExpressedIn_ expressedIn;
    
    @JsonProperty("data")
    private String data;
    
    @JsonProperty("supplierId")
    private String supplierId;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("sort")
    public String getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(String sort) {
        this.sort = sort;
    }

    @JsonProperty("expressedIn")
    public ExpressedIn_ getExpressedIn() {
        return expressedIn;
    }

    @JsonProperty("expressedIn")
    public void setExpressedIn(ExpressedIn_ expressedIn) {
        this.expressedIn = expressedIn;
    }

    @JsonProperty("supplierId")
    public String getSupplierId() {
        return supplierId;
    }

    @JsonProperty("supplierId")
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    @JsonProperty("data")
    public String getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(String data) {
        this.data = data;
    }


    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

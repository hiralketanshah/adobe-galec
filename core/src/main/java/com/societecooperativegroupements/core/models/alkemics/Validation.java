
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
    "status",
    "nbSuggestions",
    "regulatory_status"
})
public class Validation {

    @JsonProperty("status")
    private Integer status;
    @JsonProperty("nbSuggestions")
    private Integer nbSuggestions;
    @JsonProperty("regulatory_status")
    private Integer regulatoryStatus;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("nbSuggestions")
    public Integer getNbSuggestions() {
        return nbSuggestions;
    }

    @JsonProperty("nbSuggestions")
    public void setNbSuggestions(Integer nbSuggestions) {
        this.nbSuggestions = nbSuggestions;
    }

    @JsonProperty("regulatory_status")
    public Integer getRegulatoryStatus() {
        return regulatoryStatus;
    }

    @JsonProperty("regulatory_status")
    public void setRegulatoryStatus(Integer regulatoryStatus) {
        this.regulatoryStatus = regulatoryStatus;
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

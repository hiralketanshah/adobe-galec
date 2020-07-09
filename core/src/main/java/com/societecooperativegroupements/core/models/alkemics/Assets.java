
package com.societecooperativegroupements.core.models.alkemics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pictures",
    "lastUpdatedAt"
})
public class Assets {

    @JsonProperty("pictures")
    private List<Picture> pictures = null;
    @JsonProperty("lastUpdatedAt")
    private LastUpdatedAt lastUpdatedAt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pictures")
    public List<Picture> getPictures() {
        return pictures;
    }

    @JsonProperty("pictures")
    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @JsonProperty("lastUpdatedAt")
    public LastUpdatedAt getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    @JsonProperty("lastUpdatedAt")
    public void setLastUpdatedAt(LastUpdatedAt lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
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

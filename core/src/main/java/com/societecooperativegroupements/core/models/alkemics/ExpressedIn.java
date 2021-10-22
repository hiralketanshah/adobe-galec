
package com.societecooperativegroupements.core.models.alkemics;

import java.util.ArrayList;
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
    "referential",
    "presentEntities"
})
public class ExpressedIn {

    @JsonProperty("referential")
    private Referential referential;
    @JsonProperty("presentEntities")
    private List<PresentEntity> presentEntities = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("referential")
    public Referential getReferential() {
        return referential;
    }

    @JsonProperty("referential")
    public void setReferential(Referential referential) {
        this.referential = referential;
    }

    @JsonProperty("presentEntities")
    public List<PresentEntity> getPresentEntities() {
        return presentEntities != null ? new ArrayList<>(presentEntities) : null;
    }

    @JsonProperty("presentEntities")
    public void setPresentEntities(List<PresentEntity> presentEntities) {
        this.presentEntities = presentEntities != null ? new ArrayList<>(presentEntities) : null;
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

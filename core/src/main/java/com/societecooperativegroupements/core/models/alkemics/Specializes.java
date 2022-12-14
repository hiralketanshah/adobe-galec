
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
    "isIdentifiedBy"
})
public class Specializes {

    @JsonProperty("isIdentifiedBy")
    private List<IsIdentifiedBy> isIdentifiedBy = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("isIdentifiedBy")
    public List<IsIdentifiedBy> getIsIdentifiedBy() {
        return isIdentifiedBy != null ? new ArrayList<>(isIdentifiedBy) : null;
    }

    @JsonProperty("isIdentifiedBy")
    public void setIsIdentifiedBy(List<IsIdentifiedBy> isIdentifiedBy) {
        this.isIdentifiedBy = isIdentifiedBy != null ? new ArrayList<>(isIdentifiedBy) : null;
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

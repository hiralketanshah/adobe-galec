
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
    "name",
    "uniformResourceIdentifier",
    "height",
    "width",
    "fileSize",
    "productpicture_id",
    "updatedAt",
    "crc32",
    "quality",
    "createdAt"
})
public class Exportable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("uniformResourceIdentifier")
    private String uniformResourceIdentifier;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("fileSize")
    private Integer fileSize;
    @JsonProperty("productpicture_id")
    private Integer productpictureId;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("crc32")
    private String crc32;
    @JsonProperty("quality")
    private Integer quality;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("uniformResourceIdentifier")
    public String getUniformResourceIdentifier() {
        return uniformResourceIdentifier;
    }

    @JsonProperty("uniformResourceIdentifier")
    public void setUniformResourceIdentifier(String uniformResourceIdentifier) {
        this.uniformResourceIdentifier = uniformResourceIdentifier;
    }

    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Integer height) {
        this.height = height;
    }

    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Integer width) {
        this.width = width;
    }

    @JsonProperty("fileSize")
    public Integer getFileSize() {
        return fileSize;
    }

    @JsonProperty("fileSize")
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    @JsonProperty("productpicture_id")
    public Integer getProductpictureId() {
        return productpictureId;
    }

    @JsonProperty("productpicture_id")
    public void setProductpictureId(Integer productpictureId) {
        this.productpictureId = productpictureId;
    }

    @JsonProperty("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updatedAt")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("crc32")
    public String getCrc32() {
        return crc32;
    }

    @JsonProperty("crc32")
    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    @JsonProperty("quality")
    public Integer getQuality() {
        return quality;
    }

    @JsonProperty("quality")
    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    @JsonProperty("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("createdAt")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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


package it.paolo.amazonapp.entities.images;

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
    "hiRes",
    "thumb",
    "large",
    "main",
    "variant",
    "lowRes"
})
public class ColorImages {

    @JsonProperty("hiRes")
    private String hiRes;
    @JsonProperty("thumb")
    private String thumb;
    @JsonProperty("large")
    private String large;
    @JsonProperty("main")
    private Main main;
    @JsonProperty("variant")
    private String variant;
    @JsonProperty("lowRes")
    private Object lowRes;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("hiRes")
    public String getHiRes() {
        return hiRes;
    }

    @JsonProperty("hiRes")
    public void setHiRes(String hiRes) {
        this.hiRes = hiRes;
    }

    public ColorImages withHiRes(String hiRes) {
        this.hiRes = hiRes;
        return this;
    }

    @JsonProperty("thumb")
    public String getThumb() {
        return thumb;
    }

    @JsonProperty("thumb")
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public ColorImages withThumb(String thumb) {
        this.thumb = thumb;
        return this;
    }

    @JsonProperty("large")
    public String getLarge() {
        return large;
    }

    @JsonProperty("large")
    public void setLarge(String large) {
        this.large = large;
    }

    public ColorImages withLarge(String large) {
        this.large = large;
        return this;
    }

    @JsonProperty("main")
    public Main getMain() {
        return main;
    }

    @JsonProperty("main")
    public void setMain(Main main) {
        this.main = main;
    }

    public ColorImages withMain(Main main) {
        this.main = main;
        return this;
    }

    @JsonProperty("variant")
    public String getVariant() {
        return variant;
    }

    @JsonProperty("variant")
    public void setVariant(String variant) {
        this.variant = variant;
    }

    public ColorImages withVariant(String variant) {
        this.variant = variant;
        return this;
    }

    @JsonProperty("lowRes")
    public Object getLowRes() {
        return lowRes;
    }

    @JsonProperty("lowRes")
    public void setLowRes(Object lowRes) {
        this.lowRes = lowRes;
    }

    public ColorImages withLowRes(Object lowRes) {
        this.lowRes = lowRes;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public ColorImages withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

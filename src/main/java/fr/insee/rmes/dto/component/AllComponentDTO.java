
package fr.insee.rmes.dto.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dateMiseAJour",
    "statutValidation",
    "id"
})
@Generated("jsonschema2pojo")
public class AllComponentDTO implements Serializable
{

    @JsonProperty("dateMiseAJour")
    private String dateMiseAJour;
    @JsonProperty("statutValidation")
    private String statutValidation;
    @JsonProperty("id")
    private String id;
    @JsonIgnore
    @Valid
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 4692190411026885589L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AllComponentDTO() {
    }

    /**
     * 
     * @param dateMiseAJour
     * @param statutValidation
     * @param id
     */
    public AllComponentDTO(String dateMiseAJour, String statutValidation, String id) {
        super();
        this.dateMiseAJour = dateMiseAJour;
        this.statutValidation = statutValidation;
        this.id = id;
    }

    @JsonProperty("dateMiseAJour")
    public String getDateMiseAJour() {
        return dateMiseAJour;
    }

    @JsonProperty("dateMiseAJour")
    public void setDateMiseAJour(String dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }

    public AllComponentDTO withDateMiseAJour(String dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
        return this;
    }

    @JsonProperty("statutValidation")
    public String getStatutValidation() {
        return statutValidation;
    }

    @JsonProperty("statutValidation")
    public void setStatutValidation(String statutValidation) {
        this.statutValidation = statutValidation;
    }

    public AllComponentDTO withStatutValidation(String statutValidation) {
        this.statutValidation = statutValidation;
        return this;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public AllComponentDTO withId(String id) {
        this.id = id;
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

    public AllComponentDTO withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}

package fr.insee.rmes.persistence;

import fr.insee.rmes.persistence.ontologies.*;
import fr.insee.rmes.utils.Constants;
import fr.insee.rmes.utils.config.MagmaConfig;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.Function;

public enum ObjectType {
    CONCEPT(Constants.CONCEPT, SKOS.CONCEPT, c->c.getConceptsBaseUri()),
    COLLECTION(Constants.COLLECTION, SKOS.COLLECTION, MagmaConfig::getCollectionsBaseUri),
    FAMILY(Constants.FAMILY, INSEE.FAMILY, MagmaConfig::getOpFamiliesBaseUri),
    SERIES("series", INSEE.SERIES, MagmaConfig::getOpSeriesBaseUri),
    OPERATION("operation", INSEE.OPERATION, MagmaConfig::getOperationsBaseUri),
    INDICATOR("indicator", INSEE.INDICATOR, MagmaConfig::getProductsBaseUri),
    DOCUMENTATION("documentation", SDMX_MM.METADATA_REPORT, MagmaConfig::getDocumentationsBaseUri),
    DOCUMENT(Constants.DOCUMENT, FOAF.DOCUMENT, MagmaConfig::getDocumentsBaseUri),
    LINK("link", FOAF.DOCUMENT, MagmaConfig::getLinksBaseUri),
    GEO_STAT_TERRITORY("geoFeature", GEO.FEATURE, MagmaConfig::getDocumentationsGeoBaseUri),
    ORGANIZATION("organization", ORG.ORGANIZATION, c->""),
    STRUCTURE("structure", QB.DATA_STRUCTURE_DEFINITION, MagmaConfig::getStructuresBaseUri),
    CODE_LIST(Constants.CODELIST, QB.CODE_LIST, MagmaConfig::getCodelistsBaseUri),
    MEASURE_PROPERTY("measureProperty",QB.MEASURE_PROPERTY, c-> c.getStructuresComponentsBaseUri()  + "mesure"),
    ATTRIBUTE_PROPERTY("attributeProperty", QB.ATTRIBUTE_PROPERTY, c->c.getStructuresComponentsBaseUri() + "attribut"),
    DIMENSION_PROPERTY("dimensionProperty", QB.DIMENSION_PROPERTY, c->c.getStructuresComponentsBaseUri() + "dimension"),
    UNDEFINED(Constants.UNDEFINED, null, c -> "");

    private final String labelType;
    private final IRI uri;
    private final Function<MagmaConfig, String> getBaseUriFromConfig;

    ObjectType(String labelType, IRI uri, Function<MagmaConfig, String> getBaseUriFromConfig){
        this.labelType =labelType;
        this.uri=uri;
        this.getBaseUriFromConfig = getBaseUriFromConfig;
    }

    private MagmaConfig magmaConfig;

    private void setConfig(MagmaConfig magmaConfigParam) {
        magmaConfig = magmaConfigParam;
    }


    @Component
    public static class ConfigServiceInjector {
        @Autowired
        private MagmaConfig magmaConfig;

        @PostConstruct
        public void postConstruct() {
            Arrays.stream(ObjectType.values())
                    .forEach(o->o.setConfig(magmaConfig));
        }
    }

    public String getLabelType(){
        return labelType;
    }

    public  IRI getUri(){
        return this.uri;
    }

    public  String getBaseUri(){
        return this.getBaseUriFromConfig.apply(this.magmaConfig);
    }


    public String getBaseUriGestion() {
        return magmaConfig.getBaseUriGestion() + this.getBaseUri() ;
    }

}

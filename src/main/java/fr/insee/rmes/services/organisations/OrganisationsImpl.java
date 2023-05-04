package fr.insee.rmes.services.organisations;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.model.operation.SerieById;
import fr.insee.rmes.model.organisation.OrganisationModel;
import fr.insee.rmes.modelSwagger.operation.*;
import fr.insee.rmes.modelSwagger.organisations.Label;
import fr.insee.rmes.modelSwagger.organisations.OrganisationsModelSwagger;
import fr.insee.rmes.persistence.RdfService;
import fr.insee.rmes.utils.Constants;
import fr.insee.rmes.utils.config.Config;
import fr.insee.rmes.utils.exceptions.RmesException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrganisationsImpl extends RdfService implements OrganisationsServices {

    public Map<String, Object> initParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("LG1", Config.LG1);
        params.put("LG2", Config.LG2);
        return params;
    }

    @Override
    public String getAllOrganisations() throws RmesException, IOException {
        Map<String, Object> params = initParams();
        JSONArray organisationsList;
        organisationsList = repoGestion.getResponseAsArray(buildRequest(Constants.ORGANISATIONS_QUERIES_PATH, "getAllOrganisations.ftlh", params));


        ObjectMapper jsonResponse = new ObjectMapper();
        OrganisationModel[] listOperations = jsonResponse.readValue(organisationsList.toString(), OrganisationModel[].class);

        ObjectMapper mapper = new ObjectMapper();
        List<OrganisationsModelSwagger> organisationsListModelSwaggerS = new ArrayList<>();

        for (OrganisationModel byOrganisations : listOperations) {
            if (byOrganisations.getId() != null) {
                Label prefLabel1 = new Label(Config.LG1, byOrganisations.getPreflabelLg1());
                Label prefLabel2 = new Label(Config.LG2, byOrganisations.getPreflabelLg1());
                List<Label> label = new ArrayList<>();
                if (byOrganisations.getPreflabelLg1() != null) {
                    label.add(prefLabel1);}
                if (byOrganisations.getPreflabelLg2() != null) {
                    label.add(prefLabel2);
                }
                OrganisationsModelSwagger organisationsListModelSwagger = new OrganisationsModelSwagger(byOrganisations.getId(), label);
                organisationsListModelSwaggerS.add(organisationsListModelSwagger);
            }
        }
        return mapper.writeValueAsString(organisationsListModelSwaggerS);
    }

    @Override
    public JSONObject getOrganisationById(String id) throws RmesException, IOException {
        Map<String, Object> params = initParams();
        params.put("ID", id);

        JSONObject operationId = repoGestion.getResponseAsObject(buildRequest(Constants.ORGANISATIONS_QUERIES_PATH, "getOrganisationById.ftlh", params));

        ObjectMapper jsonResponse =new ObjectMapper();
        OrganisationModel operationById = jsonResponse.readValue(operationId.toString(),OrganisationModel.class);

        ObjectMapper mapper = new ObjectMapper();
        Label preflabel1 = new Label(Config.LG1,operationById.getPreflabelLg1());
        Label preflabel2 = new Label(Config.LG2, operationById.getPreflabelLg2());
        Label altlabel1 = new Label(Config.LG1,operationById.getAltlabelLg1());
        Label altlabel2 = new Label(Config.LG2, operationById.getAltlabelLg2());
        List<Label> label = new ArrayList<>();
        List<Label> altlabel = new ArrayList<>();
        if (operationById.getPreflabelLg1() != null) {
            label.add(preflabel1);}
        if (operationById.getPreflabelLg2() != null) {
            label.add(preflabel2);
        }
        if (operationById.getAltlabelLg1() != null) {
            altlabel.add(altlabel1);}
        if (operationById.getAltlabelLg2() != null) {
            altlabel.add(altlabel2);
        }
        OrganisationsModelSwagger organisationsListModelSwagger= new OrganisationsModelSwagger(operationById.getId(), operationById.getUri(),label);
        JSONObject jsonrep = new JSONObject(mapper.writeValueAsString(organisationsListModelSwagger));
        if (operationById.getAbreviation() != null){
            jsonrep.put("abreviation",operationById.getAbreviation());
        }
        if (altlabel != null){
            jsonrep.put("altlabel",altlabel);
        }
        if (operationById.getUniteDe() != null){
            jsonrep.put("uniteDe",operationById.getUniteDe());
        }
        if (operationById.getSousTelleDe() != null){
            jsonrep.put("sousTelleDe",operationById.getSousTelleDe());
        }


        return jsonrep;

    }


}
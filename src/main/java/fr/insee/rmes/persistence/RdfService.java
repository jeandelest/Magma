package fr.insee.rmes.persistence;

import java.util.Map;


import fr.insee.rmes.utils.config.MagmaConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.rmes.model.ValidationStatus;
import fr.insee.rmes.utils.exceptions.RmesException;

@Service
public abstract class RdfService {

	
	@Autowired
	protected RepositoryGestion repoGestion;

    @Autowired
    protected MagmaConfig magmaConfig;
	
    protected static String buildRequest(String path, String fileName, Map<String, Object> params) throws RmesException {
        return FreeMarkerUtils.buildRequest(path, fileName, params);
    }
    
    protected JSONArray formatLabel(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", MagmaConfig.getLG1());
        lg2.put("langue", MagmaConfig.getLG2());
        lg1.put("contenu", obj.getString("prefLabelLg1"));
        lg2.put("contenu", obj.getString("prefLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONArray formatNom(JSONObject obj) {
        JSONArray nom = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", MagmaConfig.getLG1());
        lg2.put("langue", MagmaConfig.getLG2());
        lg1.put("contenu", obj.getString("altLabelLg1"));
        lg2.put("contenu", obj.getString("altLabelLg2"));

        nom.put(lg1);
        nom.put(lg2);

        return nom;
    }
    
    protected String getValidationState(String validationState){
        if(ValidationStatus.VALIDATED.toString().equalsIgnoreCase(validationState)){
            return "Publiée";
        }
        if(ValidationStatus.MODIFIED.toString().equalsIgnoreCase(validationState)){
            return "Provisoire, déjà publiée";
        }
        if(ValidationStatus.UNPUBLISHED.toString().equalsIgnoreCase(validationState)){
            return "Provisoire, jamais publiée";
        }
        return validationState;
    }
}

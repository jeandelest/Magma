package fr.insee.rmes.services.pogues;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.rmes.dto.operation.*;
import fr.insee.rmes.model.operation.OperationById;
import fr.insee.rmes.model.operation.OperationBySerieId;
import fr.insee.rmes.model.operation.SerieById;
import fr.insee.rmes.model.operation.SerieModel;
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
public class PoguesImpl extends RdfService implements PoguesServices {

    @Override
    public String getAllSeriesLists(Boolean survey) throws RmesException, IOException {
        Map<String, Object> params = initParams();
        JSONArray seriesList ;

        if ( survey) {
            seriesList = repoGestion.getResponseAsArray(buildRequest(Constants.POGUES_QUERIES_PATH, "getAllCodesListsSurvey.ftlh", params));
        }
        else {
            seriesList = repoGestion.getResponseAsArray(buildRequest(Constants.POGUES_QUERIES_PATH, "getAllCodesLists.ftlh", params));
        }

        ObjectMapper jsonResponse = new ObjectMapper();
        SerieModel[] listSeries = jsonResponse.readValue(seriesList.toString(), SerieModel[].class);

        ObjectMapper mapper = new ObjectMapper();
        List<SeriesListDTO> seriesListDTOS= new ArrayList<>();

        for (SerieModel bySerie : listSeries) {
            AltLabel altLabelSerie1 = new AltLabel(Config.LG1, bySerie.getSeriesAltLabelLg1());
            AltLabel altLabelSerie2 = new AltLabel(Config.LG2, bySerie.getSeriesAltLabelLg2());
                List<AltLabel> altLabelSerie = new ArrayList<>();
                if (bySerie.getSeriesAltLabelLg1()!=null) {
                    altLabelSerie.add(altLabelSerie1);
                    altLabelSerie.add(altLabelSerie2);   }
            Label labelSerie1= new Label(Config.LG1, bySerie.getSeriesLabelLg1());
            Label labelSerie2= new Label(Config.LG2, bySerie.getSeriesLabelLg2());
                List<Label> label = new ArrayList<>();
                 if (bySerie.getSeriesLabelLg1()!=null) {
                         label.add(labelSerie1);
                         label.add(labelSerie2); }
            Label labelType1=new Label(Config.LG1, bySerie.getTypeLabelLg1());
            Label labelType2=new Label(Config.LG2, bySerie.getTypeLabelLg2());
            List<Label> labelType=new ArrayList<>();
                if (bySerie.getTypeLabelLg1()!=null) {
                        labelType.add(labelType1);
                        labelType.add(labelType2);}
            Type typeSerie= new Type (bySerie.getTypeId(),labelType,bySerie.getType());
            Label labelFreq1=new Label(Config.LG1, bySerie.getPeriodicityLabelLg1());
            Label labelFreq2=new Label(Config.LG2, bySerie.getPeriodicityLabelLg2());
            List<Label> labelFreq=new ArrayList<>();
                if (bySerie.getPeriodicityLabelLg2()!=null) {
                        labelFreq.add(labelFreq1);
                        labelFreq.add(labelFreq2);}
            Frequence frequenceSerie= new Frequence (bySerie.getPeriodicityId(),labelFreq,bySerie.getPeriodicity());
            Label labelFamille1=new Label(Config.LG1, bySerie.getFamilyLabelLg1());
            Label labelFamille2=new Label(Config.LG2, bySerie.getFamilyLabelLg2());
            List<Label> labelFamille=new ArrayList<>();
                if (bySerie.getFamilyLabelLg2()!=null) {
                    labelFamille.add(labelFamille1);
                    labelFamille.add(labelFamille2);}
            Famille familleSerie= new Famille (bySerie.getFamilyId(),labelFamille,bySerie.getFamily());
            SerieByIdDTO serieByIdDTO= new SerieByIdDTO(altLabelSerie,label,typeSerie,bySerie.getSeries(),bySerie.getId(),frequenceSerie,bySerie.getNbOperation(),familleSerie);
            seriesListDTOS.add(serieByIdDTO);

        }
        return mapper.writeValueAsString(seriesListDTOS);

//        for (int i = 0; i < poguesCodesLists.length(); i++) {
//            JSONObject PoguesCodesList = poguesCodesLists.getJSONObject(i);
//
//            if (PoguesCodesList.has("seriesAltLabelLg1") && PoguesCodesList.has("seriesAltLabelLg2") ) {
//                PoguesCodesList.put("altLabel", this.formatAltLabelPogues(PoguesCodesList));
//            }
//            PoguesCodesList.remove("seriesAltLabelLg1");
//            PoguesCodesList.remove("seriesAltLabelLg2");
//
//            if (PoguesCodesList.has("typeId")) {
//                PoguesCodesList.put("label", this.formatTypeLabelPogues(PoguesCodesList));
//                PoguesCodesList.put("type", this.formatTypePogues(PoguesCodesList));
//
//            }
//            PoguesCodesList.remove("typeLabelLg1");
//            PoguesCodesList.remove("typeLabelLg2");
//            PoguesCodesList.remove("typeId");
//
//
//            if (PoguesCodesList.has("familyId")) {
//                PoguesCodesList.put("label", this.formatFamilyLabelPogues(PoguesCodesList));
//                PoguesCodesList.put("famille", this.formatFamilyPogues(PoguesCodesList));
//            }
//
//            PoguesCodesList.remove("familyLabelLg1");
//            PoguesCodesList.remove("familyLabelLg2");
//            PoguesCodesList.remove("familyId");
//            PoguesCodesList.remove("family");
//
//            if (PoguesCodesList.has("periodicityId")) {
//                PoguesCodesList.put("label", this.formatFrequencyLabelPogues(PoguesCodesList));
//                PoguesCodesList.put("frequence", this.formatFrequencyPogues(PoguesCodesList));
//            }
//
//            PoguesCodesList.remove("periodicityLabelLg1");
//            PoguesCodesList.remove("periodicityLabelLg2");
//            PoguesCodesList.remove("periodicityId");
//            PoguesCodesList.remove("periodicity");
//
//            PoguesCodesList.put("label", this.formatLabelPogues(PoguesCodesList));
//
//            PoguesCodesList.remove("seriesLabelLg1");
//            PoguesCodesList.remove("seriesLabelLg2");
//
//        }
//        return poguesCodesLists.toString();


    }

    @Override
    public String getSerieById(String id) throws RmesException, IOException {
        Map<String, Object> params = initParams();

        params.put("ID", id);
        params.put("LG1", Config.LG1);
        params.put("LG2", Config.LG2);

        JSONObject serieId= repoGestion.getResponseAsObject(buildRequest(Constants.POGUES_QUERIES_PATH, "getCodesList.ftlh", params));

        ObjectMapper jsonResponse =new ObjectMapper();
        SerieById serieById = jsonResponse.readValue(serieId.toString(),SerieById.class);

        ObjectMapper mapper = new ObjectMapper();
        AltLabel altLabelSerie1 = new AltLabel(Config.LG1,serieById.getSeriesAltLabelLg1());
        AltLabel altLabelSerie2 = new AltLabel(Config.LG2, serieById.getSeriesAltLabelLg2());
                 List<AltLabel> altLabelSerie = new ArrayList<>();
                    altLabelSerie.add(altLabelSerie1);
                    altLabelSerie.add(altLabelSerie2);
        Label labelSerie1= new Label(Config.LG1, serieById.getSeriesLabelLg1());
        Label labelSerie2= new Label(Config.LG2, serieById.getSeriesLabelLg2());
                List<Label> label = new ArrayList<>();
                    label.add(labelSerie1);
                    label.add(labelSerie2);
        Label labelType1=new Label(Config.LG1, serieById.getTypeLabelLg1());
        Label labelType2=new Label(Config.LG1, serieById.getTypeLabelLg2());
                List<Label> labelType=new ArrayList<>();
                    labelType.add(labelType1);
                    labelType.add(labelType2);
                        Type typeSerie= new Type (serieById.getTypeId(),labelType,serieById.getType());
        Label labelFreq1=new Label(Config.LG1, serieById.getPeriodicityLabelLg1());
        Label labelFreq2=new Label(Config.LG1, serieById.getPeriodicityLabelLg2());
            List<Label> labelFreq=new ArrayList<>();
                    labelFreq.add(labelFreq1);
                    labelFreq.add(labelFreq2);
                        Frequence frequenceSerie= new Frequence (serieById.getPeriodicityId(),labelFreq,serieById.getPeriodicity());
        Label labelFamille1=new Label(Config.LG1, serieById.getFamilyLabelLg1());
        Label labelFamille2=new Label(Config.LG1, serieById.getFamilyLabelLg2());
            List<Label> labelFamille=new ArrayList<>();
                    labelFamille.add(labelFamille1);
                    labelFamille.add(labelFamille2);
                        Famille familleSerie= new Famille (serieById.getFamilyId(),labelFamille,serieById.getFamily());

        SerieByIdDTO serieByIdDTO= new SerieByIdDTO(altLabelSerie,label,typeSerie,serieById.getSeries(),serieById.getId(),frequenceSerie,serieById.getNbOperation(),familleSerie);

        return mapper.writeValueAsString(serieByIdDTO);

    }

    @Override
    public String getOperationsBySerieId(String id) throws RmesException, IOException {
        Map<String, Object> params = initParams();
        params.put("ID", id);
        JSONArray operationsList = repoGestion.getResponseAsArray(buildRequest(Constants.POGUES_QUERIES_PATH, "getOperationsBySerie.ftlh", params));
        ObjectMapper jsonResponse = new ObjectMapper();
        OperationBySerieId[] operationBySerieId = jsonResponse.readValue(operationsList.toString(), OperationBySerieId[].class);

        ObjectMapper mapper = new ObjectMapper();
        List<OperationsBySerieIdDTO> operationsBySerieIdDTOS= new ArrayList<>();
        for (OperationBySerieId bySerieId : operationBySerieId) {
            Label labelSerie1 = new Label(Config.LG1, bySerieId.getSeriesLabelLg1());
            Label labelSerie2 = new Label(Config.LG2, bySerieId.getSeriesLabelLg2());
                List<Label> label = new ArrayList<>();
                    label.add(labelSerie1);
                    label.add(labelSerie2);
                        Serie serie = new Serie(bySerieId.getSeriesId(), label, bySerieId.getSeries());
            Label labelOperation1 = new Label(Config.LG1, bySerieId.getOperationLabelLg1());
            Label labelOperation2 = new Label(Config.LG2, bySerieId.getOperationLabelLg2());
                List<Label> labelOperation = new ArrayList<>();
                    labelOperation.add(labelOperation1);
                    labelOperation.add(labelOperation2);
            AltLabel altLabelOperation1 = new AltLabel(Config.LG1, bySerieId.getOperationAltLabelLg1());
            AltLabel altLabelOperation2 = new AltLabel(Config.LG2, bySerieId.getOperationAltLabelLg2());
                List<AltLabel> altLabelOperation = new ArrayList<>();
                    altLabelOperation.add(altLabelOperation1);
                    altLabelOperation.add(altLabelOperation2);
                        OperationBySerieIdDTO operationBySerieIdDTO = new OperationBySerieIdDTO(altLabelOperation, labelOperation, bySerieId.getUri(), serie, bySerieId.getOperationId());
            operationsBySerieIdDTOS.add(operationBySerieIdDTO);
        }

        return mapper.writeValueAsString(operationsBySerieIdDTOS);

    }

    @Override
    public String getOperationByCode(String id) throws RmesException, IOException {
        Map<String, Object> params = initParams();
        params.put("ID", id);
        JSONObject operationId = repoGestion.getResponseAsObject(buildRequest(Constants.POGUES_QUERIES_PATH, "getOperationByCode.ftlh", params));

        ObjectMapper jsonResponse =new ObjectMapper();
        OperationById operationById = jsonResponse.readValue(operationId.toString(),OperationById.class);

        ObjectMapper mapper = new ObjectMapper();
        Label labelSerie1= new Label(Config.LG1, operationById.getSeriesLabelLg1());
        Label labelSerie2= new Label(Config.LG2, operationById.getSeriesLabelLg2());
            List<Label> label = new ArrayList<>();
                label.add(labelSerie1);
                label.add(labelSerie2);
        Serie serie= new Serie(operationById.getSeriesId(), label, operationById.getSeries());
        Label labelOperation1=new Label(Config.LG1,operationById.getOperationLabelLg1());
        Label labelOperation2=new Label(Config.LG2,operationById.getOperationLabelLg2());
            List<Label> labelOperation = new ArrayList<>();
                labelOperation.add(labelOperation1);
                labelOperation.add(labelOperation2);
        OperationByIdDTO operationByIdDTO= new OperationByIdDTO(serie,operationById.getId(),labelOperation, operationById.getUri());

        return mapper.writeValueAsString(operationByIdDTO);

    }






    protected JSONArray formatLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("seriesLabelLg1"));
        lg2.put("contenu", obj.getString("seriesLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONArray formatAltLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("seriesAltLabelLg1"));
        lg2.put("contenu", obj.getString("seriesAltLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONArray formatLabelOperationPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("operationLabelLg1"));
        lg2.put("contenu", obj.getString("operationLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONArray formatOperationAltLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("operationAltLabelLg1"));
        lg2.put("contenu", obj.getString("operationAltLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }


    protected JSONObject formatTypePogues(JSONObject obj) {
        JSONObject type = new JSONObject();

        type.put("id",obj.getString("typeId"));
        type.put("uri",obj.getString("type"));
        type.put("label",obj.get("label"));

        return type;
    }

    protected JSONArray formatTypeLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("typeLabelLg1"));
        lg2.put("contenu", obj.getString("typeLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONObject formatFamilyPogues(JSONObject obj) {
        JSONObject family = new JSONObject();

        family.put("id",obj.getString("familyId"));
        family.put("uri",obj.getString("family"));
        family.put("label",obj.get("label"));

        return family;
    }

    protected JSONArray formatFamilyLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("familyLabelLg1"));
        lg2.put("contenu", obj.getString("familyLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONObject formatFrequencyPogues(JSONObject obj) {
        JSONObject frequency = new JSONObject();


        frequency.put("id",obj.getString("periodicityId"));
        frequency.put("uri",obj.getString("periodicity"));
        frequency.put("label",obj.get("label"));

        return frequency;
    }
    protected JSONArray formatFrequencyLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("periodicityLabelLg1"));
        lg2.put("contenu", obj.getString("periodicityLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    protected JSONObject formatOperationSeriePogues(JSONObject obj) {


        JSONObject serie = new JSONObject();

        serie.put("id",obj.getString("seriesId"));
        serie.put("uri",obj.getString("series"));
        serie.put("label",obj.get("label"));

        return serie;
    }

    protected JSONObject formatOperationByCodeSeriePogues(JSONObject obj) {
        JSONObject serie = new JSONObject();

        serie.put("id",obj.getString("seriesId"));
        serie.put("uri",obj.getString("series"));
        serie.put("label",obj.get("label"));
        if (obj.has("altLabel")) {
        serie.put("altLabel",obj.get("altLabel"));}

        return serie;
    }

    protected JSONArray formatOperationSerieLabelPogues(JSONObject obj) {
        JSONArray label = new JSONArray();

        JSONObject lg1 = new JSONObject();
        JSONObject lg2 = new JSONObject();

        lg1.put("langue", Config.LG1);
        lg2.put("langue", Config.LG2);
        lg1.put("contenu", obj.getString("seriesLabelLg1"));
        lg2.put("contenu", obj.getString("seriesLabelLg2"));

        label.put(lg1);
        label.put(lg2);

        return label;
    }

    public Map<String, Object> initParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("CODELIST_GRAPH", Config.BASE_GRAPH+Config.CODELIST_GRAPH);
        params.put("LG1", Config.LG1);
        params.put("LG2", Config.LG2);


        return params;
    }




}
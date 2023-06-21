package fr.insee.rmes.controller;

import fr.insee.rmes.configuration.ConfigOpenApi;
import fr.insee.rmes.services.organisations.OrganisationsServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = OrganisationsController.class)
@Import(ConfigOpenApi.class)
class OrganisationsControllerTest {
    @MockBean
    OrganisationsServices organisationsServices;

    @Test
    void getOrganisationById_withEmptyResponse(@Autowired MockMvc mvc) throws Exception {
        String id = "toto";
        when(organisationsServices.getOrganisationById(id)).thenReturn("");
        //When
        mvc.perform(get("/organisation/"+id+"/"))
        //Then
        .andExpect(status().isNotFound());
    }

    @Test
    void getOrganisationById_withNotEmptyResponse(@Autowired MockMvc mvc) throws Exception {
        String id = "toto";
        when(organisationsServices.getOrganisationById(id)).thenReturn("{}");
        //When
        mvc.perform(get("/organisation/"+id+"/"))
                //Then
                .andExpect(status().isOk());
    }



}
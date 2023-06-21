package fr.insee.rmes.services.organisations;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;


@Import(OrganisationsImpl.class)
class OrganisationsImplTest {


//
//    @Test
//    void UnitTest_initParams() throws Exception{
//        var paramsExpected = new HashMap<>();
//        paramsExpected.put("LG1", Config.LG1);
//        paramsExpected.put("LG2",Config.LG2);
//        var params = OrganisationsImpl.initParams();
//        assertTrue(Maps.difference(paramsExpected,params).areEqual());
//    }

    @Test
    void getAllOrganisations() {
    }

    @Test
    void getOrganisationById() {
    }
}
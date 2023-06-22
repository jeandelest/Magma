package fr.insee.rmes.services.organisations;

import fr.insee.rmes.utils.config.MagmaConfig;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@Import(OrganisationsImpl.class)
class OrganisationsImplTest {



    @Test
    void UnitTest_initParams() throws Exception{
        OrganisationsImpl organisationsImpl = new OrganisationsImpl();
        var params = organisationsImpl.initParams();
        assertTrue("verify LG1 is in params",(params.containsKey("LG1") && params.containsValue(MagmaConfig.getLG1())));
        assertTrue("verify LG2 is in params",(params.containsKey("LG2") && params.containsValue(MagmaConfig.getLG2())));
    }

    @Test
    void getAllOrganisations() {
    }

    @Test
    void getOrganisationById() {
    }
}
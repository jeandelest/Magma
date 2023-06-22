package fr.insee.rmes;


import fr.insee.rmes.configuration.PropertiesLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	private static final String PROPERTIES_FILENAME = "rmeswsgi";

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		System.setProperty("spring.config.name", PROPERTIES_FILENAME);
		configureApplicationBuilder(new  SpringApplicationBuilder()).build().run(args);
	}

	public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
		return springApplicationBuilder.sources(Application.class).listeners(new PropertiesLogger());
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// Application name to find the properties that fits
		// default = "application".
		System.setProperty("spring.config.name", PROPERTIES_FILENAME);

		return configureApplicationBuilder(builder)
				.properties("spring.config.location=classpath:/,file:///${catalina.base}/webapps/"+PROPERTIES_FILENAME+".properties");
	}


	@Bean
	@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	// renvoie le principal mis dans la requête par Keycloak ou un principal avec un
	// "name" null sinon
	public Principal getPrincipal(HttpServletRequest httpRequest) {
		return httpRequest.getUserPrincipal();
	}



}

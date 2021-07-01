package fr.insee.rmes.utils.config.freemarker;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;


public class FreemarkerConfig {

	static final Logger logger = LogManager.getLogger(FreemarkerConfig.class);

	static Configuration cfg ;
	
	public static void init()  {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.27) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		cfg = new Configuration(Configuration.VERSION_2_3_30);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
        
		try {
			MultiTemplateLoader mtl = getTemplateLoader();
			logger.info("Init freemarker templateloader {} , {}", FreemarkerConfig.class.getClassLoader().getResource("queries"), FreemarkerConfig.class.getClassLoader().getResource("xdocreport"));
			cfg.setTemplateLoader(mtl);
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage());
		}

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.FRANCE);

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that will be thrown at you anyway:
		cfg.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
		cfg.setWrapUncheckedExceptions(true);
		
		
	}

	/**
	 * Get template loader
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static MultiTemplateLoader getTemplateLoader() throws IOException, URISyntaxException {
		MultiTemplateLoader mtl = null;

		//Get request files
		FileTemplateLoader ftl1 = new FileTemplateLoader(
				new File(
						FreemarkerConfig.class.getClassLoader()
						.getResource("queries")
						//renvoie null
						.toURI()
						)
				);
		
//		FileTemplateLoader ftl2 = new FileTemplateLoader(new File(FreemarkerConfig.class.getClassLoader().getResource("queries/concepts").toURI()));
//		FileTemplateLoader ftl3 = new FileTemplateLoader(new File(FreemarkerConfig.class.getClassLoader().getResource("queries/codeLists").toURI()));
//		FileTemplateLoader ftl4 = new FileTemplateLoader(new File(FreemarkerConfig.class.getClassLoader().getResource("queries/structures").toURI()));

	//	mtl = new MultiTemplateLoader(new TemplateLoader[] { ftl4, ftl3, ftl2, ftl1 });
		mtl = new MultiTemplateLoader(new TemplateLoader[] { ftl1 });
	
		return mtl;
	}

	public static Configuration getCfg() {
		if (cfg == null) {
			init();
		}
		return cfg;
	}

	

}

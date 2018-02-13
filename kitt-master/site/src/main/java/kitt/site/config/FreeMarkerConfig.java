package kitt.site.config;

import freemarker.template.TemplateModelException;
import kitt.site.ext.freemarker.*;
import kitt.site.ext.freemarker.directive.BlockDirective;
import kitt.site.ext.freemarker.directive.ExtendDirective;
import kitt.site.ext.freemarker.directive.MarkdownDirective;
import kitt.site.ext.freemarker.directive.PeriodDirective;
import kitt.site.ext.freemarker.method.*;
import kitt.site.ext.freemarker.object.StatisticsHashModel;
import kitt.site.service.KittTemplateExceptionHandler;
import kitt.site.service.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;


/**
 * Created by joe on 10/26/14.
 */
@Configuration
@AutoConfigureAfter({FreeMarkerAutoConfiguration.class, ProfileConfig.class})
public class FreeMarkerConfig {
    @Autowired
    protected freemarker.template.Configuration configuration;
    @Autowired
    protected SessionMethod sessionMethod;
    @Autowired
    protected StatisticsHashModel statisticsHashModel;
    @Autowired
    protected Request request;
    @Autowired
    private KittTemplateExceptionHandler kittTemplateExceptionHandler;
    @PostConstruct
    public void setSharedVariable() throws TemplateModelException, IOException {
        //
        //configuration.setTemplateLoader(new HtmlTemplateLoader(new FileTemplateLoader(new File("./src/main/resources/templates"))));

        configuration.setObjectWrapper(new Java8ObjectWrapper());
        configuration.setDateFormat("yyyy-MM-dd");
        configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        configuration.setSharedVariable("extend", new ExtendDirective());
        configuration.setSharedVariable("block", new BlockDirective());
        configuration.setSharedVariable("markdown", new MarkdownDirective());
        configuration.setSharedVariable("session", sessionMethod);
        configuration.setSharedVariable("request", request);
        configuration.setSharedVariable("static", new StaticMethod());
        configuration.setSharedVariable("fs", new FileStorageMethod());
        configuration.setSharedVariable("url", new UrlMethod());
        configuration.setSharedVariable("period", new PeriodDirective());
        configuration.setSharedVariable("statistics", statisticsHashModel);
        configuration.setSharedVariable("subString", new SubStringMethod());
        configuration.setSharedVariable("randNum", new RandNumMethod());
        configuration.setSharedVariable("subsWithSign", new SubsWithSignMethod());
        configuration.setSharedVariable("periodOrTime", new PeriodOrTimeMethod());
        for(String name :new String[]{"page", "cmenu","cpersonal","buyorder","about", "clist","pageTemplate","page2"}){
            configuration.addAutoInclude("init/"+name+".ftl");
        }
        configuration.setClassicCompatible(true);
        configuration.setTemplateExceptionHandler(kittTemplateExceptionHandler);
    }
}


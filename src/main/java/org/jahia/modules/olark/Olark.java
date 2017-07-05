package org.jahia.modules.olark;

import java.util.List;

import net.htmlparser.jericho.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.cache.AggregateCacheFilter;
import org.slf4j.*;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class Olark extends AbstractFilter implements ApplicationListener<ApplicationEvent> {

    private static Logger logger = LoggerFactory.getLogger(Olark.class);

    @Override
    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;
        String siteId = renderContext.getSite().hasProperty("siteId") ? renderContext.getSite().getProperty("siteId").getString() : null;
        if (StringUtils.isNotEmpty(siteId)) {
            Source source = new Source(previousOut);
            OutputDocument outputDocument = new OutputDocument(source);
            if (StringUtils.isNotBlank(siteId) && ! "xxxx-xxx-xx-xxxx".equals(siteId)) {
                List<Element> bodyElementList = source.getAllElements(HTMLElementName.BODY);
                for (Element element : bodyElementList) {
                    final EndTag bodyEndTag = element.getEndTag();
                    String language = renderContext.getMainResourceLocale().getLanguage();
                    String languageConfig = "";

                    if ("en".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"en-US\"); // English (United States)\n";
                    } else if ("de".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"de-DE\"); // Deutsch (Deutschland)\n";
                    } else if ("es".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"es-ES\"); // Spanish (Spain)\n";
                    } else if ("fr".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"fr-FR\"); // Français (France)\n";
                    } else if ("it".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"it-IT\"); // Italian (Italy)\n";
                    } else if ("nl".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"nl-NL\"); // Dutch (Netherlands)\n";
                    } else if ("pt".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"pt-BR\"); // Portuguese (Brazilian)\n";
                    } else if ("ru".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"ru-RU\"); // Russian (Russia)\n";
                    } else if ("sv".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"sv-SE\"); // Swedish (Sweden)\n";
                    } else if ("tr".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"tr-TR\"); // Turkish (Turkey)\n";
                    } else if ("zh".equals(language)) {
                        languageConfig = "olark.configure(\"system.localization\", \"zh-CN\"); // 简体中文（中国）\n";
                    }

                    String oakCode = "<!-- begin olark code -->\n" +
                            "<script type=\"text/javascript\" async>\n" +
                            ";(function(o,l,a,r,k,y){if(o.olark)return;\n" +
                            "r=\"script\";y=l.createElement(r);r=l.getElementsByTagName(r)[0];\n" +
                            "y.async=1;y.src=\"//\"+a;r.parentNode.insertBefore(y,r);\n" +
                            "y=o.olark=function(){k.s.push(arguments);k.t.push(+new Date)};\n" +
                            "y.extend=function(i,j){y(\"extend\",i,j)};\n" +
                            "y.identify=function(i){y(\"identify\",k.i=i)};\n" +
                            "y.configure=function(i,j){y(\"configure\",i,j);k.c[i]=j};\n" +
                            "k=y._={s:[],t:[+new Date],c:{},l:a};\n" +
                            "})(window,document,\"static.olark.com/jsclient/loader.js\");\n" +
                            "/* Add configuration calls below this comment */\n" +
                            "olark.identify('" + siteId + "');\n" +
                            languageConfig +
                            "</script>\n" +
                            "<!-- end olark code -->";

                    outputDocument.replace(bodyEndTag.getBegin(), bodyEndTag.getBegin() + 1, "\n" + AggregateCacheFilter.removeEsiTags(oakCode) + "\n<");
                    break; // avoid to loop if for any reasons multiple body in the page
                }
            }
            out = outputDocument.toString().trim();
        }

        return out;
    }

    public void onApplicationEvent(ApplicationEvent event) {
    }
}


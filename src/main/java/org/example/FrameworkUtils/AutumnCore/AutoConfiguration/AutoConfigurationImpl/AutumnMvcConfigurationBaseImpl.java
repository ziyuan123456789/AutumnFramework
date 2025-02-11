package org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutoConfigurationImpl;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyService;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

/**
 * @author ziyuan
 * @since 2023.11
 */

/**
 * 这也配叫自动装配??,不过好歹这有个条件注解是不是
 * 世界是一个草台班子,这句话说的确实挺对
 */
@MyService
//@MyConditional(MatchClassByInterface.class)
public class AutumnMvcConfigurationBaseImpl implements AutumnMvcConfiguration{
    @Value("baseHtml")
    String baseHtml;

    @Value("404Html")
    String notFoundPage;
    @Override
    public View getMainPage() {
        return new View(baseHtml);
    }

    @Override
    public View get404Page() {
        return new View(notFoundPage);
    }
}

package core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by ooopic on 2017/8/7.
 */
@Configuration
@ComponentScan(basePackageClasses = {AppConfig.class, PersistenceJPAConfig.class})
@EnableJpaRepositories(basePackages = {"dao"})
public class AppConfig {
    // ...
}

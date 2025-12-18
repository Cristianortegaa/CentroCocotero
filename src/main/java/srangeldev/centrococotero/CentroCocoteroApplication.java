package srangeldev.centrococotero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import srangeldev.centrococotero.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
public class CentroCocoteroApplication {


    public static void main(String[] args) {
        SpringApplication.run(CentroCocoteroApplication.class, args);
    }

}

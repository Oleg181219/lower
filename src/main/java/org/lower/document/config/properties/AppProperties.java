package org.lower.document.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.admin")
public class AppProperties {
    private String name;
}

package tech.beinjava.directusmanager.directusmanager.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class DirectusAppProperties {
    @Value("${directus.apiuser.token}")
    private String directusApiUserToken;
}

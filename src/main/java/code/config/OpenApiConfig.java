package code.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Booking Room API",
                version = "1.0",
                description = "Cookie-based JWT Authentication - Login để tự động authenticate!"
        )
)
public class OpenApiConfig {
}

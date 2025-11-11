package com.cemihsankurt.foodAppProject.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. API Bilgilerini Ayarla (Başlık, Açıklama)
                .info(new Info().title("Food App API")
                        .version("v1.0")
                        .description("Cemihsan Kurt Food Application Backend API Dokümantasyonu"))

                // 2. Global Güvenlik Gereksinimi Ekle
                // (Tüm endpoint'lerin bu 'bearerAuth'u kullanmasını söyle)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // 3. JWT Bearer Token Şemasını Tanımla
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP) // Tip: HTTP
                                                .scheme("bearer") // Şema: Bearer
                                                .bearerFormat("JWT") // Format: JWT
                                                .in(SecurityScheme.In.HEADER) // Nerede: Header'da
                                                .description("JWT Authorization header using the Bearer scheme.")
                                )
                );
    }
}

package com.example.authms.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Node.js CORS ayarlarını buraya ekleyelim
        configuration.setAllowedOrigins(List.of("*")); // Herkese izin veriliyor
        configuration.setAllowedMethods(List.of("GET", "HEAD", "OPTIONS", "POST", "PUT")); // Belirtilen metodlar
        configuration.setAllowedHeaders(List.of("Access-Control-Allow-Headers", "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("Custom-Header1", "Custom-Header2")); // İsteğe bağlı, Node.js CORS'ta yok ama burada bırakılmış
        configuration.setAllowCredentials(true); // Kimlik bilgileri gönderimine izin ver
        configuration.setMaxAge(2592000L); // 30 gün

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }
}

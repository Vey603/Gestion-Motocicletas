package com.gestion_motocicletas.soap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

import java.util.Properties;

@Configuration
public class SoapConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.gestion_motocicletas.soap.dto");
        return marshaller;
    }

    @Bean(name = "motocicletas")
    public SimpleWsdl11Definition motocicletasWsdl() {
        return new SimpleWsdl11Definition(
                new ClassPathResource("wsdl/motocicletas.wsdl"));
    }

    @Bean
    public SoapFaultMappingExceptionResolver soapFaultExceptionResolver() {
        SoapFaultMappingExceptionResolver resolver = new SoapFaultMappingExceptionResolver();

        Properties mappings = new Properties();
        mappings.setProperty("MotocicletaNoEncontradaException", "CLIENT");
        mappings.setProperty("MotocicletaInvalidaException", "CLIENT");
        resolver.setExceptionMappings(mappings);

        SoapFaultDefinition defaultFault = new SoapFaultDefinition();
        defaultFault.setFaultCode(SoapFaultDefinition.SERVER);
        defaultFault.setFaultStringOrReason("Error interno del servidor");
        resolver.setDefaultFault(defaultFault);

        resolver.setOrder(1);
        return resolver;
    }
}

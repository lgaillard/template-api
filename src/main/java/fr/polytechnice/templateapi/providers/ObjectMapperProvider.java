package fr.polytechnice.templateapi.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.polytechnice.templateapi.utils.CustomObjectMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Object Mapper Provider
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    public static final ObjectMapper objectMapper = new CustomObjectMapper();

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}

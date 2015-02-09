/*
 * Copyright 2015 Trento Rise.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.commons.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import java.io.IOException;
import javax.annotation.Nullable;

/**
 * Utility class to ease working with Jackson.
 *
 * @author David Leoni
 */
public class Jacksonizer {

    /**
     * The singleton instance of the Jacksonizer
     */
    private static final Jacksonizer INSTANCE = new Jacksonizer();

    @Nullable
    private static ObjectMapper objectMapper;

    /**
     * Returns a clone of the json object mapper used internally.
     */
    public ObjectMapper makeJacksonMapper() {
        return getObjectMapper().copy();
    }

    /**
     * Returns the internal object mapper, lazily creating it if necessary
     */
    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false) // let's be tolerant
                    .configure(MapperFeature.USE_GETTERS_AS_SETTERS,
                            false); // not good for unmodifiable collections            
            objectMapper.registerModule(new GuavaModule());
            objectMapper.registerModule(new OdtCommonsModule());
        }
        return objectMapper;
    }

    /**
     * Returns a JSON representation of the provided object.
     *
     * @return the provided object in JSON format
     * @throws IllegalArgumentException on json error.
     */
    public String toJson(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        }
        catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Couldn't serialize provided object!", ex);
        }
    }

    /**
     * Reconstructs an object from provided json representation.
     *
     * @param clazz the Java class of the object to reconstruct.
     *
     * @throws IllegalArgumentException on json error.
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return getObjectMapper().readValue(jsonString, clazz);
        }
        catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Couldn't deserialize provided SemText json: " + jsonString, ex);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Couldn't deserialize provided SemText json: " + jsonString, ex);
        }
    }

    /**
     * Factory method, returning the Jacksonizer with the default internal
     * Jackson object mapper.
     */
    public static Jacksonizer of() {
        return INSTANCE;
    }
}

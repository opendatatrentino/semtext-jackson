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
package eu.trentorise.opendata.semtext.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.commons.SemVersion;
import javax.annotation.Nullable;

/**
 *
 * @author David Leoni
 */
public class Jacksonizer {

    private static Jacksonizer INSTANCE = new Jacksonizer();
    
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
            objectMapper = eu.trentorise.opendata.commons.jackson.Jacksonizer.of().makeJacksonMapper();
            
        }
        return objectMapper;
    }

    
    /**
     * Factory method
     * @return 
     */
    public static Jacksonizer of(){
        return INSTANCE;
    }
}

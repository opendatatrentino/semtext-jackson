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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableListMultimap;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.LocalizedString;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.commons.SemVersion;
import java.io.IOException;
import java.util.Locale;

/**
 * A module for handling Odt commons objects Dict and LocalizedString with
 * Jackson JSON serialization framework.
 *
 * @author David Leoni <david.leoni@unitn.it>
 */
public final class OdtCommonsModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    /**
     * Returns the version of the module by reading it from build info at the
     * root of provided class resources.
     */
    public static Version readJacksonVersion(Class clazz) {
        SemVersion semver = SemVersion.of(OdtUtils.readBuildInfo(OdtCommonsModule.class).getVersion());
        return new Version(semver.getMajor(),
                semver.getMinor(),
                semver.getPatch(),
                semver.getPreReleaseVersion(),
                "eu.trentorise.opendata.commons.jackson",
                "odt-commons-jackson");
    }

    private static abstract class JacksonLocalizedString {

        @JsonCreator
        public static LocalizedString of(@JsonProperty("string") String string, @JsonProperty("locale") Locale locale) {
            return null; // just because the method can't be abstract.
        }

    }

    /**
     * Creates the module and registers all the needed serializaers and
     * deserializers
     */
    public OdtCommonsModule() {
        super("odt-commons-jackson", readJacksonVersion(OdtCommonsModule.class));

        // todo register version in some way
        addSerializer(Dict.class, new StdSerializer<Dict>(Dict.class) {
            @Override
            public void serialize(Dict value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeObject(value.asMultimap());
            }
        });

        addDeserializer(Dict.class, new StdDeserializer<Dict>(Dict.class) {

            @Override
            public Dict deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                TypeReference ref = new TypeReference<ImmutableListMultimap<Locale, String>>() {
                };
                return Dict.of((ImmutableListMultimap) jp.readValueAs(ref));
            }
        });

        setMixInAnnotation(LocalizedString.class, JacksonLocalizedString.class);

    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // it's like this in Guava module!
    }

    @Override
    public boolean equals(Object o
    ) {
        return this == o; // it's like this in Guava module!
    }

}

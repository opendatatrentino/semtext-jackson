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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.jackson.Jacksonizer;
import eu.trentorise.opendata.commons.jackson.OdtCommonsModule;
import eu.trentorise.opendata.semtext.Meaning;
import eu.trentorise.opendata.semtext.MeaningKind;
import eu.trentorise.opendata.semtext.MeaningStatus;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semtext.Sentence;
import eu.trentorise.opendata.semtext.Term;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * A module for handling semtext objects with Jackson JSON serialization
 * framework. In order to work properly the module needs you to register also
 * OdtCommonModule and GuavaModule (both are in separate maven packages)
 *
 * @author David Leoni <david.leoni@unitn.it>
 */
public final class SemTextModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    private static abstract class JacksonMeaning {

        @JsonCreator
        public static Meaning of(
                @JsonProperty("id") String id,
                @JsonProperty("kind") MeaningKind kind,
                @JsonProperty("probability") double probability,
                @JsonProperty("name") Dict name,
                @JsonDeserialize(using = MetadataDeserializer.class)
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;
        }
    }

    private static abstract class JacksonTerm {

        @JsonCreator
        public static Term of(
                @JsonProperty("start") int start,
                @JsonProperty("end") int end,
                @JsonProperty("meaningStatus") MeaningStatus meaningStatus,
                @JsonProperty("selectedMeaning") @Nullable Meaning selectedMeaning,
                @JsonProperty("meanings") Iterable<Meaning> meanings,
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;

        }
    }

    private static abstract class JacksonSentence {

        @JsonCreator
        public static Sentence of(
                @JsonProperty("start") int start,
                @JsonProperty("end") int end,
                @JsonProperty("terms") Iterable<Term> terms,
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;
        }
    }

    private static abstract class JacksonSemText {
//@JsonProperty("")

        @JsonCreator
        public static SemText ofSentences(
                @JsonProperty("text") String text,
                @JsonProperty("locale") Locale locale,
                @JsonProperty("sentences") Iterable<Sentence> sentences,
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;
        }

    }

    /**
     * Creates the module and registers all the needed serializaers and
     * deserializers
     */
    public SemTextModule() {
        super("odt-commons-jackson", OdtCommonsModule.readJacksonVersion(SemTextModule.class));

        setMixInAnnotation(Meaning.class, JacksonMeaning.class);
        setMixInAnnotation(Term.class, JacksonTerm.class);
        setMixInAnnotation(Sentence.class, JacksonSentence.class);
        setMixInAnnotation(SemText.class, JacksonSemText.class);

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

    /**
     * Returns a jackson json object mapper fully configured to work with
     * semtext objects.
     */
    public static ObjectMapper makeJacksonMapper() {
        ObjectMapper objectMapper = Jacksonizer.of().makeJacksonMapper();
        objectMapper.registerModule(new SemTextModule());
        return objectMapper;
    }
    
    /**
     * Registers in the provided object mapper the jackson semtext module and
     * also the required odt commons and guava modules
     */
    public static void registerAll(ObjectMapper om) {
        om.registerModule(new GuavaModule());
        om.registerModule(new OdtCommonsModule());
        om.registerModule(new SemTextModule());
    }    

}

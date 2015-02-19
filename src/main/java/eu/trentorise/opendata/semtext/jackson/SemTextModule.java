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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.NotFoundException;
import static eu.trentorise.opendata.commons.OdtUtils.checkNotEmpty;
import eu.trentorise.opendata.commons.jackson.Jacksonizer;
import eu.trentorise.opendata.commons.jackson.OdtCommonsModule;
import eu.trentorise.opendata.semtext.HasMetadata;
import eu.trentorise.opendata.semtext.Meaning;
import eu.trentorise.opendata.semtext.MeaningKind;
import eu.trentorise.opendata.semtext.MeaningStatus;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semtext.Sentence;
import eu.trentorise.opendata.semtext.Term;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * A module for handling semtext objects with Jackson JSON serialization
 * framework. In order to work properly the module needs you to register also
 * OdtCommonModule and GuavaModule (both are in separate maven packages). For
 * properly deserializing metadata you also need to register it by calling {@link #registerMetadata(java.lang.Class, java.lang.String, java.lang.Class)
 * }
 *
 * @author David Leoni <david.leoni@unitn.it>
 */
public final class SemTextModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    /**
     * Map for Class name, namespace, and jacksonizable class
     */
    private static final Map<String, Map<String, TypeReference>> metadataNamespaces = new HashMap();

    private static class MeaningMetadataDeserializer extends MetadataDeserializer {

        private MeaningMetadataDeserializer() {
            super(Meaning.class);
        }
    }

    private static abstract class JacksonMeaning {

        @JsonCreator
        public static Meaning of(
                @JsonProperty("id") String id,
                @JsonProperty("kind") MeaningKind kind,
                @JsonProperty("probability") double probability,
                @JsonProperty("name") Dict name,
                @JsonDeserialize(using = MeaningMetadataDeserializer.class)
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;
        }
    }

    private static class TermMetadataDeserializer extends MetadataDeserializer {

        private TermMetadataDeserializer() {
            super(Term.class);
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
                @JsonDeserialize(using = TermMetadataDeserializer.class)
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;

        }
    }

    private static class SentenceMetadataDeserializer extends MetadataDeserializer {

        private SentenceMetadataDeserializer() {
            super(Sentence.class);
        }
    }

    private static abstract class JacksonSentence {

        @JsonCreator
        public static Sentence of(
                @JsonProperty("start") int start,
                @JsonProperty("end") int end,
                @JsonProperty("terms") Iterable<Term> terms,
                @JsonDeserialize(using = SentenceMetadataDeserializer.class)
                @JsonProperty("metadata") Map<String, ?> metadata) {
            return null;
        }
    }

    private static class SemTextMetadataDeserializer extends MetadataDeserializer {

        private SemTextMetadataDeserializer() {
            super(SemText.class);
        }
    }

    private static abstract class JacksonSemText {

        @JsonCreator
        public static SemText ofSentences(
                @JsonProperty("locale") Locale locale,
                @JsonProperty("text") String text,
                @JsonProperty("sentences") Iterable<Sentence> sentences,
                @JsonDeserialize(using = SemTextMetadataDeserializer.class)
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
     * Registers in the provided object mapper the jackson semtext module and
     * also the required odt commons and guava modules.
     */
    public static void registerModulesInto(ObjectMapper om) {
        om.registerModule(new GuavaModule());
        om.registerModule(new OdtCommonsModule());
        om.registerModule(new SemTextModule());
    }

    /**
     * Registers the provided namespace to the corresponding metadata class, so
     * Jackson will know how to deserialize objects under that namespace. The
     * mapping will be valid only for the specified metadata holder class.
     *
     * NOTE: If your class has generics (like List<String>) , use
     * {@link #registerMetadata(java.lang.Class, java.lang.String, com.fasterxml.jackson.core.type.TypeReference)}
     * instead.
     *
     * @param hasMetadataClass Either {@link eu.trentorise.opendata.semtext.SemText}, {@link eu.trentorise.opendata.semtext.Sentence},
     * {@link eu.trentorise.opendata.semtext.Term} or
     * {@link eu.trentorise.opendata.semtext.Meaning}
     * @param namespace a non-empty string
     * @param metadataClass any class that Jackson can serialize and
     * deserialize.
     */
    public static <T> void registerMetadata(Class<? extends HasMetadata> hasMetadataClass, String namespace, final Class<T> metadataClass) {
        registerMetadata(hasMetadataClass, namespace, new TypeReference<T>() {
            @Override
            public Type getType() {
                return metadataClass;
            }
        });

    }

    /**
     * Registers the provided namespace to the corresponding metadata type reference,
     * so Jackson will know how to deserialize objects under that namespace. The
     * mapping will be valid only for the specified metadata holder class.
     *
     * @param hasMetadataClass Either {@link eu.trentorise.opendata.semtext.SemText}, {@link eu.trentorise.opendata.semtext.Sentence},
     * {@link eu.trentorise.opendata.semtext.Term} or
     * {@link eu.trentorise.opendata.semtext.Meaning}
     * @param namespace a non-empty string
     * @param metadataClass any class that Jackson can serialize and
     * deserialize.
     */
    public static void registerMetadata(Class<? extends HasMetadata> hasMetadataClass, String namespace, TypeReference metadataClass) {
        checkNotNull(hasMetadataClass);
        checkNotEmpty(namespace, "Invalid metadata namespace!");
        checkNotNull(metadataClass);

        Map<String, TypeReference> candidateMapping = metadataNamespaces.get(hasMetadataClass.getName());
        Map<String, TypeReference> namespaceMapping;
        if (candidateMapping == null) {
            namespaceMapping = new HashMap();
        } else {
            namespaceMapping = candidateMapping;
        }

        namespaceMapping.put(namespace, metadataClass);
        metadataNamespaces.put(hasMetadataClass.getName(), namespaceMapping);
    }

    /**
     * Unregisters all the previously registered metadata namespaces.
     */
    public static void clearMetadata() {
        metadataNamespaces.clear();
    }

    /**
     * Returns the namespaces registered for the given class that can hold
     * metadata. If nothing is found an empty set is returned.
     *
     * @param hasMetadataClass Either {@link eu.trentorise.opendata.semtext.SemText}, {@link eu.trentorise.opendata.semtext.Sentence},
     * {@link eu.trentorise.opendata.semtext.Term} or
     * {@link eu.trentorise.opendata.semtext.Meaning}
     */
    public static ImmutableSet<String> getMetadataNamespaces(Class<? extends HasMetadata> hasMetadataClass) {
        Map<String, TypeReference> map = metadataNamespaces.get(hasMetadataClass.getName());
        if (map == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(map.keySet());
        }
    }

    /**
     * Returns the jacksonizable class associated to the provided metadata
     * holder class and namespace
     *
     * @param hasMetadataClass Either {@link eu.trentorise.opendata.semtext.SemText}, {@link eu.trentorise.opendata.semtext.Sentence},
     * {@link eu.trentorise.opendata.semtext.Term} or
     * {@link eu.trentorise.opendata.semtext.Meaning}
     * @param namespace a registered namespace
     * @throws eu.trentorise.opendata.commons.NotFoundException if the namespace
     * was not registered
     */
    public static TypeReference getMetadataTypeReference(Class<? extends HasMetadata> hasMetadataClass, String namespace) {
        checkNotNull(hasMetadataClass);
        checkNotEmpty(namespace, "Invalid metadata namespace!");
        Map<String, TypeReference> mapping = metadataNamespaces.get(hasMetadataClass.getName());
        if (mapping != null) {
            TypeReference clazz = mapping.get(namespace);
            if (clazz != null) {
                return clazz;
            }
        }
        throw new NotFoundException("Couldn't find any class registered under class " + hasMetadataClass.getName() + " and namespace " + namespace);
    }

}

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

import com.fasterxml.jackson.core.type.TypeReference;
import eu.trentorise.opendata.semtext.HasMetadata;
import javax.annotation.Nullable;

/**
 * Exception for problems occurring during SemText metadata deserialization.
 * @author David Leoni
 */
public class SemTextMetadataException extends RuntimeException {

    @Nullable
    private Class<? extends HasMetadata> hasMetadataClass;
    @Nullable
    private TypeReference typeRef;
    @Nullable
    private String namespace;

    /**
     *
     * @param msg The error message
     * @param hasMetadataClass Claas under which metadata was being instantiated
     * @param namespace Namespace of the metadata
     * @param typeRef type of the object which was attempted to instantiate. If
     * unknown put null.
     */
    public SemTextMetadataException(String msg, Class<? extends HasMetadata> hasMetadataClass, String namespace, @Nullable TypeReference typeRef) {
        super(msg);
        this.hasMetadataClass = hasMetadataClass;
        this.namespace = namespace;
        this.typeRef = typeRef;
    }

    /**
     *
     * @param msg The error message
     * @param hasMetadataClass Claas under which metadata was being instantiated
     * @param namespace Namespace of the metadata
     * @param typeRef type of the object which was attempted to instantiate. If
     * unknown put null.
     */
    public SemTextMetadataException(String msg,
            @Nullable Class<? extends HasMetadata> hasMetadataClass,
            @Nullable String namespace,
            @Nullable TypeReference typeRef,
            Throwable thrwbl) {
        super(msg, thrwbl);
        this.hasMetadataClass = hasMetadataClass;
        this.namespace = namespace;
        this.typeRef = typeRef;
    }

    /**
     * Returns the class holding metadata.
     */    
    public @Nullable
    Class<? extends HasMetadata> getHasMetadataClass() {
        return hasMetadataClass;
    }

    /**
     * Returns the metadata namespace
     */
    public @Nullable
    String getNamespace() {
        return namespace;
    }

    /**
     * Returns the type of the object which was attempted to instantiate. If
     * unknown put null.
     *
     */
    @Nullable
    public TypeReference getTypeRef() {
        return typeRef;
    }

    /**
     * Appends namespace and metadata class to
     */
    @Override
    public String getMessage() {
        String foundType = typeRef == null ? "" : "failed instantiation " + typeRef.getType() + " in ";
        return super.getMessage() + foundType + "namespace: \"" + namespace + "\" in object of class \"" + hasMetadataClass.getName() + "\"";
    }

}

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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author David Leoni
 */
public class MetadataDeserializer extends StdDeserializer<Map<String, Object>> {

    private MetadataDeserializer() {
        super(Map.class);
    }

    public MetadataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ImmutableMap.Builder<String, Object> retb = ImmutableMap.builder();

        while (jp.nextToken() != JsonToken.END_OBJECT) {

            String namespace = jp.getCurrentName();
            // current token is "name",
            // move to next, which is "name"'s value
            jp.nextToken();
            MyMetadata myMetadata = jp.readValueAs(MyMetadata.class); // display mkyong
            assert (myMetadata != null);
            retb.put(namespace, myMetadata);
        }

        return retb.build();
    }

}

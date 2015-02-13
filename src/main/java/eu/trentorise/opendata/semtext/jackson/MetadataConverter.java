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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;



/**
 *
 * @author David Leoni
 */
class MetadataConverter extends StdConverter<JsonNode, ImmutableMap<String, Object>> {

    private static ObjectMapper om = SemTextModule.makeJacksonMapper();

    @Override
    public ImmutableMap<String, Object> convert(JsonNode map) {
        /*Collection<?> values = map.values();
        if (values.isEmpty()) {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<String, Object> retb = ImmutableMap.builder();

        if (map.containsKey("testns")) {

            try {
                MyMetadata md = om.treeToValue(map.get("testns"), MyMetadata.class);

                retb.put("testns", md);
            }
            catch (JsonProcessingException ex) {
                throw new RuntimeException(ex); // todo review
            }

        }

        return retb.build();*/
        return ImmutableMap.of();
    }

}

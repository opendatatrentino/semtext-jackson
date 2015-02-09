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
package eu.trentorise.opendata.commons.test.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.LocalizedString;
import eu.trentorise.opendata.commons.OdtConfig;
import eu.trentorise.opendata.commons.jackson.Jacksonizer;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class JacksonizerTest {

    private static final Logger logger = Logger.getLogger(JacksonizerTest.class.getName());

    @BeforeClass
    public static void beforeClass() {
        OdtConfig.of(JacksonizerTest.class).loadLogConfig();
    }

    @Test
    public void testDict() throws JsonProcessingException, IOException {
        Jacksonizer jm = Jacksonizer.of();

        ObjectMapper om = jm.makeJacksonMapper();
        JacksonTest.testJsonConv(om, Dict.of("a", "b"), logger);
        JacksonTest.testJsonConv(om, Dict.of(Locale.FRENCH, "a", "b"), logger);

        Dict dict = om.readValue("{}", Dict.class);
        assertEquals(Dict.of(), dict);

        try {
            Dict dict_2 = om.readValue("{\"it\":null}", Dict.class);
            Assert.fail("Should have validated the dict!");
        }
        catch (Exception ex) {

        }
    }

    @Test
    public void testLocalizedString() throws JsonProcessingException, IOException {
        Jacksonizer jm = Jacksonizer.of();

        ObjectMapper om = jm.makeJacksonMapper();
        JacksonTest.testJsonConv(om, LocalizedString.of("a", Locale.FRENCH), logger);

        try {
            LocalizedString locString = om.readValue("{\"string\":null, \"locale\":\"en\"}", LocalizedString.class);
            Assert.fail("Should have validated the string!");
        }
        catch (Exception ex) {

        }
    }
    
}

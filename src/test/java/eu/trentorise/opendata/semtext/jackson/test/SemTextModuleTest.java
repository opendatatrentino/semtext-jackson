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
package eu.trentorise.opendata.semtext.jackson.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.OdtConfig;
import eu.trentorise.opendata.semtext.Meaning;
import eu.trentorise.opendata.semtext.MeaningKind;
import eu.trentorise.opendata.semtext.MeaningStatus;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semtext.Sentence;
import eu.trentorise.opendata.semtext.Term;
import java.util.Locale;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.trentorise.opendata.commons.test.jackson.JacksonTest;
import eu.trentorise.opendata.semtext.jackson.SemTextModule;
import java.io.IOException;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;

/**
 * 
 * @author David Leoni
 */
public class SemTextModuleTest {

    private static final Logger logger = Logger.getLogger(SemTextModuleTest.class.getName());

    @BeforeClass
    public static void beforeClass() {
        OdtConfig.of(SemTextModuleTest.class).loadLogConfig();
    }

    @Test
    public void testMapper() throws IOException {        
        ObjectMapper om = SemTextModule.makeJacksonMapper();

        try {
            om.readValue("{\"start\":2, \"end\":1}", Term.class);
            Assert.fail("Should have failed because of missing attributes!");
        }
        catch (Exception ex) {

        }

        Meaning m1 = Meaning.of(
                "a",
                MeaningKind.ENTITY,
                0.2,
                Dict.of(Locale.ITALIAN, "a"),
                ImmutableMap.of("a", Dict.of("s")));
        Meaning m2 = Meaning.of(
                "b",
                MeaningKind.ENTITY,
                0.2,
                Dict.of(Locale.ITALIAN, "a"),
                ImmutableMap.of("b", Dict.of("s")));

        JacksonTest.testJsonConv(om, Meaning.of("a", MeaningKind.CONCEPT, 0.2), logger);

        Term term = Term.of(
                0, 
                2, 
                MeaningStatus.SELECTED, 
                m1, 
                ImmutableList.of(m1, m2), 
                ImmutableMap.of("c", 3));

        JacksonTest.testJsonConv(om,
                SemText.ofSentences(
                        "abcdefghilmno",
                        Locale.ITALIAN,
                        ImmutableList.of(Sentence.of(0, 7, term)),
                        ImmutableMap.of("a", 9)), logger);

    }

    /**
     * These ones for some reason don't work....
     */
    @Test
    @Ignore
    public void testEmptyConstructors() throws IOException {
        ObjectMapper om = SemTextModule.makeJacksonMapper();
        assertEquals(Meaning.of(), om.readValue("{}", Meaning.class));
        assertEquals(SemText.of(), om.readValue("{}", SemText.class));
    }
}

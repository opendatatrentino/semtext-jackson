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


import eu.trentorise.opendata.commons.OdtConfig;
import eu.trentorise.opendata.semtext.jackson.Jacksonizer;
import eu.trentorise.opendata.semtext.Meaning;
import eu.trentorise.opendata.semtext.MeaningKind;
import eu.trentorise.opendata.semtext.MeaningStatus;
import eu.trentorise.opendata.semtext.SemText;
import eu.trentorise.opendata.semtext.Sentence;
import eu.trentorise.opendata.semtext.Term;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author David Leoni
 */
public class JacksonMapperTest {
    
    private static final Logger logger = Logger.getLogger(JacksonMapperTest.class.getName());

    @BeforeClass
    public static void beforeClass(){
        OdtConfig.of(JacksonMapperTest.class).loadLogConfig();
    }
    
    @Test
    public void testMapper(){
        Jacksonizer jm = Jacksonizer.of();
        
        Term term = Term.of(0,2,MeaningStatus.SELECTED, Meaning.of("a", MeaningKind.ENTITY, 0.2));  
        String json = jm.toJson(SemText.of("abcdefghilmno", Locale.ITALIAN, Sentence.of(0, 7, term)));
        logger.log(Level.FINE, "json = {0}", json);
    }
}

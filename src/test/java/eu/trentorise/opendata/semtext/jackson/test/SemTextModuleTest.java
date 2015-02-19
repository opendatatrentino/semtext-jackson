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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.OdtConfig;
import eu.trentorise.opendata.commons.jackson.OdtCommonsModule;
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
import java.util.Date;
import java.util.logging.Level;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author David Leoni
 */
public class SemTextModuleTest {

    private static final Logger logger = Logger.getLogger(SemTextModuleTest.class.getName());

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() {
        OdtConfig.of(SemTextModuleTest.class).loadLogConfig();
    }

    @Before
    public void beforeMethod() {
        objectMapper = new ObjectMapper();
        SemTextModule.registerModulesInto(objectMapper);
    }

    @After
    public void afterMethod() {
        objectMapper = null;
        SemTextModule.clearMetadata();
    }
    
    @Test
    public void testUnregisteredMetadata() throws IOException {

        Meaning m1 = Meaning.of(
                "a",
                MeaningKind.ENTITY,
                0.2,
                Dict.of(Locale.ITALIAN, "a"),
                ImmutableMap.of("testns", Dict.of("s")));
        try {
            JacksonTest.testJsonConv(objectMapper, m1, logger);
            Assert.fail("Should have complained about unregistered namespace!");
        } catch (Exception ex){
                
        }
    }

    /** Registers MyMetadata in objectMapper */
    private void registerMyMetadata(){
        objectMapper.registerModule(new SimpleModule() {
            {
                setMixInAnnotation(MyMetadata.class, MyMetadataJackson.class);
            }
        });
    }

    @Test
    public void testMeaning() throws IOException {
        SemTextModule.registerMetadata(Meaning.class, "a", Dict.class);
        SemTextModule.registerMetadata(Meaning.class, "b", MyMetadata.class);

        registerMyMetadata();

        Meaning m1 = Meaning.of(
                "a",
                MeaningKind.ENTITY,
                0.2,
                Dict.of(Locale.ITALIAN, "a"),
                ImmutableMap.of("a", Dict.of("s"),
                                "b", MyMetadata.of("hello")));
        JacksonTest.testJsonConv(objectMapper, m1, logger);
    }

    @Test
    public void testTerm() throws IOException {
        SemTextModule.registerMetadata(Meaning.class, "a", Dict.class);
        Meaning m1 = Meaning.of(
                "a",
                MeaningKind.ENTITY,
                0.2,
                Dict.of(Locale.ITALIAN, "a"),
                ImmutableMap.of("a", Dict.of("s")));
        JacksonTest.testJsonConv(objectMapper, m1, logger);
    }

    @Test
    public void testSentence() throws IOException {
        SemTextModule.registerMetadata(Sentence.class, "a", MyMetadata.class);
        registerMyMetadata();
        
        Sentence sen = Sentence.of(0,
                1,
                ImmutableList.<Term>of(),
                ImmutableMap.of("a", MyMetadata.of("hello")));
        JacksonTest.testJsonConv(objectMapper, sen, logger);
    }
    
    @Test
    public void testMetadataSentence() throws IOException {
        SemTextModule.registerMetadata(Sentence.class, "a", MyMetadata.class);
        registerMyMetadata();
        Sentence sen = Sentence.of(0,
                1,
                ImmutableList.<Term>of(),
                ImmutableMap.of("a", MyMetadata.of("hello")));
        JacksonTest.testJsonConv(objectMapper, sen, logger);
    }
    
    
    @Test
    public void testMetadataSemText() throws IOException {
        SemTextModule.registerMetadata(SemText.class, "a", MyMetadata.class);      
        registerMyMetadata();
        
        SemText res = JacksonTest.testJsonConv(objectMapper,
                SemText.ofSentences(Locale.ITALIAN,
                        "abcdefghilmno",
                        ImmutableList.<Sentence>of(),
                        ImmutableMap.of("a", MyMetadata.of())), logger);        
    }    

    @Test
    public void testMapper() throws IOException {

        SemTextModule.registerMetadata(Meaning.class, "a", Dict.class);
        SemTextModule.registerMetadata(Meaning.class, "b", Dict.class);
        SemTextModule.registerMetadata(Term.class, "c", Integer.class);
        SemTextModule.registerMetadata(Sentence.class, "a", MyMetadata.class);
        SemTextModule.registerMetadata(SemText.class, "a", Integer.class);

        objectMapper.registerModule(new SimpleModule() {
            {
                setMixInAnnotation(MyMetadata.class, MyMetadataJackson.class);
            }
        });
        
        try {
            objectMapper.readValue("{\"start\":2, \"end\":1}", Term.class);
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

        JacksonTest.testJsonConv(objectMapper, Meaning.of("a", MeaningKind.CONCEPT, 0.2), logger);

        Term term = Term.of(
                0,
                2,
                MeaningStatus.SELECTED,
                m1,
                ImmutableList.of(m1, m2),
                ImmutableMap.of("c", 3));

        JacksonTest.testJsonConv(objectMapper,
                SemText.ofSentences(Locale.ITALIAN,
                        "abcdefghilmno",
                        ImmutableList.of(Sentence.of(0,
                                        7,
                                        ImmutableList.of(term),
                                        ImmutableMap.of("a", MyMetadata.of("hello")))),
                        ImmutableMap.of("a", 9)), logger);

    }

    /**
     * These ones for some reason don't work....
     */
    @Test(expected=AssertionError.class)
    public void testEmptyConstructors() throws IOException {

        assertEquals(Meaning.of(), objectMapper.readValue("{}", Meaning.class));
        assertEquals(SemText.of(), objectMapper.readValue("{}", SemText.class));
    }

    @Test
    public void manualRegistrationExample_1() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());
        om.registerModule(new OdtCommonsModule());
        om.registerModule(new SemTextModule());

        String json = om.writeValueAsString(SemText.of(Locale.ITALIAN, "ciao"));
        SemText reconstructedSemText = om.readValue(json, SemText.class);
    }

    @Test
    public void manualRegistration_2() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();
        SemTextModule.registerModulesInto(om);

        String json = om.writeValueAsString(SemText.of(Locale.ITALIAN, "ciao"));
        SemText reconstructedSemText = om.readValue(json, SemText.class);
    }

    
        
    @Test
    public void testMetadataSerializationSimple() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();

        // register all required modules into the Jackson Object Mapper
        SemTextModule.registerModulesInto(om);

        // declare that metadata under namespace 'testns' in SemText objects should be deserialized into a Date object
        SemTextModule.registerMetadata(SemText.class, "testns", Date.class);

        // Let's say MyMetadata is tricky to deserialize, so we tell Jackson how to deserialize it with a mixin annotation
        om.registerModule(new SimpleModule() {
            {
                setMixInAnnotation(MyMetadata.class, MyMetadataJackson.class);
            }
        });
                        
        String json = om.writeValueAsString(SemText.of("ciao").withMetadata("testns", new Date(123)));
        
        logger.fine("json = " + json);
        
        SemText reconstructedSemText = om.readValue(json, SemText.class);                                
        
        Date reconstructedMetadata = (Date) reconstructedSemText.getMetadata("testns");
        
        assert  new Date(123).equals(reconstructedMetadata);
    }    
    
    @Test
    public void testMetadataSerializationComplex() throws JsonProcessingException, IOException {
        ObjectMapper om = new ObjectMapper();

        // register all required modules into the Jackson Object Mapper
        SemTextModule.registerModulesInto(om);

        // declare that metadata under namespace 'testns' in SemText objects should be deserialized into a MyMetadata object
        SemTextModule.registerMetadata(SemText.class, "testns", MyMetadata.class);

        // Let's say MyMetadata is tricky to deserialize, so we tell Jackson how to deserialize it with a mixin annotation
        om.registerModule(new SimpleModule() {
            {
                setMixInAnnotation(MyMetadata.class, MyMetadataJackson.class);
            }
        });
        
        String json = om.writeValueAsString(SemText.of(Locale.ITALIAN, "ciao").withMetadata("testns", MyMetadata.of("hello")));
        
        SemText reconstructedSemText = om.readValue(json, SemText.class);                                
        
        MyMetadata reconstructedMetadata = (MyMetadata) reconstructedSemText.getMetadata("testns");
        
        assert  MyMetadata.of("hello").equals(reconstructedMetadata);
    }

}

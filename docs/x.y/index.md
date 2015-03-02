<p class="jadoc-to-strip">
WARNING: WORK IN PROGRESS - THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE PROJECT WEBSITE
</p>


#### Maven dependency

TODO Update 

SemText Jackson is available on Maven Central. To use it, put this in the dependencies section of your _pom.xml_: 


```
<dependency>
  <groupId>eu.trentorise.opendata.semtext</groupId>
  <artifactId>semtext-jackson</artifactId>
  <version>#{version}</version>            
</dependency>
```

In case updates are available, version numbers follows [semantic versioning](http://semver.org/) rules.


#### Using Jackson Module

You can register SemTextModule in your own Jackson ObjectMapper:
```
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());
        om.registerModule(new OdtCommonsModule());
        om.registerModule(new SemTextModule());

        String json = om.writeValueAsString(SemText.of("ciao", Locale.ITALIAN));
        SemText reconstructedSemText = om.readValue(json, SemText.class);
```

Notice we are also registered the necessary Guava (for immutable collections) and Odt Commons modules (for Dict and LocalizedString). To register everything in one command just write:

```
        ObjectMapper om = new ObjectMapper();
        SemTextModule.registerModulesInto(om);        
  
```

#### Simple usage example

```
        ObjectMapper om = new ObjectMapper();
        SemTextModule.registerModulesInto(om);
        
        String json = om.writeValueAsString(SemText.of(Locale.ITALIAN, "ciao"));
        SemText reconstructedSemText = om.readValue(json, SemText.class);
```

#### Metadata deserialization

Any object can be attached as metadata to SemText, Sentence, Term or Meaning, with the constraint that it must be non-null and should be immutable. Metadata is accessed by providing a namespace. For example, let's say we want to associate a Java Date to SemText, under the namespace "testns". 

To create our object in Java we can write this:
```
SemText.of("ciao").withMetadata("testns", new Date(123))
```

Once serialized as JSON, it will look like this:

```
{
  "locale":"",
  "text":"ciao",
  "sentences":[],
  "metadata":{
                "testns":123
             }
}
```

In order for metadata objects to be properly deserialized, we need to associate namespaces to object types by registering them in the SemTextModule. So, in order to serialize/deserialize the example above, we would do something like the following:

```
ObjectMapper om = new ObjectMapper();

// register all required modules into the Jackson Object Mapper
SemTextModule.registerModulesInto(om);

// declare that metadata under namespace 'testns' in SemText objects
// should be deserialized into a Date object
SemTextModule.registerMetadata(SemText.class, "testns", Date.class);       
                        
String json = om.writeValueAsString(SemText.of("ciao").withMetadata("testns", new Date(123)));
        
SemText reconstructedSemText = om.readValue(json, SemText.class);                                
        
Date reconstructedMetadata = (Date) reconstructedSemText.getMetadata("testns");
        
assert  new Date(123).equals(reconstructedMetadata);
```

NOTE: namespace register is a static variable, so it's shared among all the object mappers. If you're writing a library with SemText serializer make sure to use a reasonably unique (thus long) namespace. Applications instead may use shorter namespaces.

##### Custom metadata deserialization

A more complex example can be found in <a href="https://github.com/opendatatrentino/semtext-jackson/blob/master/src/test/java/eu/trentorise/opendata/semtext/jackson/test/SemTextModuleTest.java" target="_blank">SemTextModuleTest.metadataSerializationComplex</a>, which shows how to develop and register a custom immutable metadata object. 

package com.googlecode.jsonplugin;

import junit.framework.TestCase;

import java.util.Map;

/**
 * Test serialization of an Enum in the two supported modes:
 * - Enum as a name=value pair;
 * - Enum as a bean
 */
public class JSONEnumTest extends TestCase {

    /**
     * Asserts that a bean can be serialized to JSON and restored as a map
     */
    public void testEnumAsNameValue() throws Exception {
        Bean bean1 = new Bean();

        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(false);
        String json = jsonWriter.write(bean1);

        Map result = (Map) JSONUtil.deserialize(json);
        assertEquals("str", result.get("stringField"));
        assertEquals(true, result.get("booleanField"));
        assertEquals("s", result.get("charField"));      // note: this is a String
        assertEquals(10.1, result.get("doubleField"));
        assertEquals(1.5, result.get("floatField"));     // note: this is a Double
        assertEquals(10L, result.get("intField"));       // note: this is a Long
        assertEquals(AnEnum.ValueA, AnEnum.valueOf((String) result.get("enumField")));  // note: this is a String
        assertEquals(AnEnumBean.Two, AnEnumBean.valueOf((String) result.get("enumBean")));  // note: this is a String
    }

    /**
     * Asserts that a bean can be serialized to JSON and restored as a map
     * <p/>
     * In this case, the name of the enum is in _name and the two properties of AnEnumBean are also serialized
     */
    public void testEnumAsBean() throws Exception {
        Bean bean1 = new Bean();

        bean1.setStringField("str");
        bean1.setBooleanField(true);
        bean1.setCharField('s');
        bean1.setDoubleField(10.1);
        bean1.setFloatField(1.5f);
        bean1.setIntField(10);
        bean1.setLongField(100);
        bean1.setEnumField(AnEnum.ValueA);
        bean1.setEnumBean(AnEnumBean.Two);

        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.setEnumAsBean(true);
        String json = jsonWriter.write(bean1);

        Map result = (Map) JSONUtil.deserialize(json);
        assertEquals("str", result.get("stringField"));
        assertEquals(true, result.get("booleanField"));
        assertEquals("s", result.get("charField"));      // note: this is a String
        assertEquals(10.1, result.get("doubleField"));
        assertEquals(1.5, result.get("floatField"));     // note: this is a Double
        assertEquals(10L, result.get("intField"));       // note: this is a Long
        Map enumBean1 = (Map) result.get("enumField");
        assertNotNull(enumBean1);
        assertEquals(AnEnum.ValueA, AnEnum.valueOf((String) enumBean1.get("_name")));  // get the special name property
        Map enumBean2 = (Map) result.get("enumBean");
        assertEquals(AnEnumBean.Two, AnEnumBean.valueOf((String) enumBean2.get("_name")));  // get the special name property
        assertEquals(AnEnumBean.Two.getPropA(), (String) enumBean2.get("propA"));  // get the propA property
        assertEquals(AnEnumBean.Two.getPropB(), (String) enumBean2.get("propB"));  // get the propA property
    }
}

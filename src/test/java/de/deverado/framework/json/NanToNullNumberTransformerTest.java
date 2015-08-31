package de.deverado.framework.json;

import static junit.framework.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

public class NanToNullNumberTransformerTest {

    private ObjectMapper m;

    @Before
    public void setUp() throws Exception {
        NanToNullNumberTransformer transformer = new NanToNullNumberTransformer();
        m = new ObjectMapper();
        m.addMixInAnnotations(transformer.getMixinTarget(),
                transformer.getMixinAnnotation());
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Need to test here to catch changes in the internals of the transformers.
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonGenerationException
     */
    @Test
    public void shouldMakeInfNullButONLYCLASSTYPE()
            throws JsonGenerationException, JsonMappingException, IOException {
        TestObject infObject = new TestObject();
        infObject.setD(Double.NEGATIVE_INFINITY);
        String string = m.writeValueAsString(infObject);
        assertTrue(string, string.indexOf("dobj\":null") > 0);
    }

    @Test
    public void FAILStoMakeInfNullForPrimitives()
            throws JsonGenerationException, JsonMappingException, IOException {
        TestObject infObject = new TestObject();
        infObject.setD(Double.NEGATIVE_INFINITY);
        String string = m.writeValueAsString(infObject);
        assertTrue(string, string.indexOf("d\":\"-Infinity") > 0);
    }

    @Test
    public void shouldStillSerializeNumbers() throws JsonGenerationException,
            JsonMappingException, IOException {
        CleanObject cleanObject = new CleanObject();
        String string = m.writeValueAsString(cleanObject);
        assertTrue(string, string.indexOf("d\":2.0") > 0);
        assertTrue(string, string.indexOf("bi\":12") > 0);
    }

    static class TestObject {
        private double d;
        private String other = "other";

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public Double getDobj() {
            return d;
        }

        public void setDobj(Double d) {
            this.d = d;
        }

        public String getOther() {
            return other;
        }
    }

    static class CleanObject {
        private double d = 2.0;
        private BigInteger bi = new BigInteger("12");
        private String other = "other";

        public double getD() {
            return d;
        }

        public BigInteger getBi() {
            return bi;
        }

        public String getOther() {
            return other;
        }
    }
}

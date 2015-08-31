package de.deverado.framework.json;

import com.google.inject.Guice;
import junit.framework.TestCase;
import org.junit.Test;

public class JacksonModuleTest extends TestCase {

    @Test
    public void testModule() {
        Guice.createInjector(new JacksonModule());
    }
}
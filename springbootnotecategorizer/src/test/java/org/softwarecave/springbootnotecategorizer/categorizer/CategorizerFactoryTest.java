package org.softwarecave.springbootnotecategorizer.categorizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CategorizerFactoryTest {

    @Test
    public void testGetKeywordBasedCategorizer() {
        CategorizerFactory factory = new CategorizerFactory();
        Categorizer categorizer = factory.getKeywordBasedCategorizer();
        assertNotNull(categorizer);
    }

}

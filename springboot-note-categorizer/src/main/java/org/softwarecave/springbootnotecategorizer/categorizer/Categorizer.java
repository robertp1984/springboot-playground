package org.softwarecave.springbootnotecategorizer.categorizer;

public interface Categorizer {
    CategorizerResults categorize(String title, String body);
}

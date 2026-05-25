package org.softwarecave.springbootnotecategorizer.categorizer;

import org.softwarecave.springbootnotecategorizer.Category;

public record CategorizerResult(Category category, double score) {
}

package org.softwarecave.springbootnotecategorizer.categorizer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.softwarecave.springbootnotecategorizer.Category;

@Data
@AllArgsConstructor
public class CategorizerResult {
    private final Category category;
    private final double score;
}

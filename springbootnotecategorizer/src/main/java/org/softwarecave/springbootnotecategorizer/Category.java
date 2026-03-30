package org.softwarecave.springbootnotecategorizer;

public enum Category {
    JAVA("Java", "Java programming language"),
    SPRING("Spring", "Spring Framework and its ecosystem"),
    JPA("JPA", "Java Persistence API and ORM frameworks"),
    KAFKA("Kafka", "Apache Kafka and its ecosystem"),
    GIT("Git", "Git version control system"),
    DOCKER("Docker", "Docker containerization"),
    CLOUD("Cloud", "cloud computing platforms");

    private final String name;
    private final String description;

    private Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Category getCategoryByName(String name) {
        for (Category category : values()) {
            if (category.name.equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }
}

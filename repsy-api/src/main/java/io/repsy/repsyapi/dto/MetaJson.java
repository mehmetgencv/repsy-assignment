package io.repsy.repsyapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MetaJson {

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("author")
    private String author;

    @JsonProperty("dependencies")
    private List<Dependency> dependencies;

    public static class Dependency {
        @JsonProperty("package")
        private String packageName;

        @JsonProperty("version")
        private String version;

        // Getters and Setters
        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
}
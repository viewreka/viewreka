package org.beryx.viewreka.project;

/**
 * Interface for reading Viewreka projects.
 * @param <P> the type of Viewreka project provided by this reader
 */
public interface ProjectReader<P extends Project<?,?>> {
    /**
     * Retrieves a Viewreka project from the specified URI.
     * @param projectUri the URI used to identify the project
     * @return the Viewreka project
     */
    P getProject(String projectUri);
}

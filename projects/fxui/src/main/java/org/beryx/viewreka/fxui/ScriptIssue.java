package org.beryx.viewreka.fxui;

/**
 * An issue detected while interpreting the project script
 */
public interface ScriptIssue {
    /**
     * @return a text describing this issue
     */
    String getMessage();

    /**
     * @return true, if this issue prevents the construction of a valid Viewreka project
     */
    boolean isFatal();

    /**
     * @return a (possibly null) {@link Throwable}, which is the cause of this issue
     */
    Throwable getError();

    /**
     * @return a (possibly null) string used to locate the source of the project script
     */
    String getSourceLocator();

    /**
     * @return the line on which the issue occurred
     */
    int getLine();

    /**
     * @return the column on which the issue occurred
     */
    int getColumn();
}

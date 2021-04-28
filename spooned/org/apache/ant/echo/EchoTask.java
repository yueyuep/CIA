package org.apache.ant.echo;
/**
 * A very simple task that takes a bit of text and echos it back out
 * when it is executed. This is useful for troubleshooting properties
 * in buildfiles, letting the user know that something is going to happen
 * and as a very simple example that can be copied to create other tasks.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class EchoTask extends AbstractTask {
    // -----------------------------------------------------------------
    // PRIVATE DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     * Data to echo
     */
    private java.lang.String text;

    // -----------------------------------------------------------------
    // PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     * Executes this task.
     */
    public boolean execute() throws org.apache.ant.echo.AntException {
        project.getFrontEnd().writeMessage(text);
        return true;
    }

    /**
     * Sets the text that this task will echo.
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }
}
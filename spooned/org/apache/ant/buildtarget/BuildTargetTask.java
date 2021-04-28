package org.apache.ant.buildtarget;
/**
 * A simple task that builds a target if a property is set to true
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class BuildTargetTask extends AbstractTask {
    // -----------------------------------------------------------------
    // PRIVATE DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     * Data to echo
     */
    private java.lang.String ifProperty;

    /**
     * Target to execute
     */
    private java.lang.String targetName;

    // -----------------------------------------------------------------
    // PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     * Executes this task.
     */
    public boolean execute() throws org.apache.ant.buildtarget.AntException {
        // XXX should really check internal state before proceeding! Target
        // has to be set...
        // XXX oh, and we should really check to see if the target exists
        // and fail out if it doesn't. :)
        if (ifProperty != null) {
            java.lang.String ifPropertyValue = project.getProperty(ifProperty);
            if (ifPropertyValue.equals("true")) {
                project.startBuild(targetName);
                return true;
            } else {
                return true;
            }
        } else {
            project.startBuild(targetName);
            return true;
        }
    }

    /**
     * Sets the property that will be examined
     */
    public void setIf(java.lang.String ifProperty) {
        this.ifProperty = ifProperty;
    }

    /**
     * Sets the target to be executed
     */
    public void setTarget(java.lang.String targetName) {
        this.targetName = targetName;
    }
}
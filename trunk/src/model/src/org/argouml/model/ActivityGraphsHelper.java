// $Id$
// Copyright (c) 2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.model;

/**
 * The interface for the helper for ActivityGraphs.<p>
 *
 * Created from the old ActivityGraphsHelper.
 */
public interface ActivityGraphsHelper {
    /**
     * Finds the Classifier to which a given ObjectFlowState
     * refers by its given name. This function may be used for when the user
     * types the name of a classifier in the diagram, in an ObjectFlowState.
     *
     * @author MVW
     * @param ofs the given ObjectFlowState
     * @param s   the given String that represents
     *            the name of the "type" Classifier
     * @return    the found classifier or null
     */
    Object findClassifierByName(Object ofs, String s);

    /**
     * Find a state of a Classifier by its name.
     * This routine is used to make the connection between
     * a ClassifierInState and its State.
     *
     * @author mvw
     * @param c the Classifier. If this is not a Classifier, then
     *          IllegalArgumentException is thrown.
     * @param s the string that represents the name of
     *          the state we are looking for. If "" or null, then
     *          null is returned straight away.
     * @return  the State (as Object) or null, if not found.
     */
    Object findStateByName(Object c, String s);

    /**
     * Returns true if an activitygraph may be added to the given
     * context. To decouple ArgoUML as much as possible from the
     * model implementation, the parameter of the method is of
     * type Object.<p>
     *
     * An ActivityGraph specifies the dynamics of<ol>
     * <li> a Package, or
     * <li> a Classifier (including UseCase), or
     * <li> a BehavioralFeature.
     * </ol>
     *
     * @param context the given context
     * @return boolean true if an activitygraph may be added
     */
    boolean isAddingActivityGraphAllowed(Object context);

    /**
     * @author mvw
     * @param classifierInState the classifierInState
     * @param state the state that will be linked
     */
    void addInState(Object classifierInState, Object state);
}

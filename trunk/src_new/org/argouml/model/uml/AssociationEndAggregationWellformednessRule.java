// $Id$
// Copyright (c) 2003 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
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

package org.argouml.model.uml;

import java.util.Collection;
import java.util.Iterator;

import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.data_types.MAggregationKind;

/**
 * Checks that there is at most one associationend within an association an 
 * aggregation or composite.
 *
 * @author jaap.branderhorst@xs4all.nl
 */
public class AssociationEndAggregationWellformednessRule
	extends AbstractWellformednessRule {

    /**
     * Constructor for AssociationEndAggregationWellformednessRule.
     * @param key the message key to be looked up 
     *            to show the message to the user
     */
    public AssociationEndAggregationWellformednessRule(String key) {
	super(key);
    }

    /**
     * Constructor for AssociationEndAggregationWellformednessRule.
     */
    public AssociationEndAggregationWellformednessRule() {
	setUserMessageKey("associationend.aggregation");
    }

    /**
     * Checks that there is at most one associationend within an association an 
     * aggregation or composite.
     * @see org.argouml.model.uml.AbstractWellformednessRule#isWellformed(Object,Object)
     */
    public boolean isWellformed(Object/*MBase*/ element, Object newValue) {
	if (element instanceof MAssociationEnd 
            && newValue instanceof MAggregationKind) {
	    MAssociationEnd modelelement = (MAssociationEnd) element;
	    MAggregationKind aggregation = (MAggregationKind) newValue;
	    if (aggregation.equals(MAggregationKind.NONE)) return true;
	    Collection ends = modelelement.getAssociation().getConnections();
	    if (ends.size() > 2) return false;
	    Iterator it = ends.iterator();
	    int counter = 0;
	    while (it.hasNext()) {
		MAssociationEnd end = (MAssociationEnd) it.next();
		if (!modelelement.equals(end)
		    && !(end.getAggregation().equals(MAggregationKind.NONE)))
		{
		    return false;
		}
	    }
	} else {
	    return false;
	}
	return true;
    }

}

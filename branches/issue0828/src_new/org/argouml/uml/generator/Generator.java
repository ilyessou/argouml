// Copyright (c) 1996-2001 The Regents of the University of California. All
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

// File: Generator.java
// Classes: Generator
// Original Author:
// $Id$

// 10 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// extension points.

// 25 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Patched
// generateStereotype to handle the case where the stereotype has a null name
// (caused NPE when making a new stereotype).

package org.argouml.uml.generator;
import org.argouml.application.api.*;
import org.argouml.language.helpers.*;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;
import ru.novosoft.uml.foundation.data_types.MMultiplicity;
import ru.novosoft.uml.foundation.data_types.MExpression;
import ru.novosoft.uml.behavior.common_behavior.*;
import ru.novosoft.uml.behavior.use_cases.*;
import ru.novosoft.uml.behavior.collaborations.*;
import ru.novosoft.uml.behavior.state_machines.*;
import ru.novosoft.uml.model_management.*;
import java.util.*;

/** This class is the abstract super class that defines a code
 * generation framework.  It is basically a depth-first traversal of
 * the UML model that generates strings as it goes.  This framework
 * should probably be redesigned to separate the traversal logic from
 * the generation logic.  See the <a href=
 * "http://hillside.net/patterns/patterns.html">Vistor design
 * pattern</a> in "Design Patterns", and the <a href=
 * "http://www.ccs.neu.edu/research/demeter/"> Demeter project</a>. */

public abstract class Generator
implements NotationProvider {

  private NotationName _notationName = null;

  /** Two spaces used for indenting code in classes. */
  public static String INDENT = "  ";

  public final static String fileSep=System.getProperty("file.separator");

    private static Map s_generators = new HashMap();
    public static Generator getGenerator(NotationName n) {
	return (Generator)s_generators.get(n);
    }

  public Generator(NotationName notationName) {
      _notationName = notationName;
      s_generators.put(_notationName, this);
  }

  public NotationName getNotation() {
        return _notationName;
  }

  public String generate(Object o) {
    if (o == null)
      return "";
    if (o instanceof MExtensionPoint)
        return generateExtensionPoint((MExtensionPoint) o);
    if (o instanceof MOperation)
      return generateOperation((MOperation) o, false);
    if (o instanceof MAttribute)
      return generateAttribute((MAttribute) o, false);
    if (o instanceof MParameter)
      return generateParameter((MParameter) o);
    if (o instanceof MPackage)
      return generatePackage((MPackage) o);
    if (o instanceof MClassifier)
      return generateClassifier((MClassifier) o);
    if (o instanceof MExpression)
      return generateExpression((MExpression) o);
    if (o instanceof String)
      return generateName((String) o);
    if (o instanceof String)
      return generateUninterpreted((String) o);
    if (o instanceof MStereotype)
      return generateStereotype((MStereotype) o);
    if (o instanceof MTaggedValue)
      return generateTaggedValue((MTaggedValue) o);
    if (o instanceof MAssociation)
      return generateAssociation((MAssociation)o);
    if (o instanceof MAssociationEnd)
      return generateAssociationEnd((MAssociationEnd)o);
    if (o instanceof MMultiplicity)
      return generateMultiplicity((MMultiplicity)o);
    if (o instanceof MState)
      return generateState((MState)o);
    if (o instanceof MTransition)
      return generateTransition((MTransition)o);
    if (o instanceof MAction)
      return generateAction((MAction)o);
    if (o instanceof MCallAction)
      return generateAction((MAction)o);
    if (o instanceof MGuard)
      return generateGuard((MGuard)o);
    if (o instanceof MMessage)
      return generateMessage((MMessage)o);

    if (o instanceof MModelElement)
      return generateName(((MModelElement)o).getName());

    if (o == null) return "";

    return o.toString();
  }

  public abstract String generateExtensionPoint(MExtensionPoint op);
  public abstract String generateOperation(MOperation op, boolean documented);
  public abstract String generateAttribute(MAttribute attr, boolean documented);
  public abstract String generateParameter(MParameter param);
  public abstract String generatePackage(MPackage p);
  public abstract String generateClassifier(MClassifier cls);
  public abstract String generateTaggedValue(MTaggedValue s);
  public abstract String generateAssociation(MAssociation a);
  public abstract String generateAssociationEnd(MAssociationEnd ae);
  public abstract String generateMultiplicity(MMultiplicity m);
  public abstract String generateState(MState m);
  public abstract String generateTransition(MTransition m);
  public abstract String generateAction(MAction m);
  public abstract String generateGuard(MGuard m);
  public abstract String generateMessage(MMessage m);

  public String generateExpression(MExpression expr) {
    if (expr == null) return "";
    return generateUninterpreted(expr.getBody());
  }

  public String generateExpression(MConstraint expr) {
    if (expr == null) return "";
    return generateExpression(expr.getBody());
  }

  public String generateName(String n) {
    return n;
  }

  public String generateUninterpreted(String un) {
    if (un == null) return "";
    return un;
  }

  public String generateClassifierRef(MClassifier cls) {
    if (cls == null) return "";
    return cls.getName();
  }

  public String generateStereotype(MStereotype st) {
    if (st == null) return "";
    if (st.getName() == null) return "";  // Patch by Jeremy Bennett
    if (st.getName().length() == 0) return "";
    return NotationHelper.getLeftGuillemot() +
           generateName(st.getName()) +
	   NotationHelper.getRightGuillemot();
  }

    // Module stuff
  public boolean isModuleEnabled() { return true; }
  public Vector getModulePopUpActions(Vector v, Object o) { return null; }
  public boolean shutdownModule() { return true; }
  public boolean initializeModule() { return true; }
  public void setModuleEnabled(boolean enabled) { }
  public boolean inContext(Object[] o) { return false; }

} /* end class Generator */

// $Id$
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

// File: GeneratorJava.java
// Classes: GeneratorJava
// Original Author:
// $Id$

// 12 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// extension points.

package org.argouml.language.java.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.argouml.application.ArgoVersion;
import org.argouml.application.api.Argo;
import org.argouml.application.api.Notation;
import org.argouml.model.ModelFacade;
import org.argouml.model.uml.UmlHelper;
import org.argouml.uml.DocumentationManager;
import org.argouml.uml.generator.FileGenerator;
import org.argouml.uml.generator.Generator;
import ru.novosoft.uml.behavior.collaborations.MAssociationRole;
import ru.novosoft.uml.behavior.collaborations.MMessage;
import ru.novosoft.uml.behavior.state_machines.MGuard;
import ru.novosoft.uml.behavior.state_machines.MState;

import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.behavior.use_cases.MExtensionPoint;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.core.MAttribute;
import ru.novosoft.uml.foundation.core.MClass;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MConstraint;
import ru.novosoft.uml.foundation.core.MFeature;
import ru.novosoft.uml.foundation.core.MMethod;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.core.MParameter;
import ru.novosoft.uml.foundation.core.MStructuralFeature;
import ru.novosoft.uml.foundation.data_types.MCallConcurrencyKind;
import ru.novosoft.uml.foundation.data_types.MChangeableKind;
import ru.novosoft.uml.foundation.data_types.MExpression;
import ru.novosoft.uml.foundation.data_types.MMultiplicity;
import ru.novosoft.uml.foundation.data_types.MMultiplicityRange;
import ru.novosoft.uml.foundation.data_types.MScopeKind;
import ru.novosoft.uml.foundation.data_types.MVisibilityKind;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;
import ru.novosoft.uml.model_management.MPackage;
import tudresden.ocl.OclTree;

import tudresden.ocl.parser.node.AConstraintBody;

import org.argouml.application.api.Argo;
import org.argouml.application.api.Configuration;

/** Generator subclass to generate text for display in diagrams in in
 * text fields in the Argo/UML user interface.  The generated code
 * looks a lot like (invalid) Java.  The idea is that other generators
 * could be written for outher languages.  This code is just a
 * placeholder for future development, I expect it to be totally
 * replaced. */

// TODO: always check for null!!!

public class GeneratorJava
    extends Generator implements FileGenerator {

    /** logger */
    private static final Logger cat = Logger.getLogger(GeneratorJava.class);
    
    /*
     * 2002-06-09 changed visibility of VERBOSE_DOCS and
     * LF_BEFORE_CURLY to public instead of private
     * Reason: needed for testing
     *
     * 2002-06-11 removed VERBOSE_DOCS and LF_BEFORE_CURLY and changed
     * them in configurable items (not yet implemented in GUI)
     */

    protected boolean _verboseDocs = false;
    protected boolean _lfBeforeCurly = false;
    private static final boolean VERBOSE_DOCS = false;
    private static final String LINE_SEPARATOR =
	System.getProperty("line.separator");
    // TODO: make it configurable
    // next two flags shows in what mode we are working
    /** true when GenerateFile
     */
    private static boolean _isFileGeneration = false;
    /** true if GenerateFile in Update Mode
     */
    private static boolean _isInUpdateMode = false; 

    private static GeneratorJava SINGLETON = new GeneratorJava();

    public static GeneratorJava getInstance() {
        return SINGLETON;
    }

    protected GeneratorJava() {
        super(
	      Notation.makeNotation(
				    "Java",
				    null,
				    Argo.lookupIconResource("JavaNotation")));
    }

    public static String Generate(Object o) {
        return SINGLETON.generate(o);
    }

    /** Generates a file for the classifier.
     * This method could have been static if it where not for the need to
     * call it through the Generatorinterface.
     * @return the full path name of the the generated file or
     * 	       null if no file can be generated.
     */
    public String GenerateFile(Object modelElement, String path) {
        String name = ModelFacade.getName(modelElement);
        if (name == null || name.length() == 0)
            return null;
        Object classifier = /*(MClassifier)*/ modelElement;
        String filename = name + ".java";
        if (!path.endsWith(FILE_SEPARATOR))
            path += FILE_SEPARATOR;

        String packagePath = getPackageName(ModelFacade.getNamespace(classifier));

        int lastIndex = -1;
        do {
            File f = new File(path);
            if (!f.isDirectory()) {
                if (!f.mkdir()) {
                    cat.error(" could not make directory " + path);
                    return null;
                }
            }

            if (lastIndex == packagePath.length())
                break;

            int index = packagePath.indexOf(".", lastIndex + 1);
            if (index == -1)
                index = packagePath.length();

            path += packagePath.substring(lastIndex + 1, index)
                + FILE_SEPARATOR;
            lastIndex = index;
        } while (true);

        String pathname = path + filename;
        //cat.info("-----" + pathname + "-----");

        //now decide wether file exist and need an update or is to be
        //newly generated
        File f = new File(pathname);
        _isFileGeneration = true; // used to produce method javadoc
        if (f.exists()) {
            try {
                update((MClassifier)classifier, f);
            } catch (Exception exp) {
                _isInUpdateMode = false;
                _isFileGeneration = false;
                cat.error("FAILED: " + f.getPath());
            }

            //cat.info("----- end generating -----");
            _isFileGeneration = false;
            return pathname;
        }

        //String pathname = path + filename;
        // TODO: package, project basepath, tagged values to configure
        cat.info("Generating (new) " + f.getPath());
        _isFileGeneration = true;
        String header = SINGLETON.generateHeader((MClassifier)classifier, pathname, packagePath);
        String src = SINGLETON.generate(classifier);
        BufferedWriter fos = null;
        try {
        	if ( Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING) == null 
		|| Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING).trim().equals(""))
            	fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), System.getProperty("file.encoding")));
		else
            	fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING)));
            fos.write(header);
            fos.write(src);
        } catch (IOException exp) {
            cat.error("IO Exception: "+exp+", for file: "+f.getPath());
        } finally {
            _isFileGeneration = false;
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException exp) {
                cat.error("FAILED: " + f.getPath());
            }
        }

        //cat.info("----- end updating -----");
        return pathname;
    }

    public String generateHeader(MClassifier cls,
				 String pathname,
				 String packagePath) {
        StringBuffer sb = new StringBuffer(80);
        //TODO: add user-defined copyright
        if (VERBOSE_DOCS) {
            sb.append("// FILE: ").append(pathname.replace('\\', '/'));
	    sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
	}
        if (packagePath.length() > 0) {
            sb.append("package ").append(packagePath).append(";");
	    sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
	}
        sb.append(generateImports(cls, packagePath));
        return sb.toString();
    }

    public String generateImports(MClassifier cls, String packagePath) {
        // TODO: check also generalizations
        StringBuffer sb = new StringBuffer(80);
        java.util.HashSet importSet = new java.util.HashSet();
        String ftype;
        Iterator j;
        Collection c = ModelFacade.getFeatures(cls);
        if (c != null) {
            // now check packages of all feature types
            for (j = c.iterator(); j.hasNext();) {
                Object mFeature = /*(MFeature)*/ j.next();
                if (ModelFacade.isAAttribute(mFeature)) {
                    if ((ftype =
                            generateImportType(ModelFacade.getType(mFeature),
                                               packagePath))
                            != null) {
                        importSet.add(ftype);
                    }
                } else if (ModelFacade.isAOperation(mFeature)) {
                    // check the parameter types
                    Iterator it = ModelFacade.getParameters(mFeature).iterator();
                    while (it.hasNext()) {
                        Object parameter = it.next();
                        if ((ftype =
                                generateImportType(ModelFacade.getType(parameter), packagePath))
                                != null) {
                            importSet.add(ftype);
                        }
                    }
                    // check the return parameter types
                    it =
                        UmlHelper
			.getHelper()
			.getCore()
			.getReturnParameters(/*(MOperation)*/mFeature)
			.iterator();
                    while (it.hasNext()) {
                        Object parameter = it.next();
                        if ((ftype =
                                 generateImportType(ModelFacade.getType(parameter), packagePath))
                                != null) {
                            importSet.add(ftype);
                        }
                    }
                }
            }
        }
        c = ModelFacade.getAssociationEnds(cls);
        if (!c.isEmpty()) {
            // check association end types
            for (j = c.iterator(); j.hasNext();) {
                Object associationEnd = /*(MAssociationEnd)*/ j.next();
                Object association = ModelFacade.getAssociation(associationEnd);
                Iterator connEnum = ModelFacade.getConnections(association).iterator();
                while (connEnum.hasNext()) {
                    Object associationEnd2 = /*(MAssociationEnd)*/ connEnum.next();
                    if (associationEnd2 != associationEnd
                            && ModelFacade.isNavigable(associationEnd2)
                            && !ModelFacade.isAbstract(ModelFacade.getAssociation(associationEnd2))) {
                        // association end found
                        Object multiplicity = ModelFacade.getMultiplicity(associationEnd2);
                        if (!MMultiplicity.M1_1.equals(multiplicity)
                                && !MMultiplicity.M0_1.equals(multiplicity)) {
                            importSet.add("java.util.Vector");
                        } else if (
				   (ftype =
				    generateImportType(ModelFacade.getType(associationEnd2),
						       packagePath))
				   != null) {
                            importSet.add(ftype);
                        }
                    }
                }
            }
        }
        // finally generate the import statements
        for (j = importSet.iterator(); j.hasNext();) {
            ftype = (String) j.next();
            sb.append("import ").append(ftype).append(";");
	    sb.append(LINE_SEPARATOR);
        }
        if (!importSet.isEmpty()) {
            sb.append(LINE_SEPARATOR);
        }
        return sb.toString();
    }

    public String generateImportType(Object type, String exclude) {
        String ret = null;
        if (type != null && ModelFacade.getNamespace(type) != null) {
            String p = getPackageName(ModelFacade.getNamespace(type));
            if (p.length() > 0 && !p.equals(exclude))
                ret = p + '.' + ModelFacade.getName(type);
        }
        return ret;
    }

    /**
     * <p>Generate code for an extension point.</p>
     *
     * <p>Provided to comply with the interface, but returns null
     *   since no code will be generated. This should prevent a source tab
     *   being shown.</p>
     *
     * @param ep  The extension point to generate for
     *
     * @return    The generated code string. Always empty in this
     *            implementation.
     */

    public String generateExtensionPoint(MExtensionPoint ep) {

        return null;
    }

    public String generateAssociationRole(MAssociationRole m) {
        return "";
    }

    public String generateOperation(MOperation op, boolean documented) {
        if (_isFileGeneration)
            documented = true; // fix Issue 1506
        StringBuffer sb = new StringBuffer(80);
        String nameStr = null;
        boolean constructor = false;
        Object/*MStereotype*/ stereo = null;
        if (ModelFacade.getStereotypes(op).size() > 0) {
            stereo = ModelFacade.getStereotypes(op).iterator().next();
        }
        if (stereo != null
                && ModelFacade.getName(stereo).equals("create")) { // constructor
            nameStr = generateName(ModelFacade.getName(ModelFacade.getOwner(op)));
            constructor = true;
        } else {
            nameStr = generateName(ModelFacade.getName(op));
        }
        // Each pattern here must be similar to corresponding code piece
        // Operation code piece doesn't start with '\n'
        // so the next line is commented. See Issue 1505
        //sb.append(LINE_SEPARATOR); // begin with a blank line
        if (documented) {
            String s =
                generateConstraintEnrichedDocComment(op, documented, INDENT);
            if (s != null && s.trim().length() > 0)
		// should starts as the code piece
                sb.append(s).append(LINE_SEPARATOR);
        }

        // 2002-07-14
        // Jaap Branderhorst
        // missing concurrency generation
	//sb.append(INDENT); fixed issue 1505
        sb.append(generateConcurrency(op));
        sb.append(generateAbstractness(op));
        sb.append(generateChangeability(op));
        sb.append(generateScope(op));
        sb.append(generateVisibility(op));

        // pick out return type
        Object/*MParameter*/ rp = UmlHelper.getHelper().getCore().getReturnParameter(op);
        if (rp != null) {
            Object/*MClassifier*/ returnType = ModelFacade.getType(rp);
            if (returnType == null && !constructor) {
                sb.append("void ");
            } else if (returnType != null) {
                sb.append(generateClassifierRef(returnType)).append(' ');
            }
        }

        // name and params
        Vector params = new Vector(op.getParameters());
        params.remove(rp);

        sb.append(nameStr).append('(');

        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(generateParameter((MParameter)params.elementAt(i)));
//                MParameter p = (MParameter) params.elementAt(i);
//                sb.append(generateParameter(p));
            }
        }

        sb.append(')');

        return sb.toString();

    }

    public String generateAttribute(MAttribute attr, boolean documented) {
        if (_isFileGeneration)
            documented = true; // always "documented" if we generate file.
        StringBuffer sb = new StringBuffer(80);
        if (documented) {
            String s =
                generateConstraintEnrichedDocComment(attr, documented, INDENT);
            if (s != null && s.trim().length() > 0)
                sb.append(s).append(LINE_SEPARATOR);
        }
        //sb.append(INDENT); fixed issue 1505
        sb.append(generateCoreAttribute(attr));
        sb.append(";").append(LINE_SEPARATOR);

        return sb.toString();
    }

    public String generateCoreAttribute(MAttribute attr) {
        StringBuffer sb = new StringBuffer(80);
        sb.append(generateVisibility(attr));
        sb.append(generateScope(attr));
        sb.append(generateChangability(attr));
        /*
	 * 2002-07-14 Jaap Branderhorst Generating the multiplicity
	 * should not lead to putting the range in the generated code
	 * (no 0..1 as modifier) Therefore removed the multiplicity
	 * generation START OLD CODE

	 if (!MMultiplicity.M1_1.equals(attr.getMultiplicity()))
	 {
	 String m = generateMultiplicity(attr.getMultiplicity());
	 if (m != null && m.trim().length() > 0)
	 sb.append(m).append(' ');
	 }
	*/
        // END OLD CODE

        Object/*MClassifier*/ type = attr.getType();
        Object/*MMultiplicity*/ multi = attr.getMultiplicity();
        // handle multiplicity here since we need the type
        // actually the API of generator is buggy since to generate
        // multiplicity correctly we need the attribute too
        if (type != null && multi != null) {
            if (multi.equals(MMultiplicity.M1_1)) {
                sb.append(generateClassifierRef(type)).append(' ');
            } else if (org.argouml.model.ModelFacade.isADataType(type)) {
                sb.append(generateClassifierRef(type)).append("[] ");
            } else
                sb.append("java.util.Vector ");
        }

        sb.append(generateName(attr.getName()));
        Object/*MExpression*/ init = attr.getInitialValue();
        if (init != null) {
            String initStr = generateExpression((MExpression)init).trim();
            if (initStr.length() > 0)
                sb.append(" = ").append(initStr);
        }

        return sb.toString();
    }

    public String generateParameter(MParameter parameter) {
        StringBuffer sb = new StringBuffer(20);
        //TODO: qualifiers (e.g., const)
        //TODO: stereotypes...
        sb.append(generateClassifierRef(ModelFacade.getType(parameter))).append(' ');
        sb.append(generateName(ModelFacade.getName(parameter)));
        //TODO: initial value
        return sb.toString();
    }

    public String generatePackage(MPackage p) {
        StringBuffer sb = new StringBuffer(80);
        String packName = generateName(p.getName());
        sb.append("package ").append(packName).append(" {");
	sb.append(LINE_SEPARATOR);
        Collection ownedElements = p.getOwnedElements();
        if (ownedElements != null) {
            Iterator ownedEnum = ownedElements.iterator();
            while (ownedEnum.hasNext()) {
                Object modelElement = /*(MModelElement)*/ ownedEnum.next();
                sb.append(generate(modelElement));
                sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
            }
        } else {
            sb.append("(no elements)");
        }
        sb.append(LINE_SEPARATOR).append("})").append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Generate the start sequence for a classifier. The start sequence is
     * everything from the preceding javadoc comment to the opening curly brace.
     * Start sequences are non-empty for classes and interfaces only.
     *
     * This method is intented for package internal usage only.
     *
     * @param cls the classifier for which to generate the start sequence
     *
     * @return the generated start sequence
     */
    StringBuffer generateClassifierStart(MClassifier cls) {
        String sClassifierKeyword;
        if (org.argouml.model.ModelFacade.isAClass(cls))
            sClassifierKeyword = "class";
        else if (org.argouml.model.ModelFacade.isAInterface(cls))
            sClassifierKeyword = "interface";
        else
            return null; // actors, use cases etc.

        StringBuffer sb = new StringBuffer(80);

        // Add the comments for this classifier first.
        // Each pattern here must be similar to corresponding code piece
        // Classfier code piece doesn't start with LINE_SEPARATOR
        // so the next line is commented. See Issue 1505
        //sb.append (LINE_SEPARATOR);
        sb.append(DocumentationManager.getComments(cls));
	sb.append(generateConstraintEnrichedDocComment(cls, true, ""));

        // Now add visibility
        sb.append(generateVisibility(cls.getVisibility()));

        // Add other modifiers
        if (cls.isAbstract() && !(ModelFacade.isAInterface(cls))) {
            sb.append("abstract ");
        }

        if (cls.isLeaf()) {
            sb.append("final ");
        }

        // add additional modifiers
        if (cls.getTaggedValue("src_modifiers") != null)
            sb.append(" " + cls.getTaggedValue("src_modifiers") + " ");

        // add classifier keyword and classifier name
        sb.append(sClassifierKeyword).append(" ");
	sb.append(generateName(cls.getName()));

        // add base class/interface
        String baseClass = generateGeneralization(cls.getGeneralizations());
        if (!baseClass.equals("")) {
            sb.append(" ").append("extends ").append(baseClass);
        }

        // add implemented interfaces, if needed
        // nsuml: realizations!
        if (org.argouml.model.ModelFacade.isAClass(cls)) {
            String interfaces = generateSpecification((MClass) cls);
	    cat.debug("Specification: " + interfaces);
            if (!interfaces.equals("")) {
                sb.append(" ").append("implements ").append(interfaces);
            }
        }

        // add opening brace
        sb.append(_lfBeforeCurly ? (LINE_SEPARATOR + "{") : " {");

        // list tagged values for documentation
        String tv = generateTaggedValues(cls);
        if (tv != null && tv.length() > 0) {
            sb.append(LINE_SEPARATOR).append(INDENT).append(tv);
        }

        return sb;
    }

    protected StringBuffer generateClassifierEnd(MClassifier cls) {
        StringBuffer sb = new StringBuffer();
        if (org.argouml.model.ModelFacade.isAClass(cls) || org.argouml.model.ModelFacade.isAInterface(cls)) {
            if (_verboseDocs) {
                String classifierkeyword = null;
                if (org.argouml.model.ModelFacade.isAClass(cls)) {
                    classifierkeyword = "class";
                } else {
                    classifierkeyword = "interface";
                }
                sb.append(LINE_SEPARATOR);
		sb.append("//end of ").append(classifierkeyword);
		sb.append(" ").append(cls.getName()).append(LINE_SEPARATOR);
            }
            sb.append("}");
        }
        return sb;
    }
    /**
     * Append the classifier end sequence to the prefix text specified. The
     * classifier end sequence is the closing curly brace together with any
     * comments marking the end of the classifier.
     *
     * This method is intented for package internal usage.
     *
     * @param sbPrefix the prefix text to be amended. It is OK to call append on
     *                 this parameter.
     * @param cls      the classifier for which to generate the classifier end
     *                 sequence. Only classes and interfaces have a classifier
     *                 end sequence.
     * @param fPlain   if true, only the closing brace is generated. Otherwise,
     *                 this may also generate some comments.
     *
     * @return the complete classifier code, i.e., sbPrefix plus the classifier
     *         end sequence
     */
    StringBuffer appendClassifierEnd(
				     StringBuffer sbPrefix,
				     Object/*MClassifier*/ cls,
				     boolean fPlain) {
        // 2002-07-11
        // Jaap Branderhorst
        // Was:
        // START OLD CODE
        // if (fPlain)
        // {
        // 	return sbPrefix.append("}");
        // }
        // else
        // {
        //	String sClassifierKeyword;
        //	if (cls instanceof MClass)
        //		sClassifierKeyword = "class";
        //	else
        //		if (cls instanceof MInterface)
        //			sClassifierKeyword = "interface";
        //		else
        //			return null; // actors, use cases etc.

        //			sbPrefix.append("\n}");
        //	if (_verboseDocs)
        //	{
        //		sbPrefix
        //			.append(" /* end of ")
        //			.append(sClassifierKeyword)
        //			.append(" ")
        //			.append(generateName(cls.getName()))
        //			.append(" */");
        //	}
        //	sbPrefix.append('\n');
        // END OLD CODE
        // which caused problems due to the misuse of the boolean
        // fplain. (verbosedocs has same semantics) To prevent
        // backward compatibility problems i didnt remove the method
        // but changed to:
        sbPrefix.append(generateClassifierEnd((MClassifier)cls));

        return sbPrefix;

    }

    /**
     * Generates code for a classifier. In case of Java code is
     * generated for classes and interfaces only at the moment.
     * @see org.argouml.application.api.NotationProvider#generateClassifier(MClassifier)
     */
    public String generateClassifier(MClassifier cls) {
        /*
         * 2002-07-11 Jaap Branderhorst To prevent generation of not
         * requested whitespace etc. the method is reorganized.  First
         * the start of the classifier is generated.  Next the body
         * (method).  Then the end of the classifier.  The last step
         * is to concatenate everything.  Done this because if the
         * body was empty there were still linefeeds.

         * Start old code:
	 StringBuffer sb = generateClassifierStart(cls);
	 if (sb == null)
	 return ""; // not a class or interface

	 String tv = null; // helper for tagged values

	 // add attributes
	 Collection strs = MMUtil.SINGLETON.getAttributes(cls);
	 //
         // 2002-06-08
         // Jaap Branderhorst
         // Bugfix: strs is never null. Should check for isEmpty instead
         // old code:
         // if (strs != null)
         // new code:
         //
	 if (!strs.isEmpty())
	 {
	 sb.append('\n');
	 if (_verboseDocs && cls instanceof MClass)
	 {
	 sb.append(INDENT).append("// Attributes\n");
	 }

	 Iterator strEnum = strs.iterator();
	 while (strEnum.hasNext())
	 {
	 MStructuralFeature sf = (MStructuralFeature) strEnum.next();

	 sb.append(generate(sf));

	 tv = generateTaggedValues(sf);
	 if (tv != null && tv.length() > 0)
	 {
	 sb.append(INDENT).append(tv);
	 }
	 }
	 }

	 // add attributes implementing associations
	 Collection ends = cls.getAssociationEnds();
	 if (ends != null)
	 {
	 sb.append('\n');
	 if (_verboseDocs && cls instanceof MClass)
	 {
	 sb.append(INDENT).append("// Associations\n");
	 }

	 Iterator endEnum = ends.iterator();
	 while (endEnum.hasNext())
	 {
	 MAssociationEnd ae = (MAssociationEnd) endEnum.next();
	 MAssociation a = ae.getAssociation();

	 sb.append(generateAssociationFrom(a, ae));

	 tv = generateTaggedValues(a);
	 if (tv != null && tv.length() > 0)
	 {
	 sb.append(INDENT).append(tv);
	 }
	 }
	 }

	 // add operations
	 // TODO: constructors
	 Collection behs = MMUtil.SINGLETON.getOperations(cls);
	 //
         // 2002-06-08
         // Jaap Branderhorst
         // Bugfix: behs is never null. Should check for isEmpty instead
         // old code:
         // if (behs != null)
         // new code:
         //
	 if (!behs.isEmpty())
	 {
	 sb.append('\n');
	 if (_verboseDocs)
	 {
	 sb.append(INDENT).append("// Operations\n");
	 }
	 Iterator behEnum = behs.iterator();

	 while (behEnum.hasNext())
	 {
	 MBehavioralFeature bf = (MBehavioralFeature) behEnum.next();

	 sb.append(generate(bf));

	 tv = generateTaggedValues((MModelElement) bf);

	 if ((cls instanceof MClass)
	 && (bf instanceof MOperation)
	 && (!((MOperation) bf).isAbstract()))
	 {
	 if (_lfBeforeCurly)
	 sb.append('\n').append(INDENT);
	 else
	 sb.append(' ');
	 sb.append('{');

	 if (tv.length() > 0)
	 {
	 sb.append('\n').append(INDENT).append(tv);
	 }

	 // there is no ReturnType in behavioral feature (nsuml)
	 sb.append('\n').append(generateMethodBody((MOperation) bf)).append(
	 INDENT).append(
	 "}\n");
	 }
	 else
	 {
	 sb.append(";\n");
	 if (tv.length() > 0)
	 {
	 sb.append(INDENT).append(tv).append('\n');
	 }
	 }
	 }
	 }

	 sb = appendClassifierEnd(sb, cls, false);

	 return sb.toString();
         start new code: */
        StringBuffer returnValue = new StringBuffer();
        StringBuffer start = generateClassifierStart(cls);
        if ((start != null) && (start.length() > 0)) {
            StringBuffer body = generateClassifierBody(cls);
            StringBuffer end = generateClassifierEnd(cls);
            returnValue.append(start.toString());
            if ((body != null) && (body.length() > 0)) {
                returnValue.append(LINE_SEPARATOR);
                returnValue.append(body);
                if (_lfBeforeCurly) {
                    returnValue.append(LINE_SEPARATOR);
                }
            }
            returnValue.append((end != null) ? end.toString() : "");
        }
        return returnValue.toString();
    }

    /**
     * Generates the body of a class or interface.
     * @param cls
     * @return StringBuffer
     */
    protected StringBuffer generateClassifierBody(MClassifier cls) {
        StringBuffer sb = new StringBuffer();
        if (ModelFacade.isAClass(cls) || ModelFacade.isAInterface(cls)) {
            String tv = null; // helper for tagged values

            // add attributes
            Collection strs = ModelFacade.getStructuralFeatures(cls);

            //
            // 2002-06-08
            // Jaap Branderhorst
            // Bugfix: strs is never null. Should check for isEmpty instead
            // old code:
            // if (strs != null)
            // new code:
            //
            if (!strs.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                if (_verboseDocs && org.argouml.model.ModelFacade.isAClass(cls)) {
                    sb.append(INDENT).append("// Attributes");
		    sb.append(LINE_SEPARATOR);
                }

                Iterator strEnum = strs.iterator();
                while (strEnum.hasNext()) {
                    Object structuralFeature = /*(MStructuralFeature)*/ strEnum.next();

                    sb.append(generate(structuralFeature));

                    tv = generateTaggedValues((MModelElement)structuralFeature);
                    if (tv != null && tv.length() > 0) {
                        sb.append(INDENT).append(tv);
                    }
                }
            }

            // add attributes implementing associations
            Collection ends = cls.getAssociationEnds();
            // 2002-06-08
            // Jaap Branderhorst
            // Bugfix: ends is never null. Should check for isEmpty instead
            // old code:
            // if (ends != null)
            // new code:
            if (!ends.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                if (_verboseDocs && org.argouml.model.ModelFacade.isAClass(cls)) {
                    sb.append(INDENT).append("// Associations");
		    sb.append(LINE_SEPARATOR);
                }

                Iterator endEnum = ends.iterator();
                while (endEnum.hasNext()) {
                    Object associationEnd = /*(MAssociationEnd)*/ endEnum.next();
                    Object association = ModelFacade.getAssociation(associationEnd);

                    sb.append(generateAssociationFrom((MAssociation)association, (MAssociationEnd)associationEnd));

                    tv = generateTaggedValues((MAssociation)association);
                    if (tv != null && tv.length() > 0) {
                        sb.append(INDENT).append(tv);
                    }
                }
            }

            // Inner classes
            Collection elements = cls.getOwnedElements();
            for (Iterator i = elements.iterator(); i.hasNext(); ) {
                Object element = /*(MModelElement)*/ i.next();
                if (ModelFacade.isAClass(element) || ModelFacade.isAInterface(element)) {
                    sb.append(generateClassifier((MClassifier)element));
                }
            }

            // add operations
            // TODO: constructors
            Collection behs = ModelFacade.getOperations(cls);

            //
            // 2002-06-08
            // Jaap Branderhorst
            // Bugfix: behs is never null. Should check for isEmpty instead
            // old code:
            // if (behs != null)
            // new code:
            //
            if (!behs.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                if (_verboseDocs) {
                    sb.append(INDENT).append("// Operations");
		    sb.append(LINE_SEPARATOR);
                }
                Iterator behEnum = behs.iterator();

                while (behEnum.hasNext()) {
                    Object behavioralFeature = /*(MBehavioralFeature)*/ behEnum.next();

                    sb.append(generate(behavioralFeature));

                    tv = generateTaggedValues((MModelElement) behavioralFeature);

                    if ((ModelFacade.isAClass(cls))
                            && (ModelFacade.isAOperation(behavioralFeature))
                            && (!ModelFacade.isAbstract(behavioralFeature))) {
                        if (_lfBeforeCurly)
                            sb.append(LINE_SEPARATOR).append(INDENT);
                        else
                            sb.append(' ');
                        sb.append('{');

                        if (tv.length() > 0) {
                            sb.append(LINE_SEPARATOR).append(INDENT).append(tv);
                        }

                        // there is no ReturnType in behavioral feature (nsuml)
                        sb
                            .append(LINE_SEPARATOR)
                            .append(generateMethodBody((MOperation)behavioralFeature))
                            .append(INDENT)
                            .append("}").append(LINE_SEPARATOR);
                    } else {
                        sb.append(";").append(LINE_SEPARATOR);
                        if (tv.length() > 0) {
                            sb.append(INDENT).append(tv).append(LINE_SEPARATOR);
                        }
                    }
                }
            }
        }
        return sb;

    } /**
       * Generate the body of a method associated with the given
       * operation. This assumes there's at most one method
       * associated!
       *
       * If no method is associated with the operation, a default
       * method body will be generated.
       */
    public String generateMethodBody(MOperation op) {
        //cat.info("generateMethodBody");
        if (op != null) {
            Collection methods = op.getMethods();
            Iterator i = methods.iterator();
            MMethod m = null;

            while (i != null && i.hasNext()) {
                m = (MMethod) i.next();

                if (m != null) {
                    if (m.getBody() != null) {
                        String body = m.getBody().getBody();
                        if (body.equals("\n")) {
                            return LINE_SEPARATOR;
                        }
                        StringTokenizer tokenizer =
			    new StringTokenizer(body, "\n");
                        StringBuffer bsb = new StringBuffer();
                        while (tokenizer.hasMoreTokens()) {
                            String token = tokenizer.nextToken();
                            bsb.append(token);
                            if (tokenizer.hasMoreTokens()) {
                                bsb.append(LINE_SEPARATOR);
                            }
                        }
                        if (bsb.length() > 0) {
                            body = bsb.toString();
                        }
                        return body;
                    } else
                        return "";
                }
            }

            // pick out return type
            Object/*MParameter*/ rp =
                UmlHelper.getHelper().getCore().getReturnParameter(op);
            if (rp != null) {
                Object/*MClassifier*/ returnType = ModelFacade.getType(rp);
                return generateDefaultReturnStatement((MClassifier)returnType);
            }
        }

        return generateDefaultReturnStatement(null);
    }

    public String generateDefaultReturnStatement(MClassifier cls) {
        if (cls == null)
            return "";

        String clsName = cls.getName();
        if (clsName.equals("void"))
            return "";
        if (clsName.equals("char"))
            return INDENT + "return 'x';" + LINE_SEPARATOR;
        if (clsName.equals("int"))
            return INDENT + "return 0;" + LINE_SEPARATOR;
        if (clsName.equals("boolean"))
            return INDENT + "return false;" + LINE_SEPARATOR;
        if (clsName.equals("byte"))
            return INDENT + "return 0;" + LINE_SEPARATOR;
        if (clsName.equals("long"))
            return INDENT + "return 0;" + LINE_SEPARATOR;
        if (clsName.equals("float"))
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        if (clsName.equals("double"))
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        return INDENT + "return null;" + LINE_SEPARATOR;
    }

    public String generateTaggedValues(MModelElement e) {
        if (_isInUpdateMode)
            return ""; // no tagged values are generated in update mode.
        Collection tvs = e.getTaggedValues();
        if (tvs == null || tvs.size() == 0)
            return "";
        boolean first = true;
        StringBuffer buf = new StringBuffer();
        Iterator iter = tvs.iterator();
        String s = null;
        while (iter.hasNext()) {
            /*
             * 2002-11-07 Jaap Branderhorst Was 
	     *
	     * s = generateTaggedValue((MTaggedValue) iter.next()); 
	     *
	     * which caused problems because the test tags (i.e. tags with
             * name <NotationName.getName()>+TEST_SUFFIX) were still
             * generated.
	     *
             * New code:
             */
            s = generate(/*(MTaggedValue)*/ iter.next());
            // end new code
            if (s != null && s.length() > 0) {
                if (first) {
                    /*
                     * Corrected 2001-09-26 STEFFEN ZSCHALER
                     *
                     * Was:
		     buf.append("// {");
                     *
                     * which caused problems with new lines characters
                     * in tagged values (e.g. comments...). The new
                     * version still has some problems with tagged
                     * values containing "*"+"/" as this closes the
                     * comment prematurely, but comments should be
                     * taken out of the tagged values list anyway...
                     */
                    buf.append("/* {");

                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(s);
            }
        }
        /*
         * Corrected 2001-09-26 STEFFEN ZSCHALER
         *
         * Was:
	 if (!first) buf.append("}\n");
         *
         * which caused problems with new-lines in tagged values.
         */
        if (!first)
            buf.append("}*/").append(LINE_SEPARATOR);

        return buf.toString();
    }

    public String generateTaggedValue(MTaggedValue tv) {
        if (tv == null)
            return "";
        String s = generateUninterpreted(tv.getValue());
        if (s == null || s.length() == 0 || s.equals("/** */"))
            return "";
        String t = tv.getTag();
        if (t.equals("documentation"))
            return "";
        return generateName(t) + "=" + s;
    }

    /**
     * Enhance/Create the doccomment for the given model element,
     * including tags for any OCL constraints connected to the model
     * element. The tags generated are suitable for use with the ocl
     * injector which is part of the Dresden OCL Toolkit and are in
     * detail:
     *
     * &nbsp;@invariant for each invariant specified
     * &nbsp;@precondition for each precondition specified
     * &nbsp;@postcondition for each postcondition specified
     * &nbsp;@key-type specifying the class of the keys of a mapped association
     * &nbsp; Currently mapped associations are not supported yet...
     * &nbsp;@element-type specifying the class referenced in an association
     *
     * @since 2001-09-26 ArgoUML 0.9.3
     * @author Steffen Zschaler
     *
     * @param me the model element for which the documentation comment is needed
     * @param ae the association end which is represented by the model element
     * @return the documentation comment for the specified model element, either
     * enhanced or completely generated
     */
    public String generateConstraintEnrichedDocComment(MModelElement me,
						       MAssociationEnd ae) {
        String s = generateConstraintEnrichedDocComment(me, true, INDENT);

        Object/*MMultiplicity*/ m = ae.getMultiplicity();
        if (!(MMultiplicity.M1_1.equals(m) || MMultiplicity.M0_1.equals(m))) {
            // Multiplicity greater 1, that means we will generate some sort of
            // collection, so we need to specify the element type tag
            StringBuffer sDocComment = new StringBuffer(80);

            // Prepare doccomment
            if (!(s == null || "".equals(s))) {
                // Just remove closing "*/"
                sDocComment.append(s.substring(0, s.indexOf("*/") + 1));
            } else {
                sDocComment
                    .append(INDENT)
                    .append("/**").append(LINE_SEPARATOR)
                    .append(INDENT)
                    .append(" * ").append(LINE_SEPARATOR)
                    .append(INDENT)
                    .append(" *");
            }

            // Build doccomment
            Object/*MClassifier*/ type = ae.getType();
            if (type != null) {
                sDocComment.append(" @element-type ").append(ModelFacade.getName(type));
            } 

	    // REMOVED: 2002-03-11 STEFFEN ZSCHALER: element type
	    // unknown is not recognized by the OCL injector...
	    // else {
	    //     sDocComment += " @element-type unknown";
	    // }
            sDocComment.append(LINE_SEPARATOR).append(INDENT).append(" */");
	    sDocComment.append(LINE_SEPARATOR);
            return sDocComment.toString();
        } else {
            return (s != null) ? s : "";
        }
    }

    /**
     * Enhance/Create the doccomment for the given model element,
     * including tags for any OCL constraints connected to the model
     * element. The tags generated are suitable for use with the ocl
     * injector which is part of the Dresden OCL Toolkit and are in
     * detail:
     *
     * &nbsp;@invariant for each invariant specified
     * &nbsp;@precondition for each precondition specified
     * &nbsp;@postcondition for each postcondition specified
     *
     * @since 2001-09-26 ArgoUML 0.9.3
     * @author Steffen Zschaler
     *
     * @param me the model element for which the documentation comment is needed
     * @param documented if existing tagged values should be generated 
     *                   in addition to javadoc
     * @param indent indent String (usually blanks) for indentation of 
     *               generated comments
     * @return the documentation comment for the specified model
     * element, either enhanced or completely generated
     */
    public static String generateConstraintEnrichedDocComment(
            Object/*MModelElement*/ me,
	    boolean documented,
	    String indent) 
    {
        if (_isFileGeneration)
            documented = true; // always "documented" if we generate file
        // Retrieve any existing doccomment
        String s =
            (VERBOSE_DOCS || DocumentationManager.hasDocs(me))
	    ? DocumentationManager.getDocs(me, indent)
	    : null;
        StringBuffer sDocComment = new StringBuffer(80);

        if (s != null && s.trim().length() > 0) {
            sDocComment.append(s).append(LINE_SEPARATOR);
        }
        cat.debug("documented=" + documented);
        if (!documented)
            return sDocComment.toString();

        // Extract constraints
        Collection cConstraints = ModelFacade.getConstraints(me);

        if (cConstraints.size() == 0) {
            return sDocComment.toString();
        }

        // Prepare doccomment
        if (s != null) {
            // Just remove closing */
            s = sDocComment.toString();
            sDocComment = new StringBuffer(s.substring(0, s.indexOf("*/") + 1));
        } else {
            sDocComment
                .append(INDENT)
                .append("/**").append(LINE_SEPARATOR)
                .append(INDENT)
                .append(" * ").append(LINE_SEPARATOR)
                .append(INDENT)
                .append(" *");
        }

        // Add each constraint

        class TagExtractor
            extends tudresden.ocl.parser.analysis.DepthFirstAdapter {
            private LinkedList m_llsTags = new LinkedList();
            private String m_sConstraintName;
            private int m_nConstraintID = 0;

            public TagExtractor(String sConstraintName) {
                super();

                m_sConstraintName = sConstraintName;
            }

            public Iterator getTags() {
                return m_llsTags.iterator();
            }

            public void caseAConstraintBody(AConstraintBody node) {
                // We don't care for anything below this node, so we
                // do not use apply anymore.
                String sKind =
                    (node.getStereotype() != null)
		    ? (node.getStereotype().toString())
		    : (null);
                String sExpression =
                    (node.getExpression() != null)
		    ? (node.getExpression().toString())
		    : (null);
                String sName =
                    (node.getName() != null)
		    ? (node.getName().getText().toString())
		    : (m_sConstraintName + "_" + (m_nConstraintID++));

                if ((sKind == null) || (sExpression == null)) {
                    return;
                }

                String sTag;
                if (sKind.equals("inv ")) {
                    sTag = "@invariant ";
                } else if (sKind.equals("post ")) {
                    sTag = "@postcondition ";
                } else if (sKind.equals("pre ")) {
                    sTag = "@precondition ";
                } else {
                    return;
                }

                sTag += sName + ": " + sExpression;
                m_llsTags.addLast(sTag);
            }
        }

        tudresden.ocl.check.types.ModelFacade mf =
            new org.argouml.ocl.ArgoFacade(me);
        for (Iterator i = cConstraints.iterator(); i.hasNext();) {
            Object constraint = /*(MConstraint)*/ i.next();

            try {
                OclTree otParsed =
                    OclTree.createTree((String)ModelFacade.getBody(ModelFacade.getBody(constraint)), mf);

                TagExtractor te = new TagExtractor(ModelFacade.getName(constraint));
                otParsed.apply(te);

                for (Iterator j = te.getTags(); j.hasNext();) {
                    sDocComment.append(' ').append(j.next());
		    sDocComment.append(LINE_SEPARATOR);
		    sDocComment.append(INDENT).append(" *");
                }
            } catch (java.io.IOException ioe) {
                // Nothing to be done, should not happen anyway ;-)
            }
        }

        sDocComment.append("/").append(LINE_SEPARATOR);

        return sDocComment.toString();
    }

    public String generateConstraints(MModelElement me) {

        // This method just adds comments to the generated java
        // code. This should be code generated by ocl-argo int he
        // future?
        Collection cs = me.getConstraints();
        if (cs == null || cs.size() == 0)
            return "";
        StringBuffer sb = new StringBuffer(80);
        if (VERBOSE_DOCS)
            sb.append(INDENT).append("// constraints").append(LINE_SEPARATOR);
        // MConstraint[] csarray = (MConstraint[])cs.toArray();
        // cat.debug("Got " + csarray.size() + " constraints.");
        for (Iterator i = cs.iterator(); i.hasNext();) {
            Object constraint = /*(MConstraint)*/ i.next();
            String constrStr = generateConstraint((MConstraint)constraint);
            StringTokenizer st =
                new StringTokenizer(constrStr, LINE_SEPARATOR + "\r");
            while (st.hasMoreElements()) {
                String constrLine = st.nextToken();
                sb.append(INDENT).append("// ").append(constrLine);
		sb.append(LINE_SEPARATOR);
            }
        }
        sb.append(LINE_SEPARATOR);
        return sb.toString();
    }

    public String generateConstraint(MConstraint c) {
        if (c == null)
            return "";
        StringBuffer sb = new StringBuffer(20);
        if (c.getName() != null && c.getName().length() != 0)
            sb.append(generateName(c.getName())).append(": ");
        sb.append(generateExpression(c));
        return sb.toString();
    }

    public String generateAssociationFrom(MAssociation a, MAssociationEnd associationEnd) {
        // TODO: does not handle n-ary associations
        StringBuffer sb = new StringBuffer(80);

        /*
         * Moved into while loop 2001-09-26 STEFFEN ZSCHALER
         *
         * Was:
         *
	 s += DocumentationManager.getDocs(a) + "\n" + INDENT;
	*/

        Collection connections = ModelFacade.getConnections(a);
        Iterator connEnum = connections.iterator();
        while (connEnum.hasNext()) {
            Object associationEnd2 = /*(MAssociationEnd)*/ connEnum.next();
            if (associationEnd2 != associationEnd) {
                /**
                 * Added generation of doccomment 2001-09-26 STEFFEN ZSCHALER
                 *
                 */
                sb.append(INDENT);
		sb.append(generateConstraintEnrichedDocComment(a, (MAssociationEnd)associationEnd2));
                sb.append(generateAssociationEnd((MAssociationEnd)associationEnd2));
            }
        }

        return sb.toString();
    }

    public String generateAssociation(MAssociation a) {
        //    String s = "";
        //     String generatedName = generateName(a.getName());
        //     s += "MAssociation " + generatedName + " {\n";

        //     Iterator endEnum = a.getConnection().iterator();
        //     while (endEnum.hasNext()) {
        //       MAssociationEnd ae = (MAssociationEnd)endEnum.next();
        //       s += generateAssociationEnd(ae);
        //       s += ";\n";
        //     }
        //     s += "}\n";
        //    return s;
        return "";
    }

    public String generateAssociationEnd(MAssociationEnd ae) {
        if (!ae.isNavigable())
            return "";
        if (ae.getAssociation().isAbstract())
            return "";
        //String s = INDENT + "protected ";
        // must be public or generate public navigation method!
        //String s = INDENT + "public ";
        StringBuffer sb = new StringBuffer(80);
        sb.append(INDENT).append(generateCoreAssociationEnd(ae));

        return (sb.append(";").append(LINE_SEPARATOR)).toString();
    }

    public String generateCoreAssociationEnd(MAssociationEnd ae) {
        StringBuffer sb = new StringBuffer(80);
        sb.append(generateVisibility(ae.getVisibility()));

        if (MScopeKind.CLASSIFIER.equals(ae.getTargetScope()))
            sb.append("static ");
        //     String n = ae.getName();
        //     if (n != null && !String.UNSPEC.equals(n)) 
	//         s += generateName(n) + " ";
        //     if (ae.isNavigable()) s += "navigable ";
        //     if (ae.getIsOrdered()) s += "ordered ";
        Object/*MMultiplicity*/ m = ae.getMultiplicity();
        if (MMultiplicity.M1_1.equals(m) || MMultiplicity.M0_1.equals(m))
            sb.append(generateClassifierRef(ae.getType()));
        else
            sb.append("Vector "); //generateMultiplicity(m) + " ";

        sb.append(' ').append(generateAscEndName(ae));

        return sb.toString();
    }

    //   public String generateConstraints(MModelElement me) {
    //     Vector constr = me.getConstraint();
    //     if (constr == null || constr.size() == 0) return "";
    //     String s = "{";
    //     Iterator conEnum = constr.iterator();
    //     while (conEnum.hasNext()) {
    //       s += generateConstraint((MConstraint)conEnum.next());
    //       if (conEnum.hasNext()) s += "; ";
    //     }
    //     s += "}";
    //     return s;
    //   }

    //   public String generateConstraint(MConstraint c) {
    //     return generateExpression(c);
    //   }

    ////////////////////////////////////////////////////////////////
    // internal methods?

    public String generateGeneralization(Collection generalizations) {
        if (generalizations == null)
            return "";
        Collection classes = new ArrayList();
        Iterator enum = generalizations.iterator();
        while (enum.hasNext()) {
            Object generalization = /*(MGeneralization)*/ enum.next();
            Object generalizableElement = ModelFacade.getParent(generalization);
            // assert ge != null
            if (generalizableElement != null)
                classes.add(generalizableElement);
        }
        return generateClassList(classes);
    }

    //  public String generateSpecification(Collection realizations) {
    public String generateSpecification(MClass cls) {
        Collection realizations =
            ModelFacade.getSpecifications(cls);
        if (realizations == null)
            return "";
	cat.debug("realizations: " + realizations.size());
        StringBuffer sb = new StringBuffer(80);
        Iterator clsEnum = realizations.iterator();
        while (clsEnum.hasNext()) {
            Object inter = /*(MInterface)*/ clsEnum.next();
            sb.append(generateClassifierRef(inter));
            if (clsEnum.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public String generateClassList(Collection classifiers) {
        if (classifiers == null)
            return "";
        StringBuffer sb = new StringBuffer(80);
        Iterator clsEnum = classifiers.iterator();
        while (clsEnum.hasNext()) {
            sb.append(generateClassifierRef(/*(MClassifier)*/ clsEnum.next()));
            if (clsEnum.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public String generateVisibility(MVisibilityKind vis) {
        //if (vis == null) return "";
        if (MVisibilityKind.PUBLIC.equals(vis))
            return "public ";
        if (MVisibilityKind.PRIVATE.equals(vis))
            return "private ";
        if (MVisibilityKind.PROTECTED.equals(vis))
            return "protected ";
        return "";
    }

    public String generateVisibility(MFeature f) {
        String _tagged = f.getTaggedValue("src_visibility");
        if (_tagged != null) {
            if (_tagged.trim().equals("")
		|| _tagged.trim().toLowerCase().equals("package")
		|| _tagged.trim().toLowerCase().equals("default"))
                return "";
            else
                return f.getTaggedValue("src_visibility") + " ";
        }
        Object/*MVisibilityKind*/ vis = f.getVisibility();
        //if (vis == null) return "";
        if (MVisibilityKind.PUBLIC.equals(vis))
            return "public ";
        if (MVisibilityKind.PRIVATE.equals(vis))
            return "private ";
        if (MVisibilityKind.PROTECTED.equals(vis))
            return "protected ";
        return "";
    }

    public String generateScope(MFeature f) {
        MScopeKind scope = f.getOwnerScope();
        //if (scope == null) return "";
        if (MScopeKind.CLASSIFIER.equals(scope))
            return "static ";
        return "";
    }

    /**
     * Generate "abstract" keyword for an abstract operation.
     */
    public String generateAbstractness(MOperation op) {
        if (op.isAbstract()) {
            return "abstract ";
        } else {
            return "";
        }
    }

    /**
     * Generate "final" keyword for final operations.
     */
    public String generateChangeability(MOperation op) {
        if (op.isLeaf()) {
            return "final ";
        } else {
            return "";
        }
    }

    public String generateChangability(MStructuralFeature sf) {
        MChangeableKind ck = sf.getChangeability();
        //if (ck == null) return "";
        if (MChangeableKind.FROZEN.equals(ck))
            return "final ";
        //if (MChangeableKind.ADDONLY.equals(ck)) return "final ";
        return "";
    }

    /**
     * Generates "synchronized" keyword for guarded operations.
     * @param op The operation
     * @return String The synchronized keyword if the operation is guarded, 
     *                else "".
     */
    public String generateConcurrency(MOperation op) {
        if (op.getConcurrency() != null
            && op.getConcurrency().getValue() == MCallConcurrencyKind._GUARDED)
	{
            return "synchronized ";
        }
        return "";
    }

    public String generateMultiplicity(MMultiplicity m) {
        if (m == null) {
            return "";
        }
        if (MMultiplicity.M0_N.equals(m))
            return ANY_RANGE;
        Collection v = m.getRanges();
        if (v == null)
            return "";
        StringBuffer sb = new StringBuffer(20);
        Iterator rangeEnum = v.iterator();
        while (rangeEnum.hasNext()) {
            MMultiplicityRange mr = (MMultiplicityRange) rangeEnum.next();
            sb.append(generateMultiplicityRange(mr));
            if (rangeEnum.hasNext())
                sb.append(',');
        }
        return sb.toString();
    }

    public static final String ANY_RANGE = "0..*";
    //public static final String ANY_RANGE = "*";
    // TODO: user preference between "*" and "0..*"

    public String generateMultiplicityRange(MMultiplicityRange mr) {
        Integer lower = new Integer(mr.getLower());
        Integer upper = new Integer(mr.getUpper());
        if (lower.intValue() == -1 && upper.intValue() == -1)
            return ANY_RANGE;
        if (lower.intValue() == -1)
            return "*.." + upper.toString();
        if (upper.intValue() == -1)
            return lower.toString() + "..*";
        if (lower.intValue() == upper.intValue())
            return lower.toString();
        return lower.toString() + ".." + upper.toString();

    }

    public String generateState(MState m) {
        return m.getName();
    }

    public String generateStateBody(MState m) {
        cat.info("GeneratorJava: generating state body");
        StringBuffer sb = new StringBuffer(80);
        Object entryAction = ModelFacade.getEntry(m);
        Object exitAction = ModelFacade.getExit(m);   
        Object doAction = ModelFacade.getDoActivity(m);

        if (entryAction != null) {
            String entryStr = Generate(entryAction);
            if (entryStr.length() > 0)
                sb.append("entry / ").append(entryStr);
        }
        if (doAction != null) {
            String doStr = Generate(doAction);
            if (doStr.length() > 0) {
                if (sb.length() > 0)
                    sb.append(LINE_SEPARATOR);
                sb.append("do / ").append(doStr);
            }
        }
        if (exitAction != null) {
            String exitStr = Generate(exitAction);
            if (sb.length() > 0)
                sb.append(LINE_SEPARATOR);
            if (exitStr.length() > 0)
                sb.append("exit / ").append(exitStr);
        }
        Collection trans = m.getInternalTransitions();
        if (trans != null) {
            Iterator iter = trans.iterator();
            while (iter.hasNext()) {
                if (sb.length() > 0)
                    sb.append(LINE_SEPARATOR);
                sb.append(generateTransition((MTransition) iter.next()));
            }
        }

        /*   if (trans != null) {
	     int size = trans.size();
	     MTransition[] transarray = (MTransition[])trans.toArray();
	     for (int i = 0; i < size; i++) {
	     if (s.length() > 0) s += "\n";
	     s += Generate(transarray[i]);
	     }
	     }*/
        return sb.toString();
    }

    public String generateTransition(MTransition m) {
        StringBuffer sb = new StringBuffer(generate(m.getName()));
        String t = generate(m.getTrigger());
        String g = generate(m.getGuard());
        String e = generate(m.getEffect());
        if (sb.length() > 0)
            sb.append(": ");
        sb.append(t);
        if (g.length() > 0)
            sb.append(" [").append(g).append(']');
        if (e.length() > 0)
            sb.append(" / ").append(e);
        return sb.toString();

        /*  String s = m.getName();
	    String t = generate(m.getTrigger());
	    String g = generate(m.getGuard());
	    String e = generate(m.getEffect());
	    if(s == null) s = "";
	    if(t == null) t = "";
	    if (s.length() > 0 &&
            (t.length() > 0 ||
            (g != null && g.length() > 0) ||
            (e != null && e.length() > 0)))
            s += ": ";
	    s += t;
	    if (g != null && g.length() > 0) s += " [" + g + "]";
	    if (e != null && e.length() > 0) s += " / " + e;
	    return s;*/
    }

    public String generateAction(Object m) {
        // return m.getName();
        
        if (m != null) {
            Object script = ModelFacade.getScript(m);
            if ((script != null)
            && (ModelFacade.getBody(script) != null))                
                return ModelFacade.getBody(script).toString();
        }
        return "";
    }

    public String generateGuard(MGuard m) {
        //return generateExpression(m.getExpression());
        if (m != null && m.getExpression() != null)
            return generateExpression(m.getExpression());
        return "";
    }

    public String generateMessage(MMessage m) {
        if (m == null)
            return "";
        return generateName(m.getName()) + "::" + generateAction(m.getAction());
    }

    public String generateAscEndName(MAssociationEnd ae) {
        String n = ae.getName();
        Object/*MAssociation*/ asc = ModelFacade.getAssociation(ae);
        String ascName = ModelFacade.getName(asc);
        if (n != null && n != null && n.length() > 0) {
            n = generateName(n);
        } else if (
		   ascName != null && ascName != null && ascName.length() > 0) {
            n = generateName(ascName);
        } else {
            n = "my" + generateClassifierRef(ae.getType());
        }
        return n;
    }

    /**
       Gets the Java package name for a given namespace,
       ignoring the root namespace (which is the model).

       @param namespace the namespace
       @return the Java package name
    */
    public String getPackageName(Object namespace) {
        if (namespace == null
	    || !ModelFacade.isANamespace(namespace)
	    || ModelFacade.getNamespace(namespace) == null)
            return "";
        String packagePath = ModelFacade.getName(namespace);
        while ((namespace = ModelFacade.getNamespace(namespace)) != null) {
            // ommit root package name; it's the model's root
            if (ModelFacade.getNamespace(namespace) != null)
                packagePath =
		    ModelFacade.getName(namespace) + '.' + packagePath;
        }
        return packagePath;
    }

    /**
       Update a source code file.

       @param mClassifier The classifier to update from.
       @param file The file to update.
    */
    protected static void update(MClassifier mClassifier, File file)
        throws Exception {
        cat.info("Parsing " + file.getPath());
	  String encoding = null;
        if ( Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING) == null 
	  || Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING).trim().equals(""))
        	encoding = System.getProperty("file.encoding");
	  else
        	encoding = Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING);
        FileInputStream in = new FileInputStream(file);
	  JavaLexer lexer =
		new JavaLexer(new BufferedReader(new InputStreamReader(in, encoding)));
        JavaRecognizer parser = new JavaRecognizer(lexer);
        CodePieceCollector cpc = new CodePieceCollector();
        parser.compilationUnit(cpc);
        in.close();

        File origFile = new File(file.getAbsolutePath());
        File newFile = new File(file.getAbsolutePath() + ".updated");
        File backupFile = new File(file.getAbsolutePath() + ".backup");
        if (backupFile.exists())
            backupFile.delete();
        //cat.info("Generating " + newFile.getPath());
        _isInUpdateMode = true;
        cpc.filter(file, newFile, ModelFacade.getNamespace(mClassifier));
        _isInUpdateMode = false;
        //cat.info("Backing up " + file.getPath());
        file.renameTo(backupFile);
        cat.info("Updating " + file.getPath());
        newFile.renameTo(origFile);
    }

    public boolean canParse() {
        return true;
    }

    public boolean canParse(Object o) {
        return true;
    }

    public String getModuleName() {
        return "GeneratorJava";
    }
    public String getModuleDescription() {
        return "Java Notation and Code Generator";
    }
    public String getModuleAuthor() {
        return "ArgoUML Core";
    }
    public String getModuleVersion() {
        return ArgoVersion.getVersion();
    }
    public String getModuleKey() {
        return "module.language.java.generator";
    }

    /**
     * Returns the _lfBeforeCurly.
     * @return boolean
     */
    public boolean isLfBeforeCurly() {
        return _lfBeforeCurly;
    }

    /**
     * Returns the _verboseDocs.
     * @return boolean
     */
    public boolean isVerboseDocs() {
        return _verboseDocs;
    }

    /**
     * Sets the _lfBeforeCurly.
     * @param _lfBeforeCurly The _lfBeforeCurly to set
     */
    public void setLfBeforeCurly(boolean _lfBeforeCurly) {
        this._lfBeforeCurly = _lfBeforeCurly;
    }

    /**
     * Sets the _verboseDocs.
     * @param _verboseDocs The _verboseDocs to set
     */
    public void setVerboseDocs(boolean _verboseDocs) {
        this._verboseDocs = _verboseDocs;
    }
    

    /**
     * @see org.argouml.application.api.Pluggable#inContext(java.lang.Object[])
     */
    public boolean inContext(Object[] o) {
	return true;
    }
    
    public boolean isModuleEnabled() { return true; }

}

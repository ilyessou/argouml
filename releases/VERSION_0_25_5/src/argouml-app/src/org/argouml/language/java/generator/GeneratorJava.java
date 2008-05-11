// $Id$
// Copyright (c) 1996-2007 The Regents of the University of California. All
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

package org.argouml.language.java.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.application.helpers.ApplicationVersion;
import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.configuration.Configuration;
import org.argouml.model.Model;
import org.argouml.moduleloader.ModuleInterface;
import org.argouml.ocl.ArgoFacade;
import org.argouml.uml.DocumentationManager;
import org.argouml.uml.generator.CodeGenerator;
import org.argouml.uml.generator.GeneratorHelper;
import org.argouml.uml.generator.GeneratorManager;
import org.argouml.uml.generator.Language;
import org.argouml.uml.generator.TempFileUtils;
import org.argouml.uml.reveng.ImportInterface;

import tudresden.ocl.OclTree;
import tudresden.ocl.parser.analysis.DepthFirstAdapter;
import tudresden.ocl.parser.node.AConstraintBody;
import antlr.ANTLRException;

/**
 * FileGenerator implementing class to generate Java for display in diagrams
 * and in text fields in the ArgoUML user interface.
 *
 * @stereotype singleton
 */
public class GeneratorJava implements CodeGenerator, ModuleInterface {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(GeneratorJava.class);

    private boolean verboseDocs;
    private boolean lfBeforeCurly;
    private static final boolean VERBOSE_DOCS = false;
    private static final String LINE_SEPARATOR =
	System.getProperty("line.separator");
    private static final String LANG_PACKAGE = "java.lang";

    private static final Set JAVA_TYPES;
    static {
	Set<String> types = new HashSet<String>();
	types.add("void");
	types.add("boolean");
	types.add("byte");
	types.add("char");
	types.add("int");
	types.add("short");
	types.add("long");
	types.add("float");
	types.add("double");
	JAVA_TYPES = Collections.unmodifiableSet(types);
    }

    // TODO: make it configurable
    // next two flags shows in what mode we are working
    /**
     * <code>true</code> when GenerateFile.
     */
    private static boolean isFileGeneration;

    /**
     * <code>true</code> if GenerateFile in Update Mode.
     */
    private static boolean isInUpdateMode;

    /**
     * The instance.
     */
    private static final GeneratorJava SINGLETON = new GeneratorJava();

    /**
     * Two spaces used for indenting code in classes.
     */
    private static final String INDENT = "  ";

    /**
     * Get the generator.
     *
     * @return The singleton.
     */
    public static GeneratorJava getInstance() {
        return SINGLETON;
    }

    /**
     * Constructor.
     */
    protected GeneratorJava() {
        Language java = GeneratorHelper.makeLanguage(
                "Java", "Java", 
                ResourceLoaderWrapper.lookupIconResource("JavaNotation"));
        GeneratorManager.getInstance().addGenerator(java, this);
    }

    /**
     * Generates a file for the classifier.
     * This method could have been static if it where not for the need to
     * call it through the Generatorinterface.
     * Returns the full path name of the the generated file or
     * null if no file can be generated.
     *
     * @param modelElement the element to be generated
     * @param path the path where the element will be generated
     * @return String full path name of the the generated file
     */
    private String generateFile(Object modelElement, String path) {
        String name = Model.getFacade().getName(modelElement);
        if (name == null || name.length() == 0) {
            return null;
        }
        Object classifier = modelElement;
        String filename = name + ".java";
        StringBuilder sbPath = new StringBuilder(path);
        if (!path.endsWith(FILE_SEPARATOR)) {
            sbPath.append(FILE_SEPARATOR);
        }

        String packagePath =
	    getPackageName(Model.getFacade().getNamespace(classifier));

        int lastIndex = -1;
        do {
            File f = new File(sbPath.toString());
            if (!f.isDirectory()) {
                if (!f.mkdir()) {
                    LOG.error(" could not make directory " + path);
                    return null;
                }
            }
            
            if (lastIndex == packagePath.length()) {
                break;
            }

            int index = packagePath.indexOf(".", lastIndex + 1);
            if (index == -1) {
                index = packagePath.length();
            }

            sbPath.append(packagePath.substring(lastIndex + 1, index)
                + FILE_SEPARATOR);
            lastIndex = index;
        } while (true);

        String pathname = sbPath.toString() + filename;
        //cat.info("-----" + pathname + "-----");

        //now decide whether file exist and need an update or is to be
        //newly generated
        File f = new File(pathname);
        isFileGeneration = true; // used to produce method javadoc
        if (f.exists()) {
            try {
                update(classifier, f);
            } catch (Exception exp) {
                isInUpdateMode = false;
                isFileGeneration = false;
                LOG.error("FAILED: " + f.getPath(), exp);
            }

            //cat.info("----- end generating -----");
            isFileGeneration = false;
            return pathname;
        }

        //String pathname = path + filename;
        // TODO: package, project basepath, tagged values to configure
        LOG.info("Generating (new) " + f.getPath());
        isFileGeneration = true;
        String header =
	    SINGLETON.generateHeader(classifier, pathname, packagePath);
        String src = SINGLETON.generateClassifier(classifier);
        BufferedWriter fos = null;
        try {
	    if (Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING) == null
		|| Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING)
		    .trim().equals("")) {
            	fos =
		    new BufferedWriter(
		            new OutputStreamWriter(new FileOutputStream(f),
		                    System.getProperty("file.encoding")));
	    } else {
            	fos =
		    new BufferedWriter(
		            new OutputStreamWriter(new FileOutputStream(f),
		                    Configuration.getString(
		                            Argo.KEY_INPUT_SOURCE_ENCODING)));
	    }
            fos.write(header);
            fos.write(src);
        } catch (IOException exp) {
            LOG.error("IO Exception: " + exp + ", for file: " + f.getPath());
        } finally {
            isFileGeneration = false;
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException exp) {
                LOG.error("FAILED: " + f.getPath());
            }
        }

        //cat.info("----- end updating -----");
        return pathname;
    }

    private String generateHeader(Object cls,
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

    /**
     * Generates code for some modelelement. Subclasses should
     * implement this to generate code for different notations.
     * @param o the element to be generated
     * @return String the generated code
     */
    private String generate(Object o) {
        if (o == null) {
            return "";
        }
        if (Model.getFacade().isAActionState(o)) {
            return generateActionState(o);
        }
        if (Model.getFacade().isAExtensionPoint(o)) {
            return generateExtensionPoint(o);
        }
        if (Model.getFacade().isAOperation(o)) {
            return generateOperation(o, false);
        }
        if (Model.getFacade().isAAttribute(o)) {
            return generateAttribute(o, false);
        }
        if (Model.getFacade().isAParameter(o)) {
            return generateParameter(o);
        }
        if (Model.getFacade().isAPackage(o)) {
            return generatePackage(o);
        }
        if (Model.getFacade().isAClassifier(o)) {
            return generateClassifier(o);
        }
        if (Model.getFacade().isAExpression(o)) {
            return generateExpression(o);
        }
        if (o instanceof String) {
            return generateName((String) o);
        }
        if (o instanceof String) {
            return generateUninterpreted((String) o);
        }
        if (Model.getFacade().isAStereotype(o)) {
            return generateStereotype(o);
        }
        if (Model.getFacade().isATaggedValue(o)) {
            return generateTaggedValue(o);
        }
        if (Model.getFacade().isAAssociation(o)) {
            return generateAssociation(o);
        }
        if (Model.getFacade().isAAssociationEnd(o)) {
            return generateAssociationEnd(o);
        }
        if (Model.getFacade().isAMultiplicity(o)) {
            return generateMultiplicity(o);
        }
        if (Model.getFacade().isAState(o)) {
            return generateState(o);
        }
        if (Model.getFacade().isATransition(o)) {
            return generateTransition(o);
        }
        if (Model.getFacade().isAAction(o)) {
            return generateAction(o);
        }
        if (Model.getFacade().isACallAction(o)) {
            return generateAction(o);
        }
        if (Model.getFacade().isAGuard(o)) {
            return generateGuard(o);
        }
        if (Model.getFacade().isAMessage(o)) {
            return generateMessage(o);
        }
        if (Model.getFacade().isAEvent(o)) {
            return generateEvent(o);
        }
        if (Model.getFacade().isAVisibilityKind(o)) {
            return generateVisibility(o);
        }

        if (Model.getFacade().isAModelElement(o)) {
            return generateName(Model.getFacade().getName(o));
        }

        if (o == null) {
            return "";
        }

        return o.toString();
    }

    private String generateImports(Object cls, String packagePath) {
        // TODO: check also generalizations
        StringBuffer sb = new StringBuffer(80);
        HashSet<String> importSet = new java.util.HashSet<String>();

        // now check packages of all feature types
        for (Object mFeature : Model.getFacade().getFeatures(cls)) {
            if (Model.getFacade().isAAttribute(mFeature)) {
                String ftype = generateImportType(Model.getFacade().getType(
                        mFeature), packagePath);
                if (ftype != null) {
                    importSet.add(ftype);
                }
            } else if (Model.getFacade().isAOperation(mFeature)) {
                // check the parameter types
                for (Object parameter : Model.getFacade().getParameters(
                        mFeature)) {
                    String ftype = generateImportType(Model.getFacade()
                            .getType(parameter), packagePath);
                    if (ftype != null) {
                        importSet.add(ftype);
                    }
                }

                // check the return parameter types
                for (Object parameter 
                        : Model.getCoreHelper().getReturnParameters(mFeature)) {
                    String ftype = generateImportType(Model.getFacade()
                            .getType(parameter), packagePath);
                    if (ftype != null) {
                        importSet.add(ftype);
                    }
                }

                // check raised signals
                for (Object signal 
                        : Model.getFacade().getRaisedSignals(mFeature)) {
                    if (!Model.getFacade().isAException(signal)) {
                        continue;
                    }
                    String ftype = generateImportType(Model.getFacade()
                            .getType(signal), packagePath);
                    if (ftype != null) {
                        importSet.add(ftype);
                    }
                }
            }

        }

        for (Object gen : Model.getFacade().getGeneralizations(cls)) {
            Object parent = Model.getFacade().getGeneral(gen);
            if (parent == cls) {
                continue;
            }

            String ftype = generateImportType(parent, packagePath);
            if (ftype != null) {
                importSet.add(ftype);
            }
        }

        // now check packages of the interfaces
        for (Object iface : Model.getFacade().getSpecifications(cls)) {
            String ftype = generateImportType(iface, packagePath);
            if (ftype != null) {
                importSet.add(ftype);
            }
        }

        // check association end types
        for (Object associationEnd : Model.getFacade().getAssociationEnds(cls)) {
            Object association =
                Model.getFacade().getAssociation(associationEnd);
            for (Object associationEnd2 
                    : Model.getFacade().getConnections(association)) {
                if (associationEnd2 != associationEnd
                        && Model.getFacade().isNavigable(associationEnd2)
                        && !Model.getFacade().isAbstract(
                                Model.getFacade().getAssociation(
                                        associationEnd2))) {
                    // association end found
                    if (Model.getFacade().getUpper(associationEnd2) != 1) {
                        importSet.add("java.util.Vector");
                    } else {
                        String ftype =
                            generateImportType(Model.getFacade().getType(
                                    associationEnd2),
                                    packagePath);
                        if (ftype != null) {
                            importSet.add(ftype);
                        }
                    }
                }
            }

        }
        // finally generate the import statements
        for (String importType : importSet) {
            sb.append("import ").append(importType).append(";");
	    sb.append(LINE_SEPARATOR);
        }
        if (!importSet.isEmpty()) {
            sb.append(LINE_SEPARATOR);
        }
        return sb.toString();
    }

    private String generateImportType(Object type, String exclude) {
        String ret = null;

        if (Model.getFacade().isADataType(type)
                && JAVA_TYPES.contains(Model.getFacade().getName(type))) {
            return null;
        }

        if (type != null && Model.getFacade().getNamespace(type) != null) {
            String p = getPackageName(Model.getFacade().getNamespace(type));
            if (!p.equals(exclude) && !p.equals(LANG_PACKAGE)) {
		if (p.length() > 0) {
		    ret = p + '.' + Model.getFacade().getName(type);
		} else {
		    ret = Model.getFacade().getName(type);
		}
	    }
        }
        return ret;
    }

    /*
     * Generate code for an extension point.<p>
     *
     * @param ep  The extension point to generate for
     *
     * @return    The generated code string. Always empty in this
     *            implementation.
     */
    private String generateExtensionPoint(Object ep) {
        return null;
    }

    /**
     * Generate source code for an operation.
     * <p>
     * NOTE: This needs to be package visibility because it is used in
     * OperationCodePiece.
     * 
     * @param op
     *            UML Operation to generate code for
     * @param documented
     *            flag indicating documentation comments should be included.
     * @return String containing generated code.
     */
    String generateOperation(Object op, boolean documented) {
        if (isFileGeneration) {
            documented = true; // fix Issue 1506
        }
        StringBuffer sb = new StringBuffer(80);
        String nameStr = null;
        boolean constructor = false;

        String name = null;
        for (Object o : Model.getFacade().getStereotypes(op)) {
            name = Model.getFacade().getName(o);
            if ("create".equals(name)) {
		break;
	    }
        }
        if ("create".equals(name)) {
            // constructor
            nameStr =
                generateName(Model.getFacade().getName(
                    Model.getFacade().getOwner(op)));
            constructor = true;
        } else {
            nameStr = generateName(Model.getFacade().getName(op));
        }
        // Each pattern here must be similar to corresponding code piece
        // Operation code piece doesn't start with '\n'
        // so the next line is commented. See Issue 1505
        //sb.append(LINE_SEPARATOR); // begin with a blank line
        if (documented) {
            String s =
                generateConstraintEnrichedDocComment(op, documented, INDENT);
            if (s != null && s.trim().length() > 0) {
		// should starts as the code piece
                sb.append(s).append(INDENT);
	    }
        }

        sb.append(generateVisibility(op));
        sb.append(generateAbstractness(op));
        sb.append(generateScope(op));
        sb.append(generateChangeability(op));
        sb.append(generateConcurrency(op));

        // pick out return type
        Collection returnParams = Model.getCoreHelper().getReturnParameters(op);
        Object rp;
        if (returnParams.size() == 0) {
            rp = null;
        } else {
            rp = returnParams.iterator().next();
        }
        if (returnParams.size() > 1)  {
            LOG.warn("Java generator only handles one return parameter"
                    + " - Found " + returnParams.size()
                    + " for " + Model.getFacade().getName(op));
        }
        if (rp != null && !constructor) {
            Object returnType = Model.getFacade().getType(rp);
            if (returnType == null) {
                sb.append("void ");
            } else {
                sb.append(generateClassifierRef(returnType)).append(' ');
            }
        }

        // name and params
        List params = new ArrayList(Model.getFacade().getParameters(op));
        params.remove(rp);

        sb.append(nameStr).append('(');

        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(generateParameter(params.get(i)));
            }
        }

        sb.append(')');

	Collection c = Model.getFacade().getRaisedSignals(op);
	if (!c.isEmpty()) {
	    Iterator it = c.iterator();
	    boolean first = true;
	    
	    while (it.hasNext()) {
		Object signal = it.next();

		if (!Model.getFacade().isAException(signal)) {
		    continue;
		}

		if (first) {
		    sb.append(" throws ");
		} else {
		    sb.append(", ");
		}

		sb.append(Model.getFacade().getName(it.next()));
		first = false;
	    }
	}

        return sb.toString();
    }

    private String generateAttribute(Object attr, boolean documented) {
        if (isFileGeneration) {
            documented = true; // always "documented" if we generate file.
	}
        StringBuffer sb = new StringBuffer(80);
        if (documented) {
            String s =
                generateConstraintEnrichedDocComment(attr, documented, INDENT);
            if (s != null && s.trim().length() > 0) {
                sb.append(s).append(INDENT);
	    }
        }
        sb.append(generateCoreAttribute(attr));
        sb.append(";").append(LINE_SEPARATOR);

        return sb.toString();
    }

    String generateCoreAttribute(Object attr) {
        StringBuffer sb = new StringBuffer(80);
        sb.append(generateVisibility(attr));
        sb.append(generateScope(attr));
        sb.append(generateChangability(attr));
        Object type = Model.getFacade().getType(attr);
        Object multi = Model.getFacade().getMultiplicity(attr);
        // handle multiplicity here since we need the type
        // actually the API of generator is buggy since to generate
        // multiplicity correctly we need the attribute too
        if (type != null && multi != null) {
            if (Model.getFacade().getUpper(multi) == 1) {
                sb.append(generateClassifierRef(type)).append(' ');
            } else if (Model.getFacade().isADataType(type)) {
                sb.append(generateClassifierRef(type)).append("[] ");
            } else {
                sb.append("java.util.Vector ");
            }
        }

        sb.append(generateName(Model.getFacade().getName(attr)));
        Object init = Model.getFacade().getInitialValue(attr);
        if (init != null) {
            String initStr = generateExpression(init).trim();
            if (initStr.length() > 0) {
                sb.append(" = ").append(initStr);
	    }
        }

        return sb.toString();
    }

    private String generateParameter(Object parameter) {
        StringBuffer sb = new StringBuffer(20);
        //TODO: qualifiers (e.g., const)
        //TODO: stereotypes...
        sb.append(generateClassifierRef(Model.getFacade().getType(parameter)));
	sb.append(' ');
        sb.append(generateName(Model.getFacade().getName(parameter)));
        //TODO: initial value
        return sb.toString();
    }

    private String generatePackage(Object p) {
        StringBuffer sb = new StringBuffer(80);
        String packName = generateName(Model.getFacade().getName(p));
        sb.append("package ").append(packName).append(" {");
	sb.append(LINE_SEPARATOR);
        Collection ownedElements = Model.getFacade().getOwnedElements(p);
        for (Object modelElement : ownedElements) {
            // This is the only remaining references to generate(), if it
            // can be made more specific, we can remove that method - tfm
            // (do we support anything other than classifiers in a package?)
            sb.append(generate(modelElement));
            sb.append(LINE_SEPARATOR).append(LINE_SEPARATOR);
        }
        sb.append(LINE_SEPARATOR).append("})").append(LINE_SEPARATOR);
        return sb.toString();
    }

    /**
     * Generate the start sequence for a classifier. The start sequence is
     * everything from the preceding javadoc comment to the opening curly brace.
     * Start sequences are non-empty for classes and interfaces only.
     *
     * This method is intended for package internal usage only.
     *
     * @param cls the classifier for which to generate the start sequence
     *
     * @return the generated start sequence
     */
    StringBuffer generateClassifierStart(Object cls) {
        String sClassifierKeyword;
        if (Model.getFacade().isAClass(cls)) {
            sClassifierKeyword = "class";
        } else if (Model.getFacade().isAInterface(cls)) {
            sClassifierKeyword = "interface";
        } else {
            return null; // actors, use cases etc.
        }

        StringBuffer sb = new StringBuffer(80);

        // Add the comments for this classifier first.
        // Each pattern here must be similar to corresponding code piece
        // Classfier code piece doesn't start with LINE_SEPARATOR
        // so the next line is commented. See Issue 1505
        //sb.append (LINE_SEPARATOR);
        sb.append(DocumentationManager.getComments(cls));
        sb.append(generateConstraintEnrichedDocComment(cls, true, ""));

        // Now add visibility, but not for non public top level classifiers
        if (Model.getFacade().isPublic(cls)
                || Model.getFacade().isAClassifier(
			Model.getFacade().getNamespace(cls))) {
            sb.append(generateVisibility(Model.getFacade().getVisibility(cls)));
        }

        // Add other modifiers in JLS order
        if (Model.getFacade().isAbstract(cls)
                && !(Model.getFacade().isAInterface(cls))) {
            sb.append("abstract ");
        }

        if (Model.getFacade().isLeaf(cls)) {
            sb.append("final ");
        }

        // add additional modifiers
        // TODO: This is for backward compatibility with old models reverse 
        // engineered with earlier versions of ArgoUML.  As of 0.24, and 
        // probably earlier, ArgoUML should be able to capture all necessary
        // information in the model itself. - tfm - 20070217
	Object smod =
                Model.getFacade().getTaggedValue(
                        cls, ImportInterface.SOURCE_MODIFIERS_TAG);
        if (smod != null && Model.getFacade().getValue(smod) != null) {
            sb.append(" ");
	    sb.append(Model.getFacade().getValue(smod));
	    sb.append(" ");
	}

        // add classifier keyword and classifier name
        sb.append(sClassifierKeyword).append(" ");
	sb.append(generateName(Model.getFacade().getName(cls)));

        // add base class/interface
        String baseClass =
	    generateGeneralization(Model.getFacade().getGeneralizations(cls));
        if (!baseClass.equals("")) {
            sb.append(" ").append("extends ").append(baseClass);
        }

        // add implemented interfaces, if needed
        // UML: realizations!
        if (Model.getFacade().isAClass(cls)) {
            String interfaces = generateSpecification(cls);
	    LOG.debug("Specification: " + interfaces);
            if (!interfaces.equals("")) {
                sb.append(" ").append("implements ").append(interfaces);
            }
        }

        // add opening brace
        sb.append(lfBeforeCurly ? (LINE_SEPARATOR + "{") : " {");

        // list tagged values for documentation
        String tv = generateTaggedValues(cls);
        if (tv != null && tv.length() > 0) {
            sb.append(LINE_SEPARATOR).append(INDENT).append(tv);
        }

        return sb;
    }

    private StringBuffer generateClassifierEnd(Object cls) {
        StringBuffer sb = new StringBuffer();
        if (Model.getFacade().isAClass(cls)
                || Model.getFacade().isAInterface(cls)) {
            if (verboseDocs) {
                String classifierkeyword = null;
                if (Model.getFacade().isAClass(cls)) {
                    classifierkeyword = "class";
                } else {
                    classifierkeyword = "interface";
                }
                sb.append(LINE_SEPARATOR);
		sb.append("//end of ").append(classifierkeyword);
		sb.append(" ").append(Model.getFacade().getName(cls));
		sb.append(LINE_SEPARATOR);
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
     * @return the complete classifier code, i.e., sbPrefix plus the classifier
     *         end sequence
     */
    StringBuffer appendClassifierEnd(StringBuffer sbPrefix, Object cls) {
        sbPrefix.append(generateClassifierEnd(cls));

        return sbPrefix;
    }

    /*
     * Generates code for a classifier. In case of Java code is
     * generated for classes and interfaces only at the moment.
     */
    private String generateClassifier(Object cls) {
        StringBuffer returnValue = new StringBuffer();
        StringBuffer start = generateClassifierStart(cls);
        if ((start != null) && (start.length() > 0)) {
            StringBuffer body = generateClassifierBody(cls);
            StringBuffer end = generateClassifierEnd(cls);
            returnValue.append(start.toString());
            if ((body != null) && (body.length() > 0)) {
                returnValue.append(LINE_SEPARATOR);
                returnValue.append(body);
                if (lfBeforeCurly) {
                    returnValue.append(LINE_SEPARATOR);
                }
            }
            returnValue.append((end != null) ? end.toString() : "");
        }
        return returnValue.toString();
    }

    /*
     * Generates the body of a class or interface.
     */
    private StringBuffer generateClassifierBody(Object cls) {
        StringBuffer sb = new StringBuffer();
        if (Model.getFacade().isAClass(cls)
                || Model.getFacade().isAInterface(cls)) {
            String tv = null; // helper for tagged values

            // add attributes
            Collection sFeatures = 
                Model.getFacade().getStructuralFeatures(cls);

            if (!sFeatures.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                if (verboseDocs && Model.getFacade().isAClass(cls)) {
                    sb.append(INDENT).append("// Attributes");
		    sb.append(LINE_SEPARATOR);
                }

		boolean first = true;
		for (Object structuralFeature : sFeatures) {
		    if (!first) {
			sb.append(LINE_SEPARATOR);
		    }
		    sb.append(INDENT);
                    // The only type of StructuralFeature is an Attribute
                    sb.append(generateAttribute(structuralFeature, false));

                    tv = generateTaggedValues(structuralFeature);
                    if (tv != null && tv.length() > 0) {
                        sb.append(INDENT).append(tv);
                    }
		    first = false;
                }
            }

            // add attributes implementing associations
            Collection ends = Model.getFacade().getAssociationEnds(cls);
            if (!ends.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                if (verboseDocs && Model.getFacade().isAClass(cls)) {
                    sb.append(INDENT).append("// Associations");
		    sb.append(LINE_SEPARATOR);
                }

                for (Object associationEnd : ends) {
                    Object association =
			Model.getFacade().getAssociation(associationEnd);

                    sb.append(generateAssociationFrom(association,
						      associationEnd));

                    tv = generateTaggedValues(association);
                    if (tv != null && tv.length() > 0) {
                        sb.append(INDENT).append(tv);
                    }
                }
            }

            // Inner classes
            Collection elements = Model.getFacade().getOwnedElements(cls);
            for (Iterator i = elements.iterator(); i.hasNext();) {
                Object element = i.next();
                if (Model.getFacade().isAClass(element)
		    || Model.getFacade().isAInterface(element)) {

                    sb.append(generateClassifier(element));
                }
            }

            // add operations
            // TODO: constructors
            Collection bFeatures = Model.getFacade().getOperations(cls);

            if (!bFeatures.isEmpty()) {
                sb.append(LINE_SEPARATOR);
                if (verboseDocs) {
                    sb.append(INDENT).append("// Operations");
		    sb.append(LINE_SEPARATOR);
                }

		boolean first = true;
		for (Object behavioralFeature : bFeatures) {

		    if (!first) {
                        sb.append(LINE_SEPARATOR);
                    }
		    sb.append(INDENT);
                    sb.append(generateOperation(behavioralFeature, false));

                    tv = generateTaggedValues(behavioralFeature);

                    if ((Model.getFacade().isAClass(cls))
                            && (Model.getFacade()
                                    .isAOperation(behavioralFeature))
                            && (!Model.getFacade()
                                    .isAbstract(behavioralFeature))) {
                        if (lfBeforeCurly) {
                            sb.append(LINE_SEPARATOR).append(INDENT);
                        } else {
                            sb.append(' ');
                        }
                        sb.append('{');

                        if (tv.length() > 0) {
                            sb.append(LINE_SEPARATOR).append(INDENT).append(tv);
                        }

                        // there is no ReturnType in behavioral feature (UML)
                        sb.append(LINE_SEPARATOR);
			sb.append(generateMethodBody(behavioralFeature));
			sb.append(INDENT);
			sb.append("}").append(LINE_SEPARATOR);
                    } else {
                        sb.append(";").append(LINE_SEPARATOR);
                        if (tv.length() > 0) {
                            sb.append(INDENT).append(tv).append(LINE_SEPARATOR);
                        }
                    }

		    first = false;
                }
            }
        }
        return sb;
    }

    /*
     * Generate the body of a method associated with the given
     * operation. This assumes there's at most one method
     * associated!
     *
     * If no method is associated with the operation, a default
     * method body will be generated.
     */
    private String generateMethodBody(Object op) {
        //cat.info("generateMethodBody");
        if (op != null) {
            for (Object m : Model.getFacade().getMethods(op)) {
                if (m != null) {
                    if (Model.getFacade().getBody(m) != null) {
                        String body =
			    (String) Model.getFacade().getBody(
			            Model.getFacade().getBody(m));
			// Note that this will not preserve empty lines
			// in the body
                        StringTokenizer tokenizer =
			    new StringTokenizer(body, "\r\n");
                        StringBuffer bsb = new StringBuffer();
			while (tokenizer.hasMoreTokens()) {
			    String token = tokenizer.nextToken();
			    if (token.length() > 0) {
				bsb.append(token);
				bsb.append(LINE_SEPARATOR);
			    }
                        }
                        if (bsb.length() <= 0) {
			    // generateClassifierBody relies on the string
			    // ending with a new-line
			    bsb.append(LINE_SEPARATOR);
                        }
                        return bsb.toString();
                    }
                    return "";
                }
            }

            // pick out return type
            Collection returnParams =
		Model.getCoreHelper().getReturnParameters(op);
            Object rp;
            if (returnParams.size() == 0) {
                rp = null;
            } else {
                rp = returnParams.iterator().next();
            }
            if (returnParams.size() > 1)  {
                LOG.warn("Java generator only handles one return parameter"
                        + " - Found " + returnParams.size()
                        + " for " + Model.getFacade().getName(op));
            }
            if (rp != null) {
                Object returnType = Model.getFacade().getType(rp);
                return generateDefaultReturnStatement(returnType);
            }
        }

        return generateDefaultReturnStatement(null);
    }

    private String generateDefaultReturnStatement(Object cls) {
        if (cls == null) {
            return "";
        }

        String clsName = Model.getFacade().getName(cls);
        if (clsName.equals("void")) {
            return "";
        }
        if (clsName.equals("char")) {
            return INDENT + "return 'x';" + LINE_SEPARATOR;
        }
        if (clsName.equals("int")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("boolean")) {
            return INDENT + "return false;" + LINE_SEPARATOR;
        }
        if (clsName.equals("byte")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("long")) {
            return INDENT + "return 0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("float")) {
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        }
        if (clsName.equals("double")) {
            return INDENT + "return 0.0;" + LINE_SEPARATOR;
        }
        return INDENT + "return null;" + LINE_SEPARATOR;
    }

    private String generateTaggedValues(Object e) {
        if (isInUpdateMode) {
            return ""; // no tagged values are generated in update mode.
	}
        Iterator iter = Model.getFacade().getTaggedValues(e);
        if (iter == null) {
            return "";
	}
        boolean first = true;
        StringBuffer buf = new StringBuffer();
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
            s = generateTaggedValue(iter.next());
            // end new code
            if (s != null && s.length() > 0) {
                if (first) {
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
        if (!first) {
            buf.append("}*/").append(LINE_SEPARATOR);
	}

        return buf.toString();
    }

    private String generateTaggedValue(Object tv) {
        if (tv == null) {
            return "";
	}
        String s = generateUninterpreted(Model.getFacade().getValueOfTag(tv));
        if (s == null || s.length() == 0 || s.equals("/** */")) {
            return "";
	}
        String t = Model.getFacade().getTagOfTag(tv);
        if (Argo.DOCUMENTATION_TAG.equals(t)) {
            return "";
	}
        return generateName(t) + "=" + s;
    }

    /*
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
    private String generateConstraintEnrichedDocComment(Object me, Object ae) {
        String s = generateConstraintEnrichedDocComment(me, true, INDENT);

        Object m = Model.getFacade().getMultiplicity(ae);
        if (Model.getFacade().getUpper(m) != 1) {
            // Multiplicity greater 1, that means we will generate some sort of
            // collection, so we need to specify the element type tag
            StringBuffer sDocComment = new StringBuffer(80);

            // Prepare doccomment
            if (!(s == null || "".equals(s))) {
                // Just remove closing "*/"
                sDocComment.append(s.substring(0, s.indexOf("*/") + 1));
            } else {
                sDocComment.append(INDENT).append("/**").append(LINE_SEPARATOR);
		sDocComment.append(INDENT).append(" * ").append(LINE_SEPARATOR);
		sDocComment.append(INDENT).append(" *");
            }

            // Build doccomment
            Object type = Model.getFacade().getType(ae);
            if (type != null) {
                sDocComment.append(" @element-type ");
		sDocComment.append(Model.getFacade().getName(type));
            }

	    // REMOVED: 2002-03-11 STEFFEN ZSCHALER: element type
	    // unknown is not recognized by the OCL injector...
	    // else {
	    //     sDocComment += " @element-type unknown";
	    // }
            sDocComment.append(LINE_SEPARATOR).append(INDENT).append(" */");
	    sDocComment.append(LINE_SEPARATOR);
            return sDocComment.toString();
        }
        return (s != null) ? s : "";
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
            Object me,
	    boolean documented,
	    String indent) {
        if (isFileGeneration) {
            documented = true; // always "documented" if we generate file
        }
        // Retrieve any existing doccomment
        String s =
            (VERBOSE_DOCS || DocumentationManager.hasDocs(me))
	    ? DocumentationManager.getDocs(me, indent)
	    : null;
        StringBuffer sDocComment = new StringBuffer(80);

        if (s != null && s.trim().length() > 0) {
            sDocComment.append(s).append(LINE_SEPARATOR);
        }
        LOG.debug("documented=" + documented);
        if (!documented) {
            return sDocComment.toString();
        }

        // Extract constraints
        Collection cConstraints = Model.getFacade().getConstraints(me);

        if (cConstraints.size() == 0) {
            return sDocComment.toString();
        }

        // Prepare doccomment
        if (s != null) {
            // Just remove closing */
            s = sDocComment.toString();
            sDocComment = new StringBuffer(s.substring(0, s.indexOf("*/") + 1));
        } else {
            sDocComment.append(INDENT).append("/**").append(LINE_SEPARATOR);
	    sDocComment.append(INDENT).append(" * ").append(LINE_SEPARATOR);
	    sDocComment.append(INDENT).append(" *");
        }

        // Add each constraint

        class TagExtractor extends DepthFirstAdapter {
            private LinkedList<String> llsTags = new LinkedList<String>();
            private String constraintName;
            private int constraintID;

            /**
             * Constructor.
             *
             * @param sConstraintName The constraint name.
             */
            public TagExtractor(String sConstraintName) {
                super();

                constraintName = sConstraintName;
            }

            public Iterator getTags() {
                return llsTags.iterator();
            }
            
            /*
             * @see tudresden.ocl.parser.analysis.Analysis#caseAConstraintBody(tudresden.ocl.parser.node.AConstraintBody)
             */
            @Override
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
		    ? (node.getName().getText())
		    : (constraintName + "_" + (constraintID++));

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
                llsTags.addLast(sTag);
            }
        }

        tudresden.ocl.check.types.ModelFacade mf = new ArgoFacade(me);
        for (Object constraint : cConstraints) {
            try {
		String body =
		    (String) Model.getFacade().getBody(
		            Model.getFacade().getBody(constraint));
                OclTree otParsed = OclTree.createTree(body, mf);

                TagExtractor te =
		    new TagExtractor(Model.getFacade().getName(constraint));
                otParsed.apply(te);

                for (Iterator j = te.getTags(); j.hasNext();) {
                    sDocComment.append(' ').append(j.next());
		    sDocComment.append(LINE_SEPARATOR);
		    sDocComment.append(INDENT).append(" *");
                }
            } catch (IOException ioe) {
                // Nothing to be done, should not happen anyway ;-)
            }
        }

        sDocComment.append("/").append(LINE_SEPARATOR);

        return sDocComment.toString();
    }

    private String generateAssociationFrom(Object a, Object associationEnd) {
        // TODO: does not handle n-ary associations
        StringBuffer sb = new StringBuffer(80);

        Collection connections = Model.getFacade().getConnections(a);
        for (Object associationEnd2 : connections) {
            if (associationEnd2 != associationEnd) {
                sb.append(INDENT);
		sb.append(
		        generateConstraintEnrichedDocComment(a,
		                			     associationEnd2));
                sb.append(generateAssociationEnd(associationEnd2));
            }
        }

        return sb.toString();
    }

    private String generateAssociation(Object a) {
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

    private String generateAssociationEnd(Object ae) {
        if (!Model.getFacade().isNavigable(ae)) {
            return "";
        }
        if (Model.getFacade()
                .isAbstract(Model.getFacade().getAssociation(ae))) {
            return "";
        }
        //String s = INDENT + "protected ";
        // must be public or generate public navigation method!
        //String s = INDENT + "public ";
        StringBuffer sb = new StringBuffer(80);
        sb.append(INDENT).append(generateCoreAssociationEnd(ae));

        return (sb.append(";").append(LINE_SEPARATOR)).toString();
    }

    String generateCoreAssociationEnd(Object ae) {
        StringBuffer sb = new StringBuffer(80);
        sb.append(generateVisibility(Model.getFacade().getVisibility(ae)));

        if (Model.getScopeKind().getClassifier().equals(
                Model.getFacade().getTargetScope(ae))) {
            sb.append("static ");
        }
        //     String n = ae.getName();
        //     if (n != null && !String.UNSPEC.equals(n))
	//         s += generateName(n) + " ";
        //     if (ae.isNavigable()) s += "navigable ";
        //     if (ae.getIsOrdered()) s += "ordered ";
        if (Model.getFacade().getUpper(ae) == 1) {
            sb.append(generateClassifierRef(Model.getFacade().getType(ae)));
        } else {
            sb.append("Vector "); //generateMultiplicity(m) + " ";
        }

        sb.append(' ').append(generateAscEndName(ae));

        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////
    // internal methods?

    private String generateGeneralization(Collection generalizations) {
        if (generalizations == null) {
            return "";
        }
        Collection classes = new ArrayList();
        for (Object generalization : generalizations) {
            Object generalizableElement =
                Model.getFacade().getGeneral(generalization);
            // assert ge != null
            if (generalizableElement != null) {
                classes.add(generalizableElement);
            }
        }
        return generateClassList(classes);
    }

    //  private String generateSpecification(Collection realizations) {
    private String generateSpecification(Object cls) {
        Collection realizations =
            Model.getFacade().getSpecifications(cls);
        if (realizations == null) {
            return "";
        }
	LOG.debug("realizations: " + realizations.size());
        StringBuffer sb = new StringBuffer(80);
        Iterator clsEnum = realizations.iterator();
        while (clsEnum.hasNext()) {
            Object inter = clsEnum.next();
            sb.append(generateClassifierRef(inter));
            if (clsEnum.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String generateClassList(Collection classifiers) {
        if (classifiers == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer(80);
        Iterator clsEnum = classifiers.iterator();
        while (clsEnum.hasNext()) {
            sb.append(generateClassifierRef(clsEnum.next()));
            if (clsEnum.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /*
     * Returns a visibility String either for a VisibilityKind (according to
     * the definition in NotationProvider2), but also for a model element,
     * because if it is a Feature, then the tag 'src_visibility' is to be
     * taken into account for generating language dependent visibilities.
     */
    private String generateVisibility(Object o) {
	if (Model.getFacade().isAFeature(o)) {
            // TODO: The src_visibility tag doesn't appear to be created
            // anywhere by ArgoUML currently
	    Object tv = Model.getFacade().getTaggedValue(o, "src_visibility");
	    if (tv != null) {
		String tagged = (String) Model.getFacade().getValue(tv);
		if (tagged != null) {
		    if (tagged.trim().equals("")
			|| tagged.trim().toLowerCase().equals("package")
			|| tagged.trim().toLowerCase().equals("default")) {
			return "";
		    }
                    return tagged + " ";
		}
            }
        }
        if (Model.getFacade().isAModelElement(o)) {
            if (Model.getFacade().isPublic(o)) {
                return "public ";
            }
            if (Model.getFacade().isPrivate(o)) {
                return "private ";
            }
            if (Model.getFacade().isProtected(o)) {
                return "protected ";
            }
            if (Model.getFacade().isPackage(o)) {
                return "";
            }
        }
        if (Model.getFacade().isAVisibilityKind(o)) {
            if (Model.getVisibilityKind().getPublic().equals(o)) {
                return "public ";
            }
            if (Model.getVisibilityKind().getPrivate().equals(o)) {
                return "private ";
            }
            if (Model.getVisibilityKind().getProtected().equals(o)) {
                return "protected ";
            }
            if (Model.getVisibilityKind().getPackage().equals(o)) {
                return "";
            }
        }
        return "";
    }

    private String generateScope(Object f) {
        if (Model.getFacade().isStatic(f)) {
            return "static ";
        }
        return "";
    }

    /**
     * Generate "abstract" keyword for an abstract operation.
     */
    private String generateAbstractness(Object op) {
        if (Model.getFacade().isAbstract(op)) {
            return "abstract ";
        }
        return "";
    }

    /**
     * Generate "final" keyword for final operations.
     */
    private String generateChangeability(Object op) {
        if (Model.getFacade().isLeaf(op)) {
            return "final ";
        }
        return "";
    }

    private String generateChangability(Object sf) {
        if (Model.getFacade().isReadOnly(sf)) {
            return "final ";
        }
        return "";
    }

    /**
     * Generates "synchronized" keyword for guarded operations.
     * @param op The operation
     * @return String The synchronized keyword if the operation is guarded,
     *                else "".
     */
    private String generateConcurrency(Object op) {
        if (Model.getFacade().getConcurrency(op) != null
            && Model.getConcurrencyKind().getGuarded().equals(
                    Model.getFacade().getConcurrency(op))) {
            return "synchronized ";
        }
        return "";
    }

    /*
     * Generates a String representation of a Multiplicity.
     *
     * @param m the Multiplicity.
     * @return a human readable String.
     */
    private String generateMultiplicity(Object m) {
        if (m == null || "1".equals(Model.getFacade().toString(m))) {
            return "";
        } else {
            return Model.getFacade().toString(m);
        }
    }

    private String generateState(Object m) {
        return Model.getFacade().getName(m);
    }

    private String generateSubmachine(Object m) {
        Object c = Model.getFacade().getSubmachine(m);
        if (c == null) {
            return "include / ";
        }
        if (Model.getFacade().getName(c) == null) {
            return "include / ";
        }
        if (Model.getFacade().getName(c).length() == 0) {
            return "include / ";
        }
        return ("include / " + generateName(Model.getFacade().getName(c)));
    }

    private String generateObjectFlowState(Object m) {
        Object c = Model.getFacade().getType(m);
        if (c == null) {
            return "";
        }
        return Model.getFacade().getName(c);
    }

    /*
    private String generateStateBody(Object m) {
        LOG.info("GeneratorJava: generating state body");
        StringBuffer sb = new StringBuffer(80);
        Object entryAction = Model.getFacade().getEntry(m);
        Object exitAction = Model.getFacade().getExit(m);
        Object doAction = Model.getFacade().getDoActivity(m);

        if (entryAction != null) {
            String entryStr = generate(entryAction);
            if (entryStr.length() > 0) {
                sb.append("entry / ").append(entryStr);
            }
        }
        if (doAction != null) {
            String doStr = generate(doAction);
            if (doStr.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(LINE_SEPARATOR);
                }
                sb.append("do / ").append(doStr);
            }
        }
        if (exitAction != null) {
            String exitStr = generate(exitAction);
            if (sb.length() > 0) {
                sb.append(LINE_SEPARATOR);
            }
            if (exitStr.length() > 0) {
                sb.append("exit / ").append(exitStr);
            }
        }
        Collection trans = Model.getFacade().getInternalTransitions(m);
        if (trans != null) {
            Iterator iter = trans.iterator();
            while (iter.hasNext()) {
                if (sb.length() > 0) {
                    sb.append(LINE_SEPARATOR);
                }
                sb.append(generateTransition(iter.next()));
            }
        }

        /*   if (trans != null) {
	     int size = trans.size();
	     MTransition[] transarray = (MTransition[])trans.toArray();
	     for (int i = 0; i < size; i++) {
	     if (s.length() > 0) s += "\n";
	     s += Generate(transarray[i]);
	     }
	     }*
        return sb.toString();
    } */

    private String generateTransition(Object m) {
        StringBuffer sb =
            new StringBuffer(generateName(Model.getFacade().getName(m)));
        String t = generateEvent(Model.getFacade().getTrigger(m));
        String g = generateGuard(Model.getFacade().getGuard(m));
        String e = generateAction(Model.getFacade().getEffect(m));
        if (sb.length() > 0) {
            sb.append(": ");
        }
        sb.append(t);
        if (g.length() > 0) {
            sb.append(" [").append(g).append(']');
        }
        if (e.length() > 0) {
            sb.append(" / ").append(e);
        }
        return sb.toString();

        /*  String s = m.getName();
	    String t = generate(Model.getFacade().getTrigger(m));
	    String g = generate(Model.getFacade().getGuard(m));
	    String e = generate(Model.getFacade().getEffect(m));
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

    private String generateAction(Object m) {
        // return m.getName();

        if (m != null) {
            Object script = Model.getFacade().getScript(m);
            if ((script != null)
		    && (Model.getFacade().getBody(script) != null)) {
                return Model.getFacade().getBody(script).toString();
	    }
        }
        return "";
    }

    private String generateGuard(Object m) {
        //return generateExpression(Model.getFacade().getExpression(m));
        if (m != null && Model.getFacade().getExpression(m) != null) {
            return generateExpression(Model.getFacade().getExpression(m));
        }
        return "";
    }

    private String generateMessage(Object m) {
        if (m == null) {
            return "";
        }
        return generateName(Model.getFacade().getName(m)) + "::"
	    + generateAction(Model.getFacade().getAction(m));
    }

    /*
     * Generates the text for a (trigger) event.
     *
     * @author MVW
     * @param m Object of any MEvent kind
     * @return The generated event (as a String).
     */
    private String generateEvent(Object m) {
        if (Model.getFacade().isAChangeEvent(m)) {
            return "when("
                + generateExpression(Model.getFacade().getExpression(m))
                + ")";
        }
        if (Model.getFacade().isATimeEvent(m)) {
            return "after("
                + generateExpression(Model.getFacade().getExpression(m))
                + ")";
        }
        if (Model.getFacade().isASignalEvent(m)) {
            return generateName(Model.getFacade().getName(m));
        }
        if (Model.getFacade().isACallEvent(m)) {
            return generateName(Model.getFacade().getName(m));
        }
        return "";
    }

    String generateAscEndName(Object ae) {
        String n = Model.getFacade().getName(ae);
        Object asc = Model.getFacade().getAssociation(ae);
        String ascName = Model.getFacade().getName(asc);
        if (n != null && n != null && n.length() > 0) {
            n = generateName(n);
        } else if (
		   ascName != null && ascName != null && ascName.length() > 0) {
            n = generateName(ascName);
        } else {
            n = "my" + generateClassifierRef(Model.getFacade().getType(ae));
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
	    || !Model.getFacade().isANamespace(namespace)
	    || Model.getFacade().getNamespace(namespace) == null) {
            return "";
        }
        String packagePath = Model.getFacade().getName(namespace);
        if (packagePath == null) {
            return "";
        }
        while ((namespace = Model.getFacade().getNamespace(namespace))
                != null) {
            // ommit root package name; it's the model's root
            if (Model.getFacade().getNamespace(namespace) != null) {
                packagePath =
		    Model.getFacade().getName(namespace) + '.' + packagePath;
            }
        }
        return packagePath;
    }

    /**
     * Update a source code file.
     *
     * @param mClassifier The classifier to update from.
     * @param file The file to update.
     */
    private static void update(Object mClassifier, File file)
    	throws IOException, ANTLRException {

        LOG.info("Parsing " + file.getPath());
	String encoding = null;
        if (Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING) == null
	    || Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING)
	        .trim().equals("")) {
	    encoding = System.getProperty("file.encoding");
	} else {
	    encoding = Configuration.getString(Argo.KEY_INPUT_SOURCE_ENCODING);
	}
        FileInputStream in = new FileInputStream(file);
	JavaLexer lexer =
	    new JavaLexer(
	            new BufferedReader(new InputStreamReader(in, encoding)));
        JavaRecognizer parser = new JavaRecognizer(lexer);
        CodePieceCollector cpc = new CodePieceCollector();
        parser.compilationUnit(cpc);
        in.close();

        File origFile = new File(file.getAbsolutePath());
        File newFile = new File(file.getAbsolutePath() + ".updated");
        File backupFile = new File(file.getAbsolutePath() + ".backup");
        if (backupFile.exists()) {
            backupFile.delete();
        }
        //cat.info("Generating " + newFile.getPath());
        isInUpdateMode = true;
        cpc.filter(file, newFile, Model.getFacade().getNamespace(mClassifier));
        isInUpdateMode = false;
        //cat.info("Backing up " + file.getPath());
        file.renameTo(backupFile);
        LOG.info("Updating " + file.getPath());
        newFile.renameTo(origFile);
    }

    /*
     * @see org.argouml.moduleloader.ModuleInterface#getName()
     */
    public String getName() {
        return "GeneratorJava";
    }
    
    /*
     * @see org.argouml.moduleloader.ModuleInterface#getInfo(int)
     */
    public String getInfo(int type) {
        switch (type) {
        case DESCRIPTION:
            return "Java Notation and Code Generator";
        case AUTHOR:
            return "ArgoUML team";
        case VERSION:
            return ApplicationVersion.getVersion();
        default:
            return null;
        }
    }

    /*
     * No-op.  We get enabled at static initialization time.
     * 
     * @see org.argouml.moduleloader.ModuleInterface#enable()
     */
    public boolean enable() {
//        GeneratorManager.getInstance()
//                .addGenerator(myLang, new JavaGenerator());
        return true;
    }

    /*
     * Not supported.  Always returns false.
     * 
     * @see org.argouml.moduleloader.ModuleInterface#disable()
     */
    public boolean disable() {
//        GeneratorManager.getInstance().removeGenerator(myLang);
        return false;
    }

    /**
     * Returns the _lfBeforeCurly.
     * 
     * @return boolean
     */
    public boolean isLfBeforeCurly() {
        return lfBeforeCurly;
    }

    /**
     * Returns the _verboseDocs.
     * @return boolean
     */
    public boolean isVerboseDocs() {
        return verboseDocs;
    }

    /**
     * Sets the lfBeforeCurly.
     * @param beforeCurl The new value.
     */
    public void setLfBeforeCurly(boolean beforeCurl) {
        lfBeforeCurly = beforeCurl;
    }

    /**
     * Sets the verboseDocs.
     * @param verbose The new value.
     */
    public void setVerboseDocs(boolean verbose) {
        verboseDocs = verbose;
    }

    private String generateActionState(Object actionState) {
        String ret = "";
        Object action = Model.getFacade().getEntry(actionState);
        if (action != null) {
            Object expression = Model.getFacade().getScript(action);
            if (expression != null) {
                ret = generateExpression(expression);
            }
        }
        return ret;
    }

    private String generateExpression(Object expr) {
        if (Model.getFacade().isAExpression(expr))
            return generateUninterpreted(
                    (String) Model.getFacade().getBody(expr));
        else if (Model.getFacade().isAConstraint(expr))
            return generateExpression(Model.getFacade().getBody(expr));
        return "";
    }

    private String generateName(String n) {
        return n;
    }

    /**
     * Make a string non-null.<p>
     *
     * What is the purpose of this function? Shouldn't it be private static?
     *
     * @param un The String.
     * @return a non-null string.
     */
    private String generateUninterpreted(String un) {
        if (un == null)
            return "";
        return un;
    }

    private String generateClassifierRef(Object cls) {
        if (cls == null)
            return "";
        return Model.getFacade().getName(cls);
    }

    private String generateStereotype(Object st) {
        /*
         * TODO: This code is not used. Why is it here?
         * It causes an unwanted dependency 
         * to the org.argouml.kernel. (Project,...)
         * */
//        if (st == null)
//            return "";
//        Project project = 
//            ProjectManager.getManager().getCurrentProject();
//        ProjectSettings ps = project.getProjectSettings();
//        if (Model.getFacade().isAModelElement(st)) {
//            if (Model.getFacade().getName(st) == null)
//                return ""; // Patch by Jeremy Bennett
//            if (Model.getFacade().getName(st).length() == 0)
//                return "";
//            return ps.getLeftGuillemot()
//                + generateName(Model.getFacade().getName(st))
//                + ps.getRightGuillemot();
//        }
//        if (st instanceof Collection) {
//            Object o;
//            StringBuffer sb = new StringBuffer(10);
//            boolean first = true;
//            Iterator iter = ((Collection) st).iterator();
//            while (iter.hasNext()) {
//                if (!first)
//                    sb.append(',');
//                o = iter.next();
//                if (o != null) {
//                    sb.append(generateName(Model.getFacade().getName(o)));
//                    first = false;
//                }
//            }
//            if (!first) {
//                return ps.getLeftGuillemot()
//                    + sb.toString()
//                    + ps.getRightGuillemot();
//            }
//        }
        return "";
    }

    /*
     * @see org.argouml.uml.generator.CodeGenerator#generate(java.util.Collection, boolean)
     */
    public Collection generate(Collection elements, boolean deps) {
        LOG.debug("generate() called");
        File tmpdir = null;
        try {
            tmpdir = TempFileUtils.createTempDir();
            if (tmpdir != null) {
                generateFiles(elements, tmpdir.getPath(), deps);
                return TempFileUtils.readAllFiles(tmpdir);
            }
            return Collections.EMPTY_LIST;
        } finally {
            if (tmpdir != null) {
                TempFileUtils.deleteDir(tmpdir);
            }
            LOG.debug("generate() terminated");
        }
    }

    /*
     * @see org.argouml.uml.generator.CodeGenerator#generateFiles(java.util.Collection, java.lang.String, boolean)
     */
    public Collection generateFiles(Collection elements, String path,
            boolean deps) {
        LOG.debug("generateFiles() called");
        // TODO: 'deps' is ignored here
        for (Object element : elements) {
            generateFile(element, path);
        }
        return TempFileUtils.readFileNames(new File(path));
    }

    /*
     * @see org.argouml.uml.generator.CodeGenerator#generateFileList(java.util.Collection, boolean)
     */
    public Collection generateFileList(Collection elements, boolean deps) {
        LOG.debug("generateFileList() called");
        // TODO: 'deps' is ignored here
        File tmpdir = null;
        try {
            tmpdir = TempFileUtils.createTempDir();
            for (Object element : elements) {
                generateFile(element, tmpdir.getName());
            }
            return TempFileUtils.readFileNames(tmpdir);
        } finally {
            if (tmpdir != null) {
                TempFileUtils.deleteDir(tmpdir);
            }
        }
    }
}

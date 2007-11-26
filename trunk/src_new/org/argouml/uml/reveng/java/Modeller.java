// $Id$
// Copyright (c) 2003-2007 The Regents of the University of California. All
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

package org.argouml.uml.reveng.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;


import org.apache.log4j.Logger;
import org.argouml.application.api.Argo;
import org.argouml.kernel.ProjectManager;
import org.argouml.model.CoreFactory;
import org.argouml.model.Facade;
import org.argouml.model.Model;
import org.argouml.ocl.OCLUtil;
import org.argouml.uml.reveng.DiagramInterface;
import org.argouml.uml.reveng.ImportCommon;
import org.argouml.uml.reveng.ImportInterface;
import org.argouml.uml.reveng.ImportSettings;

/**
 * Modeller maps Java source code(parsed/recognised by ANTLR) to UML model
 * elements, it applies some of the semantics in JSR-26.  Note: JSR-26 was
 * withdrawn in March, 2004, so it obviously provides no guidance for
 * more recent language features such as Java 5.
 *
 * @author Marcus Andersson
 */
public class Modeller {

    private static final String JAVA_PACKAGE = "java.lang";
    
    /**
     * Logger.<p>
     */
    private static final Logger LOG = Logger.getLogger(Modeller.class);

    /**
     * Current working model.
     */
    private Object model;

    // TODO: This can be removed when we are sure there are no external users
    // of the functionality.  Diagram creation has moved to ImportCommon where
    // a single implementation is shared by all importers. - tfm 20061209
    private DiagramInterface diagram;

    /**
     * Current import settings.
     */
    private ImportCommon importSession;

    /**
     * The package which the currentClassifier belongs to.
     */
    private Object currentPackage;

    /**
     * Last package name used in addPackage().
     * It is null for classes which are not packaged.
     * Used in popClassifier() to create diagram for that
     * package.
     */
    private String currentPackageName;

    /**
     * Keeps the data that varies during parsing.
     */
    private ParseState parseState;

    /**
     * Stack up the state when descending inner classes.
     */
    private Stack<ParseState> parseStateStack;

    /**
     * Only attributes will be generated.
     */
    private boolean noAssociations;

    /**
     * Arrays will be modelled as unique datatypes.
     */
    private boolean arraysAsDatatype;

    /**
     * The name of the file being parsed.
     */
    private String fileName;

    /**
     * Arbitrary attributes.
     */
    private Hashtable<String, Object> attributes = 
        new Hashtable<String, Object>();

    /**
     * List of the names of parsed method calls.
     */
    private List<String> methodCalls = new ArrayList<String>();

    /**
     * HashMap of parsed local variables. Indexed by variable name with string
     * representation of the type stored as the value.
     */
    private Hashtable<String, String> localVariables = 
        new Hashtable<String, String>();

    /**
     * New model elements that were created during this
     * reverse engineering session.
     * TODO: We want a stronger type here, but ArgoUML treats all elements
     * as just simple Objects.
     */
    private Collection<Object> newElements;
    

    /**
     * Create a new modeller.
     *
     * @param theModel The model to work with.
     * @param settings the settings to use for this import
     * @param theFileName the current file name
     */
    public Modeller(Object theModel, ImportSettings settings, 
            String theFileName) {
        model = theModel;
        noAssociations = settings.isAttributeSelected();
        arraysAsDatatype = settings.isDatatypeSelected();
        currentPackage = this.model;
        newElements = new HashSet<Object>();
        parseState = new ParseState(this.model, getPackage(JAVA_PACKAGE));
        parseStateStack = new Stack<ParseState>();
        fileName = theFileName;
    }
    
    /**
     * @param key the key of the attribute to get
     * @return the value of the attribute
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * @param key the key of the attribute
     * @param value the value for the attribute
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * This is a mapping from a Java compilation Unit -> a UML component.
     * Classes are resident in a component.
     * Imports are relationships between components and other classes
     * / packages.<p>
     *
     * See JSR 26.<p>
     *
     * Adding components is a little messy since there are 2 cases:
     *
     * <ol>
     * <li>source file has package statement, will be added several times
     *     since lookup in addComponent() only looks in the model since the
     *     package namespace is not yet known.
     *
     * <li>source file has not package statement: component is added
     *     to the model namespace. the is no package statement so the
     *     lookup will always work.
     *
     * </ol>
     * Therefore in the case of (1), we need to delete duplicate components
     * in the addPackage() method.<p>
     *
     * In either case we need to add a package since we don't know in advance
     * if there will be a package statement.<p>
     */
    public void addComponent() {

        // try and find the component in the current package
        // to cope with repeated imports
        // [this will never work if a package statmeent exists:
        // because the package statement is parsed after the component is
        // identified]
        Object component = Model.getFacade().lookupIn(currentPackage, fileName);

        if (component == null) {

            // remove the java specific ending (per JSR 26).
            // BUT we can't do this because then the component will be confused
            // with its class with the same name when invoking
            // Model.getFacade().lookupIn(Object,String)
            /*
	      if(fileName.endsWith(".java"))
	      fileName = fileName.substring(0, fileName.length()-5);
            */

            component = Model.getCoreFactory().createComponent();
            Model.getCoreHelper().setName(component, fileName);
            newElements.add(component);
        }

        parseState.addComponent(component);

        // set the namespace of the component, in the event
        // that the source file does not have a package stmt
        Model.getCoreHelper().setNamespace(parseState.getComponent(), model);
    }

    /**
     * Called from the parser when a package clause is found.
     *
     * @param name The name of the package.
     */
    public void addPackage(String name) {
	// Add a package figure for this package to the owner's class
	// diagram, if it's not in the diagram yet. I do this for all
	// the class diagrams up to the top level, since I need
	// diagrams for all the packages.
	String ownerPackageName, currentName = name;
        ownerPackageName = getPackageName(currentName);
	while (!"".equals(ownerPackageName)) {
	    if (diagram != null
	            && importSession != null
	            && importSession.isCreateDiagramsSelected()
	            && diagram.isDiagramInProject(ownerPackageName)) {

	        diagram.selectClassDiagram(getPackage(ownerPackageName),
	                ownerPackageName);
	        diagram.addPackage(getPackage(currentName));

	    }
	    currentName = ownerPackageName;
            ownerPackageName = getPackageName(currentName);
	}
	// Save src_path in the upper package
        // TODO: Rework this so that we don't need importSession here.
        // perhaps move to the common import code. - tfm
	Object mPackage = getPackage(currentName);
	if (importSession != null && importSession.getSrcPath() != null
	    && Model.getFacade().getTaggedValue(mPackage,
                        ImportInterface.SOURCE_PATH_TAG) == null) {
            Model.getCoreHelper()
                    .setTaggedValue(mPackage, ImportInterface.SOURCE_PATH_TAG,
                            importSession.getSrcPath());
	}

	// Find or create a Package model element for this package.
	mPackage = getPackage(name);
        currentPackageName = name;

	// Set the current package for the following source code.
	currentPackage = mPackage;
	parseState.addPackageContext(mPackage);

        // Delay diagram creation until any classifier (class or
        // interface) will be found

        //set the namespace of the component
        // check to see if there is already a component defined:
        Object component = Model.getFacade().lookupIn(currentPackage, fileName);

        if (component == null) {

            // set the namespace of the component
            Model.getCoreHelper().setNamespace(
                    parseState.getComponent(),
                    currentPackage);
        } else {

            // a component already exists,
            // so delete the latest one(the duplicate)
            Object oldComponent = parseState.getComponent();
            Model.getUmlFactory().delete(oldComponent);
            newElements.remove(oldComponent);
        // change the parse state to the existing one.
            parseState.addComponent(component);
        }
    }

    /**
     * Called from the parser when an import clause is found.
     *
     * @param name The name of the import. Can end with a '*'.
     */
    public void addImport(String name) {
        addImport(name, false);
    }
    
    /**
     * Called from the parser when an import clause is found.
     *
     * @param name The name of the import. Can end with a '*'.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addImport(String name, boolean forceIt) {
        // only do imports on the 2nd pass.
        if (getLevel() == 0) {
            return;
        }

        String packageName = getPackageName(name);
        // TODO: In the case of an inner class, we probably want either the
        // qualified name with both outer and inner class names, or just the
        // outer class name
        String classifierName = getClassifierName(name);
        Object mPackage = getPackage(packageName);

        // import on demand
	if (classifierName.equals("*")) {
	    parseState.addPackageContext(mPackage);
	    Object srcFile = parseState.getComponent();
            buildImport(mPackage, srcFile);
	}
        // single type import
	else {
            Object mClassifier = null;
	    try {
		mClassifier =
		    (new PackageContext(null, mPackage)).get(classifierName);
            } catch (ClassifierNotFoundException e) {
                if (forceIt && classifierName != null && mPackage != null) {
                    // we must guess if it's a class or an interface, so: class
                    LOG.info("Modeller.java: " 
                            + "forced creation of unknown classifier "
                            + classifierName);
                    // TODO: A better strategy would be to defer creating this
                    // until we have enough information to determine what it is
                    mClassifier = Model.getCoreFactory().buildClass(
                            classifierName, mPackage);
                    newElements.add(mClassifier);
                } else {
                    warnClassifierNotFound(classifierName, e,
                            "an imported classifier");
                }
            }
            if (mClassifier != null) {
		parseState.addClassifierContext(mClassifier);
		Object srcFile = parseState.getComponent();
		buildImport(mClassifier, srcFile);
	    }
	}
    }


    /*
     * Build a Java import equivalent in UML. First search for an existing
     * permission. Create a new one if not found.
     */
    private Object buildImport(Object element, Object srcFile) {
        // TODO: This use of UML Permission is non-standard.
        // Change it to a Dependency with a <<javaImport>> stereotype
        // or something else - tfm - 20070802
        Collection dependencies = Model.getCoreHelper().getDependencies(
                element, srcFile);
        for (Object dependency : dependencies) {
            if (Model.getFacade().isAPermission(dependency)) {
                return dependency;
            }
        }
        
        // Didn't find it.  Let's create one.
        Object pkgImport = Model.getCoreFactory().buildPermission(srcFile,
                element);
        String newName = makePermissionName(srcFile, element);
        Model.getCoreHelper().setName(pkgImport, newName);
        newElements.add(pkgImport);
        return pkgImport;
    }

    private String makeAbstractionName(Object child, Object parent) {
        return makeFromToName(child, parent);
    }   
    
    private String makeAssociationName(Object from, Object to) {
        return makeFromToName(from, to);
    }
    
    private String makeGeneralizationName(Object child, Object parent) {
        return makeFromToName(child, parent);
    }    

    private String makePermissionName(Object from, Object to) {
        return makeFromToName(from, to);
    }
    
    private String makeFromToName(Object from, Object to) {
        return makeFromToName(
                Model.getFacade().getName(from), 
                Model.getFacade().getName(to));
    }
    
    private String makeFromToName(String from, String to) {
        // NOTE: This isn't localized, but I'm not sure it can be
        // without other side effects - tfm - 20070410
        return from + " -> " + to;
    }
    
    /**
     * Called from the parser when a class declaration is found.
     *
     * @param name The name of the class.
     * @param modifiers A sequence of class modifiers.
     * @param superclassName Zero or one string with the name of the
     *        superclass. Can be fully qualified or
     *        just a simple class name.
     * @param interfaces Zero or more strings with the names of implemented
     *        interfaces. Can be fully qualified or just a
     *        simple interface name.
     * @param javadoc The javadoc comment. null or "" if no comment available.
     */
    public void addClass(String name,
                         short modifiers,
                         String superclassName,
                         List<String> interfaces,
                         String javadoc) {
        addClass(name, modifiers, Collections.EMPTY_LIST, superclassName,
                interfaces, javadoc, false);
    }

    /**
     * Called from the parser when a class declaration is found.
     *
     * @param name The name of the class.
     * @param modifiers A sequence of class modifiers.
     * @param superclassName Zero or one string with the name of the
     *        superclass. Can be fully qualified or
     *        just a simple class name.
     * @param interfaces Zero or more strings with the names of implemented
     *        interfaces. Can be fully qualified or just a
     *        simple interface name.
     * @param javadoc The javadoc comment. null or "" if no comment available.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addClass(String name,
                         short modifiers,
                         List<String> typeParameters,
                         String superclassName,
                         List<String> interfaces,
                         String javadoc,
                         boolean forceIt) {
        if (typeParameters != null && typeParameters.size() > 0) {
            logError("type parameters not supported on Class", 
                    name);
        }
        Object mClass =
	    addClassifier(Model.getCoreFactory().createClass(),
			  name, modifiers, javadoc, typeParameters);

        Model.getCoreHelper().setAbstract(
                mClass,
                (modifiers & JavaRecognizer.ACC_ABSTRACT) > 0);
        Model.getCoreHelper().setLeaf(
                mClass,
                (modifiers & JavaRecognizer.ACC_FINAL) > 0);
        Model.getCoreHelper().setRoot(mClass, false);
        newElements.add(mClass);

        // only do generalizations and realizations on the 2nd pass.
        if (getLevel() == 0) {
            return;
        }

	if (superclassName != null) {
            Object parentClass = null;
	    try {
		parentClass =
		    getContext(superclassName)
		        .get(getClassifierName(superclassName));
		getGeneralization(currentPackage, parentClass, mClass);
	    } catch (ClassifierNotFoundException e) {
	        if (forceIt && superclassName != null && model != null) {
	            LOG.info("Modeller.java: forced creation of unknown class "
	                    + superclassName);
	            String packageName = getPackageName(superclassName);
	            String classifierName = getClassifierName(superclassName);
	            Object mPackage = (packageName.length() > 0) 
                            ? getPackage(packageName)
	                    : model;
	            parentClass = Model.getCoreFactory().buildClass(
	                    classifierName, mPackage);
                    newElements.add(parentClass);
	            getGeneralization(currentPackage, parentClass, mClass);
	        } else {
	            warnClassifierNotFound(superclassName, e,
	                    "a generalization");
	        }
	    }
	}

	if (interfaces != null) {
	    addInterfaces(mClass, interfaces, forceIt);
	}
    }

    /**
     * Called from the parser when an anonymous inner class is found.
     *
     * @param type The type of this anonymous class.
     */
    public void addAnonymousClass(String type) {
        addAnonymousClass(type, false);
    }
    
    /**
     * Called from the parser when an anonymous inner class is found.
     *
     * @param type The type of this anonymous class.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addAnonymousClass(String type, boolean forceIt) {
        String name = parseState.anonymousClass();
        try {
            Object mClassifier = getContext(type).get(getClassifierName(type));
            List<String> interfaces = new ArrayList<String>();
            if (Model.getFacade().isAInterface(mClassifier)) {
                interfaces.add(type);
            }
            addClass(name,
		     (short) 0,
                     Collections.EMPTY_LIST,
		     Model.getFacade().isAClass(mClassifier) ? type : null,
		     interfaces,
		     "",
                     forceIt);
        } catch (ClassifierNotFoundException e) {
            // Must add it anyway, or the class popping will mismatch.
            addClass(name, (short) 0, Collections.EMPTY_LIST, null,
                    Collections.EMPTY_LIST, "", forceIt);
            LOG.info("Modeller.java: an anonymous class was created "
		     + "although it could not be found in the classpath.");
        }
    }

    /**
     * Add an Interface to the model.
     * 
     * TODO: This method preserves the historical public API which is used by
     * other reverse engineering modules such as the Classfile module. This
     * really needs to be decoupled.
     * 
     * @param name
     *            The name of the interface.
     * @param modifiers
     *            A sequence of interface modifiers.
     * @param interfaces
     *            Zero or more strings with the names of extended interfaces.
     *            Can be fully qualified or just a simple interface name.
     * @param javadoc
     *            The javadoc comment. "" if no comment available.
     */
    public void addInterface(String name,
                             short modifiers,
                             List<String> interfaces,
                             String javadoc) {
        addInterface(name, modifiers, Collections.EMPTY_LIST, interfaces,
                javadoc, false);
    }
    
    /**
     * Called from the parser when an interface declaration is found.
     *
     * @param name The name of the interface.
     * @param modifiers A sequence of interface modifiers.
     * @param interfaces Zero or more strings with the names of extended
     * interfaces. Can be fully qualified or just a simple interface name.
     * @param javadoc The javadoc comment. "" if no comment available.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addInterface(String name,
                             short modifiers,
                             List<String> typeParameters,
                             List<String> interfaces,
                             String javadoc,
                             boolean forceIt) {
        if (typeParameters != null && typeParameters.size() > 0) {
            logError("type parameters not supported on Interface", 
                    name);
        }
        Object mInterface =
	    addClassifier(Model.getCoreFactory().createInterface(),
			  name,
			  modifiers,
			  javadoc,
                          typeParameters);

        // only do generalizations and realizations on the 2nd pass.
        if (getLevel() == 0) {
            return;
        }

        for (String interfaceName : interfaces) {
            Object parentInterface = null;
            try {
                parentInterface =
		    getContext(interfaceName)
		        .getInterface(getClassifierName(interfaceName));
                getGeneralization(currentPackage, parentInterface, mInterface);
            } catch (ClassifierNotFoundException e) {
                if (forceIt && interfaceName != null && model != null) {
                    LOG.info("Modeller.java: " 
                            + "forced creation of unknown interface "
                            + interfaceName);
                    String packageName = getPackageName(interfaceName);
                    String classifierName = getClassifierName(interfaceName);
                    Object mPackage = (packageName.length() > 0) 
                            ? getPackage(packageName)
                            : model;
                    parentInterface = Model.getCoreFactory().buildInterface(
                            classifierName, mPackage);
                    newElements.add(parentInterface);
                    getGeneralization(currentPackage, parentInterface,
                            mInterface);
                } else {
                    warnClassifierNotFound(interfaceName, e, 
                            "a generalization");
                }
            }
        }
    }

    /**
     * Called from the parser when an enumeration declaration is found.
     *
     * @param name The name of the class.
     * @param modifiers A sequence of class modifiers.
     * @param interfaces Zero or more strings with the names of implemented
     *        interfaces. Can be fully qualified or just a
     *        simple interface name.
     * @param javadoc The javadoc comment. null or "" if no comment available.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addEnumeration(String name,
                         short modifiers,
                         List<String> interfaces,
                         String javadoc,
                         boolean forceIt) {
        Object mClass =
            addClassifier(Model.getCoreFactory().createClass(),
                          name, modifiers, javadoc, 
                          Collections.EMPTY_LIST); // no type params for now
        
        Model.getCoreHelper().addStereotype(
                mClass,
                getStereotype("enumeration"));

        if ((modifiers & JavaRecognizer.ACC_ABSTRACT) > 0) {
            // abstract enums are illegal in Java
            logError("Illegal \"abstract\" modifier on enum ", name);
        } else {
            Model.getCoreHelper().setAbstract(mClass, false);
        }
        if ((modifiers & JavaRecognizer.ACC_FINAL) > 0) {
            // it's an error to explicitly use the 'final' keyword for an enum
            // declaration
            logError("Illegal \"final\" modifier on enum ", name);
        } else {
            // enums are implicitly final unless they contain a class body
            // (which we won't know until we process the constants
            Model.getCoreHelper().setLeaf(mClass, true);
        }
        Model.getCoreHelper().setRoot(mClass, false);

        // only do realizations on the 2nd pass.
        if (getLevel() == 0) {
            return;
        }

        if (interfaces != null) {
            addInterfaces(mClass, interfaces, forceIt);
        }
    }

    /**
     * @param mClass
     * @param interfaces
     * @param forceIt
     */
    private void addInterfaces(Object mClass, List<String> interfaces, 
            boolean forceIt) {
        for (String interfaceName : interfaces) {
            Object mInterface = null;
            try {
                mInterface =
                    getContext(interfaceName)
                        .getInterface(getClassifierName(interfaceName));
            } catch (ClassifierNotFoundException e) {
                if (forceIt && interfaceName != null && model != null) {
                    LOG.info("Modeller.java: " 
                            + "forced creation of unknown interface "
                            + interfaceName);
                    String packageName = getPackageName(interfaceName);
                    String classifierName =
                            getClassifierName(interfaceName);
                    Object mPackage = 
                        (packageName.length() > 0) 
                                    ? getPackage(packageName)
                                    : model;
                    mInterface =
                            Model.getCoreFactory().buildInterface(
                                    classifierName, mPackage);
                    newElements.add(mInterface);
                } else {
                    warnClassifierNotFound(interfaceName, e,
                            "an abstraction");
                }
            }
            // TODO: This should use the Model API's buildAbstraction - tfm
            if (mInterface != null) {
                Object mAbstraction =
                    getAbstraction(mInterface, mClass);
                if (Model.getFacade().getSuppliers(mAbstraction).size()
                        == 0) {
                    Model.getCoreHelper().addSupplier(
                            mAbstraction,
                            mInterface);
                    Model.getCoreHelper().addClient(mAbstraction, mClass);
                }
                Model.getCoreHelper().setNamespace(
                        mAbstraction,
                        currentPackage);
                Model.getCoreHelper().addStereotype(
                        mAbstraction,
                        getStereotype(CoreFactory.REALIZE_STEREOTYPE));
                newElements.add(mAbstraction);
            }
        }
    }

    /**
     * Called from the parser when an enumeration literal is found.
     *
     * @param name The name of the enumerationLiteral.
     */
    void addEnumerationLiteral(String name) {
        Object enumeration = parseState.getClassifier();
        if (!isAEnumeration(enumeration)) {
            throw new ParseStateException("not an Enumeration");
        }

        short mod = JavaRecognizer.ACC_PUBLIC | JavaRecognizer.ACC_FINAL
                | JavaRecognizer.ACC_STATIC;
        
        addAttribute(mod, null, name, null, null, true);
        
        // add an <<enum>> stereotype to distinguish it from fields
        // in the class  body?    
    }
    
    /*
     * Recognizer for enumeration.  In our world an enumeration
     * is a Class with the <<enumeration>> stereotype applied. 
     */
    private boolean isAEnumeration(Object element) {
        if (!Model.getFacade().isAClass(element)) {
            return false;
        }
        for (Object stereotype : Model.getFacade().getStereotypes(element)) {
            if ("enumeration".equals(Model.getFacade().getName(stereotype))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add an annotation declaration
     *
     * @param name identifier for annotation definition.
     * @param modifiers A sequence of modifiers.
     * @param javadoc The javadoc comment. null or "" if no comment available.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addAnnotationDefinition(String name,
                         short modifiers,
                         String javadoc,
                         boolean forceIt) {
        logError( "Java 5 annotation definitions not supported", "@" + name);
    }
    
    /**
     * Called from the parser when an annotation declaration is found.
     *
     * @param name identifier for annotation.
     */
    void addAnnotation(String name) {
        logError( "Java 5 annotations not supported", "@" + name);
    }

    /**
     * Done adding an annotation.
     */
    void endAnnotation() {
        // TODO: Placeholder.  Can we use popClassifier here?
    }
    
    void addTypeParameters() {
        // TODO: Not implemented
        logError("Unsupported Java 5 type parameters", "");
    }
    
    /**
       Common code used by addClass and addInterface.

       @param newClassifier Supply one if none is found in the model.
       @param name Name of the classifier.
       @param modifiers String of modifiers.
       @param javadoc The javadoc comment. null or "" if no comment available.
       @return The newly created/found classifier.
    */
    private Object addClassifier(Object newClassifier,
                                 String name,
                                 short modifiers,
                                 String javadoc, 
                                 List<String> typeParameters) {
        Object mClassifier;
        Object mNamespace;

        if (parseState.getClassifier() != null) {
            // the new classifier is a java inner class
            mClassifier =
        	Model.getFacade().lookupIn(parseState.getClassifier(), name);
            mNamespace = parseState.getClassifier();
        } else {
            // the new classifier is a top level java class
            parseState.outerClassifier();
            mClassifier = Model.getFacade().lookupIn(currentPackage, name);
            mNamespace = currentPackage;
        }


        if (mClassifier == null) {
            // if the classifier could not be found in the model
            if (LOG.isInfoEnabled()) {
                LOG.info("Created new classifier for " + name);
            }
            mClassifier = newClassifier;
            Model.getCoreHelper().setName(mClassifier, name);
            Model.getCoreHelper().setNamespace(mClassifier, mNamespace);
            newElements.add(mClassifier);
        } else {
            // it was found and we delete any existing tagged values.
            if (LOG.isInfoEnabled()) {
                LOG.info("Found existing classifier for " + name);
            }
            // TODO: Rewrite existing elements instead? - tfm
            cleanModelElement(mClassifier);
        }

        parseState.innerClassifier(mClassifier);

        // set up the component residency (only for top level classes)
        if (parseState.getClassifier() == null) {
            // set the classifier to be a resident in its component:
            // (before we push a new parse state on the stack)

            // This test is carried over from a previous implementation,
            // but I'm not sure why it would already be set - tfm
            if (Model.getFacade().getElementResidences(mClassifier).isEmpty()) {
                Object resident = Model.getCoreFactory()
                        .createElementResidence();
                Model.getCoreHelper().setResident(resident, mClassifier);
                Model.getCoreHelper().setContainer(resident,
                        parseState.getComponent());
            }
        }

        // change the parse state to a classifier parse state
        parseStateStack.push(parseState);
        parseState = new ParseState(parseState, mClassifier, currentPackage);

        setVisibility(mClassifier, modifiers);
        
        // Add classifier documentation tags during first (or only) pass only
        if (getLevel() <= 0) {
            addDocumentationTag(mClassifier, javadoc);
        }

        return mClassifier;
    }
    
    /**
     * Return the current import pass/level.
     * 
     * @return 0, 1, or 2 depending on current import level and pass of
     *         processing. Returns -1 if level isn't defined.
     */
    private int getLevel() {
        Object level = this.getAttribute("level");
        if (level != null) {
            return ((Integer) level).intValue();
        } 
        return -1;
    }

    /**
       Called from the parser when a classifier is completely parsed.
    */
    public void popClassifier() {
        // now create diagram if it doesn't exists in project
	if (diagram != null 
                && importSession != null 
                && importSession.isCreateDiagramsSelected()) {

	    if (currentPackageName != null) {
	        diagram.selectClassDiagram(currentPackage,
	                currentPackageName);
	    }
	    // The class is in a source file
	    // with no package declaration
	    else {
	        // create new diagram in root for classifier without package
	        diagram.createRootClassDiagram();
	    }
	    // add the current classifier to the diagram.
	    Object classifier = parseState.getClassifier();
	    if (Model.getFacade().isAInterface(classifier)) {
	        diagram.addInterface(classifier,
	                importSession.isMinimizeFigsSelected());
	    } else if (Model.getFacade().isAClass(classifier)) {
	        diagram.addClass(classifier,
	                importSession.isMinimizeFigsSelected());
	    }
	}

        // Remove operations and attributes not in source
        parseState.removeObsoleteFeatures();

        // Remove inner classes not in source
        parseState.removeObsoleteInnerClasses();

        parseState = parseStateStack.pop();
    }

    /**
     * Add an Operation to the current model
     * 
     * @param modifiers
     *            A sequence of operation modifiers.
     * @param returnType
     *            The return type of the operation.
     * @param name
     *            The name of the operation as a string
     * @param parameters
     *            A List of parameter declarations containing types and names.
     * @param javadoc
     *            The javadoc comment. null or "" if no comment available.
     * @return The operation.
     */
    public Object addOperation (short modifiers,
                                String returnType,
                                String name,
                                List<ParameterDeclaration> parameters,
                                String javadoc) {
        return addOperation(modifiers, Collections.EMPTY_LIST, returnType, name,
                parameters, javadoc, false);
    }
    
    /**
     * Called from the parser when an operation is found.
     * 
     * @param modifiers
     *            A sequence of operation modifiers.
     * @param returnType
     *            The return type of the operation.
     * @param name
     *            The name of the operation as a string
     * @param parameters
     *            A number of lists, each representing a parameter.
     * @param javadoc
     *            The javadoc comment. null or "" if no comment available.
     * @param forceIt
     *            Force addition by creating all that's missing.
     * @return The operation.
     */
    Object addOperation (short modifiers,
                                List<String> typeParameters,
                                String returnType,
                                String name,
                                List<ParameterDeclaration> parameters,
                                String javadoc,
                                boolean forceIt) {
        if (typeParameters != null && typeParameters.size() > 0) {
            logError("type parameters not supported on operation return type", 
                    name);
        }
	Object mOperation = getOperation(name);
	parseState.feature(mOperation);

	Model.getCoreHelper().setAbstract(mOperation,
				(modifiers & JavaRecognizer.ACC_ABSTRACT) > 0);
	Model.getCoreHelper().setLeaf(mOperation,
			    (modifiers & JavaRecognizer.ACC_FINAL) > 0);
	Model.getCoreHelper().setRoot(mOperation, false);
	setOwnerScope(mOperation, modifiers);
	setVisibility(mOperation, modifiers);
	if ((modifiers & JavaRecognizer.ACC_SYNCHRONIZED) > 0) {
	    Model.getCoreHelper().setConcurrency(mOperation,
	            Model.getConcurrencyKind().getGuarded());
	} else if (Model.getFacade().getConcurrency(mOperation)
		   == Model.getConcurrencyKind().getGuarded()) {
	    Model.getCoreHelper().setConcurrency(mOperation,
	            Model.getConcurrencyKind().getSequential());
	}

        Collection c = new ArrayList(Model.getFacade()
                .getParameters(mOperation));
    for (Object parameter : c) {
	    Model.getCoreHelper().removeParameter(mOperation, parameter);
	}

	Object mParameter;
	String typeName;
	Object mClassifier = null;

	if (returnType == null
            || ("void".equals(returnType)
                && name.equals(Model.getFacade().getName(parseState
                        .getClassifier())))) {
	    // Constructor
	    Model.getCoreHelper().addStereotype(mOperation,
                getStereotype(mOperation, "create", "BehavioralFeature"));
	} else {
	    try {
		mClassifier =
		    getContext(returnType).get(getClassifierName(returnType));
            } catch (ClassifierNotFoundException e) {
                if (forceIt && returnType != null && model != null) {
                    LOG.info("Modeller.java: " 
                            + "forced creation of unknown classifier "
                            + returnType);
                    String packageName = getPackageName(returnType);
                    String classifierName = getClassifierName(returnType);
                    Object mPackage =
                            (packageName.length() > 0) ? getPackage(packageName)
                                    : model;
                    mClassifier = Model.getCoreFactory().buildClass(
                            classifierName, mPackage);
                    newElements.add(mClassifier);
                } else {
                    warnClassifierNotFound(returnType, e,
                            "operation return type");
                }
            }
            if (mClassifier != null) {
		mParameter = buildReturnParameter(mOperation, mClassifier);
	    }
	}

	for (ParameterDeclaration parameter : parameters) {
	    typeName = parameter.getType();
            // TODO: A type name with a trailing "..." represents
            // a variable length parameter list.  It can only be
            // the last parameter and it gets converted to an array
            // on method invocation, so perhaps we should model it that
            // way (ie convert "Foo..." to "Foo[]"). - tfm - 20070329
            if (typeName.endsWith("...")) {
                logError("Unsupported variable length parameter list notation",
                        parameter.getName());
            }
            mClassifier = null;
	    try {
                mClassifier =
		    getContext(typeName).get(getClassifierName(typeName));
            } catch (ClassifierNotFoundException e) {
                if (forceIt && typeName != null && model != null) {
                    LOG.info("Modeller.java: " 
                            + "forced creation of unknown classifier "
                            + typeName);
                    String packageName = getPackageName(typeName);
                    String classifierName = getClassifierName(typeName);
                    Object mPackage =
                            (packageName.length() > 0) ? getPackage(packageName)
                                    : model;
                    mClassifier = Model.getCoreFactory().buildClass(
                            classifierName, mPackage);
                    newElements.add(mClassifier);
                } else {
                    warnClassifierNotFound(typeName, e,
                            "operation params");
                }
            }
            if (mClassifier != null) {
                mParameter = buildInParameter(mOperation, mClassifier,
                        parameter.getName());
                if (!Model.getFacade().isAClassifier(mClassifier)) {
                    // the type resolution failed to find a valid classifier.
                    logError("Modeller.java: a valid type for a parameter "
			     + "could not be resolved:\n "
			     + "In file: " + fileName + ", for operation: "
			     + Model.getFacade().getName(mOperation)
			     + ", for parameter: ",
			     Model.getFacade().getName(mParameter));
                }
	    }
	}

	addDocumentationTag (mOperation, javadoc);

	return mOperation;
    }

    private Object buildInParameter(Object operation, Object classifier,
            String name) {
        Object parameter = buildParameter(operation, classifier, name);
        Model.getCoreHelper().setKind(
                parameter, Model.getDirectionKind().getInParameter());
        return parameter;
    }

    private Object buildReturnParameter(Object operation, Object classifier) {
        Object parameter = buildParameter(operation, classifier, "return");
        Model.getCoreHelper().setKind(
                parameter, Model.getDirectionKind().getReturnParameter());
        return parameter;
    }

    private Object buildParameter(Object operation, Object classifier,
            String name) {
        Object parameter =
                Model.getCoreFactory().buildParameter(operation, classifier);
        Model.getCoreHelper().setName(parameter, name);
        return parameter;
    }


    /**
     * Warn user that information available in input source will not be
     * reflected accurately in the model.
     * 
     * @param name
     *            name of the classifiere which wasn't found
     * @param e
     *            the exception -
     * @param operation -
     *            a string indicating what type of operation was being attempted
     */
    private void warnClassifierNotFound(String name, Throwable e,
            String operation) {
        logError("Modeller.java: a classifier (" + name
                + ") that was in the source "
                + "file could not be generated in the model ", operation);
    }

    /**
     * Add an error message to the log to be shown to the user.
     * <p>
     * TODO: This currently just writes to the error log. It needs to return
     * errors some place that the user can see them and deal with them.  We
     * also need a way to get the line and column numbers to help the user
     * track the problem down.
     */
    private void logError(String message, String identifier) {
        LOG.warn(message + " : " + identifier);
    }
    
    /**
     * Called from the parser when a class field is parsed. This can occur in a
     * class block where it indicates a method body to to be added to an
     * operation (An operation will have exactly one Java body) OR it can occur
     * in the enum declaration (not currently supported).
     * 
     * TODO: Support use in an enum declaration
     * 
     * @param op
     *            An operation.
     * @param body
     *            A method body.
     */
    public void addBodyToOperation(Object op, String body) {
        if (op == null || !Model.getFacade().isAOperation(op)) {
            // This can occur if there's a class field in an enum definition
            throw new ParseStateException(
                    "Found class body in context other than a class");
        }
        if (body == null || body.length() == 0) {
            return;
        }

        Object method = getMethod(Model.getFacade().getName(op));
        parseState.feature(method);
        Model.getCoreHelper().setBody(method,
			    Model.getDataTypesFactory()
			    	.createProcedureExpression("Java",
			    	        		   body));
	// Add the method to it's specification.
        Model.getCoreHelper().addMethod(op, method);

        // Add this method as a feature to the classifier that owns
        // the operation.
        Model.getCoreHelper().addFeature(Model.getFacade().getOwner(op),
                method);
    }

    /**
     * Called from the parser when an attribute is found.
     *
     * @param modifiers A sequence of attribute modifiers.
     * @param typeSpec The attribute's type.
     * @param name The name of the attribute.
     * @param initializer The initial value of the attribute.
     * @param javadoc The javadoc comment. null or "" if no comment available.
     */
    public void addAttribute (short modifiers,
                              String typeSpec,
                              String name,
                              String initializer,
                              String javadoc) {
        addAttribute(modifiers, typeSpec, name, initializer, javadoc, false);
    }
    
    /**
     * Called from the parser when an attribute is found.
     *
     * @param modifiers A sequence of attribute modifiers.
     * @param typeSpec The attribute's type.
     * @param name The name of the attribute.
     * @param initializer The initial value of the attribute.
     * @param javadoc The javadoc comment. null or "" if no comment available.
     * @param forceIt Force addition by creating all that's missing.
     */
    void addAttribute (short modifiers,
                              String typeSpec,
                              String name,
                              String initializer,
                              String javadoc,
                              boolean forceIt) {
	String multiplicity = "1_1";
        Object mClassifier = null;
        
        if (typeSpec != null) {
            if (!arraysAsDatatype && typeSpec.indexOf('[') != -1) {
                typeSpec = typeSpec.substring(0, typeSpec.indexOf('['));
                multiplicity = "1_N";
            }

            // the attribute type
            try {
                // get the attribute type
                mClassifier =
                        getContext(typeSpec).get(getClassifierName(typeSpec));
            } catch (ClassifierNotFoundException e) {
                if (forceIt && typeSpec != null && model != null) {
                    LOG.info("Modeller.java: forced creation of"
                            + " unknown classifier " + typeSpec);
                    String packageName = getPackageName(typeSpec);
                    String classifierName = getClassifierName(typeSpec);
                    Object mPackage =
                            (packageName.length() > 0) ? getPackage(packageName)
                            : model;
                    mClassifier =
                            Model.getCoreFactory().buildClass(
                                    classifierName, mPackage);
                    newElements.add(mClassifier);
                } else {
                    warnClassifierNotFound(typeSpec, e, "an attribute");
                }
            }
            if (mClassifier == null) {
                logError("failed to find or create type", typeSpec);
                return;
            }
        }

	// if we want to create a UML attribute:
	if (mClassifier == null
                || noAssociations
                || Model.getFacade().isADataType(mClassifier)
                || (Model.getFacade().getNamespace(mClassifier) 
                        == getPackage(JAVA_PACKAGE))) {

            Object mAttribute = parseState.getAttribute(name);
            if (mAttribute == null) {
                mAttribute = buildAttribute(parseState.getClassifier(),
                        mClassifier, name);
            }
            parseState.feature(mAttribute);

            setOwnerScope(mAttribute, modifiers);
            setVisibility(mAttribute, modifiers);
            Model.getCoreHelper().setMultiplicity(mAttribute, multiplicity);

            if (Model.getFacade().isAClassifier(mClassifier)) {
                // TODO: This should already have been done in buildAttribute
                Model.getCoreHelper().setType(mAttribute, mClassifier);
            } else {
                // the type resolution failed to find a valid classifier.
                logError("Modeller.java: a valid type for a parameter "
			 + "could not be resolved:\n "
			 + "In file: " + fileName + ", for attribute: ",
			 Model.getFacade().getName(mAttribute));
            }

            // Set the initial value for the attribute.
            if (initializer != null) {

                // we must remove line endings and tabs from the intializer
                // strings, otherwise the classes will display horribly.
                initializer = initializer.replace('\n', ' ');
                initializer = initializer.replace('\t', ' ');

		Object newInitialValue =
		    Model.getDataTypesFactory()
		        .createExpression("Java",
					  initializer);
                Model.getCoreHelper().setInitialValue(
                        mAttribute,
                        newInitialValue);
            }

            if ((modifiers & JavaRecognizer.ACC_FINAL) > 0) {
                Model.getCoreHelper().setReadOnly(mAttribute, true);
            } else if (Model.getFacade().isReadOnly(mAttribute)) {
                Model.getCoreHelper().setReadOnly(mAttribute, true);
            }
            addDocumentationTag(mAttribute, javadoc);
        }
        // we want to create a UML association from the java attribute
        else {

            Object mAssociationEnd = getAssociationEnd(name, mClassifier);
            setTargetScope(mAssociationEnd, modifiers);
            setVisibility(mAssociationEnd, modifiers);
            Model.getCoreHelper().setMultiplicity(
                    mAssociationEnd,
                    multiplicity);
            Model.getCoreHelper().setType(mAssociationEnd, mClassifier);
            Model.getCoreHelper().setName(mAssociationEnd, name);
            if ((modifiers & JavaRecognizer.ACC_FINAL) > 0) {
                Model.getCoreHelper().setReadOnly(mAssociationEnd, true);
            }
            if (!mClassifier.equals(parseState.getClassifier())) {
                // Because if they are equal,
                // then getAssociationEnd(name, mClassifier) could return
                // the wrong assoc end, on the other hand the navigability
                // is already set correctly (at least in this case), so the
                // next line is not necessary. (maybe never necessary?) - thn
                Model.getCoreHelper().setNavigable(mAssociationEnd, true);
            }
            addDocumentationTag(mAssociationEnd, javadoc);
	}
    }

    /**
       Find a generalization in the model. If it does not exist, a
       new generalization is created.

       @param mPackage Look in this package.
       @param parent The superclass.
       @param child The subclass.
       @return The generalization found or created.
    */
    private Object getGeneralization(Object mPackage,
                                     Object parent,
                                     Object child) {
        Object mGeneralization = 
            Model.getFacade().getGeneralization(child, parent);
        if (mGeneralization == null) {
            mGeneralization =
                    Model.getCoreFactory().buildGeneralization(
                            child, parent,
                            makeGeneralizationName(child, parent));
            newElements.add(mGeneralization);
        }
        if (mGeneralization != null) {
            Model.getCoreHelper().setNamespace(mGeneralization, mPackage);
        }
        return mGeneralization;
    }

    /**
     * Find an abstraction<<realize>> in the model. If it does not
     * exist, a new abstraction is created.
     *
     * @param parent The superclass.
     * @param child The subclass.
     * @return The abstraction found or created.
     */
    private Object getAbstraction(Object parent,
                                  Object child) {
        Object mAbstraction = null;
        for (Iterator i =
                Model.getFacade().getClientDependencies(child).iterator();
	     i.hasNext();) {
            mAbstraction = i.next();
            Collection c = Model.getFacade().getSuppliers(mAbstraction);
            if (c == null || c.size() == 0) {
                Model.getCoreHelper()
                	.removeClientDependency(child, mAbstraction);
            } else {
                if (parent != c.toArray()[0]) {
                    mAbstraction = null;
                } else {
                    break;
                }
            }
        }

        if (mAbstraction == null) {
            mAbstraction = Model.getCoreFactory().buildAbstraction(
                   makeAbstractionName(child, parent),
                   parent,
                   child);
            newElements.add(mAbstraction);
        }
        return mAbstraction;
    }

    /**
       Find a package in the model. If it does not exist, a new
       package is created.

       @param name The name of the package.
       @return The package found or created.
    */
    private Object getPackage(String name) {
	Object mPackage = searchPackageInModel(name);
	if (mPackage == null) {
	    mPackage =
		Model.getModelManagementFactory()
		    .buildPackage(getRelativePackageName(name), name);
            newElements.add(mPackage);
            // TODO: This is redundant - tfm
	    Model.getCoreHelper().setNamespace(mPackage, model);

	    // Find the owner for this package.
	    if ("".equals(getPackageName(name))) {
		Model.getCoreHelper().addOwnedElement(model, mPackage);
	    } else {
		Model.getCoreHelper().addOwnedElement(
		        getPackage(getPackageName(name)),
		        mPackage);
	    }
	}
	return mPackage;
    }

    /**
     * Search recursively for nested packages in the model. So if you
     * pass a package org.argouml.kernel , this method searches for a package
     * kernel, that is owned by a package argouml, which is owned by a
     * package org. This method is required to nest the parsed packages.
     *
     * @param name The fully qualified package name of the package we
     * are searching for.
     * @return The found package or null, if it is not in the model.
     */
    private Object searchPackageInModel(String name) {
	if ("".equals(getPackageName(name))) {
	    return Model.getFacade().lookupIn(model, name);
	}
        Object owner = searchPackageInModel(getPackageName(name));
        return owner == null
            ? null
            : Model.getFacade().lookupIn(owner, getRelativePackageName(name));
    }

    /**
       Find an operation in the currentClassifier. If the operation is
       not found, a new is created.

       @param name The name of the operation.
       @return The operation found or created.
    */
    private Object getOperation(String name) {
        Object mOperation = parseState.getOperation(name);
        if (mOperation != null) {
            LOG.info("Getting the existing operation " + name);
        } else {
            LOG.info("Creating a new operation " + name);
            Object cls = parseState.getClassifier();
            Object returnType = ProjectManager.getManager()
                .getCurrentProject().getDefaultReturnType();
            mOperation = Model.getCoreFactory().buildOperation2(cls, returnType,
                    name);
            newElements.add(mOperation);
        }
        return mOperation;
    }

    /**
       Find an operation in the currentClassifier. If the operation is
       not found, a new is created.

       @param name The name of the method.
       @return The method found or created.
    */
    private Object getMethod(String name) {
        Object method = parseState.getMethod(name);
        if (method != null) {
            LOG.info("Getting the existing method " + name);
        } else {
            LOG.info("Creating a new method " + name);
            method = Model.getCoreFactory().buildMethod(name);
            newElements.add(method);
            Model.getCoreHelper().addFeature(
                    parseState.getClassifier(),
                    method);
        }
        return method;
    }

    /**
     * Build a new attribute in the current classifier.
     * 
     * @param classifier
     *            the model were are reverse engineering into
     * @param type
     *            the the type of the new attribute
     * @param name
     *            The name of the attribute.
     * @return The attribute found or created.
     */
    private Object buildAttribute(Object classifier, Object type, String name) {
        Object mAttribute = 
            Model.getCoreFactory().buildAttribute2(classifier, type);
        newElements.add(mAttribute);
        Model.getCoreHelper().setName(mAttribute, name);
        return mAttribute;
    }

    /**
     * Find an associationEnd for a binary Association from the 
     * currentClassifier to the type specified.
     * If not found, a new is created.
     * 
     * @param name
     *            The name of the attribute.
     * @param mClassifier
     *            Where the association ends.
     * @return The attribute found or created.
     */
    private Object getAssociationEnd(String name, Object mClassifier) {
        Object mAssociationEnd = null;
        for (Iterator i = Model.getFacade().getAssociationEnds(mClassifier)
                .iterator(); i.hasNext();) {
            Object ae = i.next();
            Object assoc = Model.getFacade().getAssociation(ae);
            if (name.equals(Model.getFacade().getName(ae))
                    && Model.getFacade().getConnections(assoc).size() == 2
                && Model.getFacade().getType(
                            Model.getFacade().getNextEnd(ae))
                    == parseState.getClassifier()) {
                mAssociationEnd = ae;
            }
        }
        if (mAssociationEnd == null && !noAssociations) {
            String newName =
                    makeAssociationName(parseState.getClassifier(), 
                            mClassifier);

            Object mAssociation = buildDirectedAssociation(
                        newName, parseState.getClassifier(), mClassifier);
            // this causes a problem when mClassifier is not only 
            // at one assoc end: (which one is the right one?)
            mAssociationEnd =
                Model.getFacade().getAssociationEnd(
                        mClassifier,
                        mAssociation);
        }
        return mAssociationEnd;
    }

    public static Object buildDirectedAssociation(
	    String name,
	    Object sourceClassifier, 
	    Object destClassifier) {
        return Model.getCoreFactory().buildAssociation(
                destClassifier, true, sourceClassifier, false, 
                name);
    }
    
    /**
       Get the stereotype with a specific name.

       @param name The name of the stereotype.
       @return The stereotype.
    */
    private Object getStereotype(String name) {
        LOG.debug("Trying to find a stereotype of name <<" + name + ">>");
        // Is this line really safe wouldn't it just return the first
        // model element of the same name whether or not it is a stereotype
        Object stereotype = Model.getFacade().lookupIn(model, name);

        if (stereotype == null) {
            LOG.debug("Couldn't find so creating it");
            return
                Model.getExtensionMechanismsFactory()
                    .buildStereotype(name, model);
        }

        if (!Model.getFacade().isAStereotype(stereotype)) {
            // and so this piece of code may create an existing stereotype
            // in error.
            LOG.debug("Found something that isn't a stereotype so creating it");
            return
                Model.getExtensionMechanismsFactory()
                    .buildStereotype(name, model);
        }

        LOG.debug("Found it");
        return stereotype;
    }

    /**
     * Find the first suitable stereotype with baseclass for a given object.
     *
     * @param me
     * @param name
     * @param baseClass
     * @return the stereotype if found
     *
     * @throws IllegalArgumentException if the desired stereotypes for
     * the modelelement and baseclass was not found and could not be created.
     * No stereotype is created.
     */
    private Object getStereotype(Object me, String name, String baseClass) {
        Collection models =
            ProjectManager.getManager().getCurrentProject().getModels();
        Collection stereos =
                Model.getExtensionMechanismsHelper().getAllPossibleStereotypes(
                        models, me);
        Object stereotype =  null;
        if (stereos != null && stereos.size() > 0) {
            Iterator iter = stereos.iterator();
            while (iter.hasNext()) {
                stereotype = iter.next();
                if (Model.getExtensionMechanismsHelper()
                        .isStereotypeInh(stereotype, name, baseClass)) {
                    LOG.info("Returning the existing stereotype of <<"
                            + Model.getFacade().getName(stereotype) + ">>");
                    return stereotype;
                }
            }
        }
        // Instead of failing, this should create any stereotypes that it
        // requires.  Most likely cause of failure is that the stereotype isn't
        // included in the profile that is being used. - tfm 20060224
        stereotype = getStereotype(name);
        if (stereotype != null) {
            Model.getExtensionMechanismsHelper().addBaseClass(stereotype, me);
            return stereotype;
        }
        // This should never happen then:
        throw new IllegalArgumentException("Could not find "
            + "a suitable stereotype for " + Model.getFacade().getName(me)
            + " -  stereotype: <<" + name
            + ">> base: " + baseClass);
    }

    /**
       Return the tagged value with a specific tag.

       @param element The tagged value belongs to this.
       @param name The tag.
       @return The found tag. A new is created if not found.
    */
    private Object getTaggedValue(Object element, String name) {
        Object tv = Model.getFacade().getTaggedValue(element, name);
        if (tv == null) {
            tv =
                    Model.getExtensionMechanismsFactory().buildTaggedValue(
                            name, "");
            Model.getExtensionMechanismsHelper().addTaggedValue(element, tv);
        }
        return tv;
    }

    /**
     * This classifier was earlier generated by reference but now it is
     * its time to be parsed so we clean out remnants.
     *
     * @param element that they are removed from
     */
    private void cleanModelElement(Object element) {
        Object tv =
                Model.getFacade().getTaggedValue(element, Facade.GENERATED_TAG);
        while (tv != null) {
            Model.getUmlFactory().delete(tv);
            tv =
                    Model.getFacade().getTaggedValue(
                            element, Facade.GENERATED_TAG);
        }
    }

    /**
       Get the package name from a fully specified classifier name.

       @param name A fully specified classifier name.
       @return The package name.
    */
    private String getPackageName(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        String pkgName = name.substring(0, lastDot);
        return pkgName;
        
        // TODO: Fix handling of inner classes along the lines of the following...
        
        // If the last element begins with an uppercase character, assume
        // that we've really got a class, not a package.  A better strategy
        // would be to defer until we can disambiguate, but this should be
        // better than what we have now for the more common case of inner
        // classes.
//        if (Character.isUpperCase(getRelativePackageName(pkgName).charAt(0))) {
//            return getPackageName(pkgName);
//        } else {
//            return pkgName;
//        }
    }

    /**
     * Get the relative package name from a fully qualified
     * package name. So if the parameter is 'org.argouml.kernel'
     * the method is supposed to return 'kernel' (the package
     * kernel is in package 'org.argouml').
     *
     * @param packageName A fully qualified package name.
     * @return The relative package name.
     */
    private String getRelativePackageName(String packageName) {
	// Since the relative package name corresponds
	// to the classifier name of a fully qualified
	// classifier, we simply use this method.
	return getClassifierName(packageName);
    }

    /**
       Get the classifier name from a fully specified classifier name.

       @param name A fully specified classifier name.
       @return The classifier name.
    */
    private String getClassifierName(String name) {
	int lastDot = name.lastIndexOf('.');
	if (lastDot == -1) {
	    return name;
	}
        return name.substring(lastDot + 1);
    }

    /**
       Set the visibility for a model element.

       @param element The model element.
       @param modifiers A sequence of modifiers which may contain
       'private', 'protected' or 'public'.
    */
    private void setVisibility(Object element,
                               short modifiers) {
	if ((modifiers & JavaRecognizer.ACC_PRIVATE) > 0) {
	    Model.getCoreHelper().setVisibility(
	            element,
	            Model.getVisibilityKind().getPrivate());
	} else if ((modifiers & JavaRecognizer.ACC_PROTECTED) > 0) {
	    Model.getCoreHelper().setVisibility(
	            element,
	            Model.getVisibilityKind().getProtected());
	} else if ((modifiers & JavaRecognizer.ACC_PUBLIC) > 0) {
	    Model.getCoreHelper().setVisibility(
	            element,
	            Model.getVisibilityKind().getPublic());
	} else {
            // Default Java visibility is "package"
            Model.getCoreHelper().setVisibility(
                    element,
                    Model.getVisibilityKind().getPackage());
	}
    }

    /**
       Set the owner scope for a feature.

       @param feature The feature.
       @param modifiers A sequence of modifiers which may contain
       'static'.
    */
    private void setOwnerScope(Object feature, short modifiers) {
        Model.getCoreHelper().setStatic(
                feature, (modifiers & JavaRecognizer.ACC_STATIC) > 0);
    }

    /**
       Set the target scope for an association end.

       @param mAssociationEnd The end.
       @param modifiers A sequence of modifiers which may contain
       'static'.
    */
    private void setTargetScope(Object mAssociationEnd, short modifiers) {
        // TODO: Review what this is trying to do and what it should
        // be replaced by.  Target Scope is gone in UML 2.x - tfm 20070529
        if ((modifiers & JavaRecognizer.ACC_STATIC) > 0) {
            Model.getCoreHelper().setTargetScope(
                    mAssociationEnd,
                    Model.getScopeKind().getClassifier());
        } else {
            Model.getCoreHelper().setTargetScope(
                    mAssociationEnd,
                    Model.getScopeKind().getInstance());
        }
    }

    /**
       Get the context for a classifier name that may or may not be
       fully qualified.

       @param name The classifier name.
    */
    private Context getContext(String name) {
	Context context = parseState.getContext();
	String packageName = getPackageName(name);
	if (!"".equals(packageName)) {
	    context = new PackageContext(context, getPackage(packageName));
	}
	return context;
    }


    /**
     * Add the contents of a single standard javadoc tag to the model element.
     * Usually this will be added as a tagged value.
     *
     * This is called from {@link #addDocumentationTag} only.
     *
     * @param me the model element to add to
     * @param sTagName the name of the javadoc tag
     * @param sTagData the contents of the javadoc tag
     */
    private void addJavadocTagContents(Object me,
				       String sTagName,
				       String sTagData) {
        int colonPos = (sTagData != null) ? sTagData.indexOf(':') : -1;
        if (colonPos != -1 && (("invariant".equals(sTagName))
	    || ("pre-condition".equals(sTagName))
	    || ("post-condition".equals(sTagName)))) {

	    // add as OCL constraint
	    String sContext = OCLUtil.getContextString(me);
	    String name = sTagData.substring(0, colonPos);
	    String body = null;
	    if (sTagName.equals ("invariant")) {
		// add as invariant constraint Note that no checking
		// of constraint syntax is performed... BAD!
		body = sContext + " inv " + sTagData;
	    } else if (sTagName.equals ("pre-condition")) {
		body = sContext + " pre " + sTagData;
	    } else {
		body = sContext + " post " + sTagData;
	    }
	    Object bexpr =
		Model.getDataTypesFactory()
		    .createBooleanExpression("OCL", body);
	    Object mc =
		Model.getCoreFactory().buildConstraint(name, bexpr);
	    Model.getCoreHelper().addConstraint(me, mc);
	    if (Model.getFacade().getNamespace(me) != null) {
		// Apparently namespace management is not supported
		// for all model elements. As this does not seem to
		// cause problems, I'll just leave it at that for the
		// moment...
		Model.getCoreHelper().addOwnedElement(
		        Model.getFacade().getNamespace(me),
		        mc);
	    }
        } else {
            if ("stereotype".equals(sTagName)) {
                // multiple stereotype support:
                // make one stereotype tag from many stereotype tags
                Object tv = getTaggedValue(me, sTagName);
                if (tv != null) {
                    String sStereotype = Model.getFacade().getValueOfTag(tv);
                    if (sStereotype != null && sStereotype.length() > 0) {
                        sTagData = sStereotype + ',' + sTagData;
                    }
                }
                // now eliminate multiple entries in that comma separated list
                HashSet<String> stSet = new HashSet<String>();
                StringTokenizer st = new StringTokenizer(sTagData, ", ");
                while (st.hasMoreTokens()) {
                    stSet.add(st.nextToken().trim());
                }
                StringBuffer sb = new StringBuffer();
                Iterator iter = stSet.iterator();
                while (iter.hasNext()) {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(iter.next());
                }
                sTagData = sb.toString();

            }
            
            Object tv = Model.getFacade().getTaggedValue(me, sTagName);
            if (tv == null) {
                Model.getExtensionMechanismsHelper().addTaggedValue(
                        me,
                        Model.getExtensionMechanismsFactory().buildTaggedValue(
                                sTagName, sTagData));
            } else {
                Model.getExtensionMechanismsHelper()
                        .setValueOfTag(tv, sTagData);
            }

	}
    }

    /**
     * Add the javadocs as a tagged value 'documentation' to the model
     * element. All comment delimiters are removed prior to adding the
     * comment.
     *
     * Added 2001-10-05 STEFFEN ZSCHALER.
     *
     * @param modelElement the model element to which to add the documentation
     * @param sJavaDocs the documentation comment to add ("" or null
     * if no java docs)
     */
    private void addDocumentationTag(Object modelElement, String sJavaDocs) {
	if ((sJavaDocs != null)
	    && (sJavaDocs.trim().length() >= 5)) {
	    StringBuffer sbPureDocs = new StringBuffer(80);
	    String sCurrentTagName = null;
	    String sCurrentTagData = null;
	    int nStartPos = 3; // skip the leading /**
	    boolean fHadAsterisk = true;

	    while (nStartPos < sJavaDocs.length()) {
		switch (sJavaDocs.charAt (nStartPos)) {
		case '*':
		    fHadAsterisk = true;
		    nStartPos++;
		    break;
		case ' ':   // all white space, hope I didn't miss any ;-)
		case '\t':
		    // ignore white space before the first asterisk
		    if (!fHadAsterisk) {
			nStartPos++;
			break;
		    }
		default:
		    // normal comment text or standard tag
		    // check ahead for tag
		    int j = nStartPos;
		    while ((j < sJavaDocs.length())
			   && ((sJavaDocs.charAt (j) == ' ')
			       || (sJavaDocs.charAt (j) == '\t'))) {
			j++;
		    }
		    if (j < sJavaDocs.length()) {
			if (sJavaDocs.charAt (j) == '@') {
			    // if the last javadoc is on the last line
			    // no new line will be found, causing an
			    // indexoutofboundexception.
			    int lineEndPos = 0;
			    if (sJavaDocs.indexOf('\n', j) < 0) {
				lineEndPos = sJavaDocs.length() - 2;
			    } else {
				lineEndPos = sJavaDocs.indexOf('\n', j) + 1;
			    }
			    sbPureDocs.append(sJavaDocs.substring(j,
								  lineEndPos));
			    // start standard tag potentially add
			    // current tag to set of tagged values...
			    if (sCurrentTagName != null) {
				addJavadocTagContents (modelElement,
						       sCurrentTagName,
						       sCurrentTagData);
			    }
			    // open new tag
			    int nTemp = sJavaDocs.indexOf (' ', j + 1);
			    if (nTemp == -1) {
				nTemp = sJavaDocs.length() - 1;
			    }
			    sCurrentTagName = sJavaDocs.substring(j + 1,
								  nTemp);
			    int nTemp1 = sJavaDocs.indexOf ('\n', ++nTemp);
			    if (nTemp1 == -1) {
				nTemp1 = sJavaDocs.length();
			    } else {
				nTemp1++;
			    }
			    sCurrentTagData =
				sJavaDocs.substring (nTemp, nTemp1);
			    nStartPos = nTemp1;
			} else {
			    // continue standard tag or comment text
			    int nTemp = sJavaDocs.indexOf ('\n', nStartPos);
			    if (nTemp == -1) {
				nTemp = sJavaDocs.length();
			    } else {
				nTemp++;
			    }
			    if (sCurrentTagName != null) {
				sbPureDocs.append(sJavaDocs.substring(nStartPos,
								      nTemp));
				sCurrentTagData +=
				    " "
				    + sJavaDocs.substring (nStartPos, nTemp);
			    } else {
				sbPureDocs.append(sJavaDocs.substring(nStartPos,
								      nTemp));
			    }
			    nStartPos = nTemp;
			}
		    }
		    fHadAsterisk = false;
		}
	    }
            sJavaDocs = sbPureDocs.toString();

            /*
             * After this, we have the documentation text, but there's still a
             * trailing '/' left, either at the end of the actual comment text
             * or at the end of the last tag.
             */
            sJavaDocs = removeTrailingSlash(sJavaDocs);

            // handle last tag, if any (strip trailing slash there too)
	    if (sCurrentTagName != null) {
		sCurrentTagData = removeTrailingSlash(sCurrentTagData);
		addJavadocTagContents (modelElement, sCurrentTagName,
				       sCurrentTagData);
	    }

	    // Now store documentation text in a tagged value
	    Model.getExtensionMechanismsHelper().addTaggedValue(
                    modelElement,
                    Model.getExtensionMechanismsFactory().buildTaggedValue(
                            Argo.DOCUMENTATION_TAG, sJavaDocs));
	    addStereotypes(modelElement);
        }
    }


    /*
     * Remove a trailing slash, including the entire line if it's the only thing
     * on the line.
     */
    private String removeTrailingSlash(String s) {
        if (s.endsWith("\n/")) {
            return s.substring(0, s.length() - 2);
        } else  if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        } else {
            return s;
        }
    }

    /*
     * If there is a tagged value named 'stereotype', make it a real
     * stereotype and remove the tagged value.
     * We allow multiple instances of this tagged value
     * AND parse a single instance for multiple stereotypes
     */
    private void addStereotypes(Object modelElement) {
        Object tv = Model.getFacade()
                .getTaggedValue(modelElement, "stereotype");
        if (tv != null) {
            String stereo = Model.getFacade().getValueOfTag(tv);
            if (stereo != null && stereo.length() > 0) {
                StringTokenizer st = new StringTokenizer(stereo, ", ");
                while (st.hasMoreTokens()) {
                    Model.getCoreHelper().addStereotype(modelElement,
                            getStereotype(st.nextToken().trim()));
                }
            }
            Model.getUmlFactory().delete(tv);
        }
    }

    /**
     * Manage collection of parsed method calls. Used for reverse engineering of
     * interactions.
     */
    /**
     * Add a parsed method call to the collection of method calls.
     * @param methodName
     *            The method name called.
     */
    public void addCall(String methodName) {
        methodCalls.add(methodName);
    }

    /**
     * Get collection of method calls.
     * @return list containing collected method calls
     */
    public synchronized List<String> getMethodCalls() {
        return methodCalls;
    }

    /**
     * Clear collected method calls.
     */
    public void clearMethodCalls() {
        methodCalls.clear();
    }

    /**
     * Add a local variable declaration to the list of variables.
     *
     * @param type type of declared variable
     * @param name name of declared variable
     */
    public void addLocalVariableDeclaration(String type, String name) {
        localVariables.put(name, type);
    }

    /**
     * Return the collected set of local variable declarations.
     *
     * @return hashtable containing all local variable declarations.
     */
    public Hashtable getLocalVariableDeclarations() {
        return localVariables;
    }

    /**
     * Clear the set of local variable declarations.
     */
    public void clearLocalVariableDeclarations() {
        localVariables.clear();
    }
    
    /**
     * Get the elements which were created while reverse engineering this file.
     * 
     * @return the collection of elements
     */
    public Collection getNewElements() {
        return newElements;
    }
}

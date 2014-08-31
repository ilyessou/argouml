/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2013 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2008 The Regents of the University of California. All
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

package org.argouml.cognitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Agency manages Critics.  Since classes are not really first class
 * objects in java, a singleton instance of Agency is made and passed
 * around as needed.  The Agency keeps a registry of all Critics that
 * should be applied to each type of design material. When a
 * design material instance is critiqued it asks Agency to apply all
 * registered Critic's.  In the current scheme there is a thread that
 * proactively, continuously critiques the Design at hand, even if
 * the user is idle! This is simple and it works.  The disadvantage
 * is that _all_ active critics related to a given design material are
 * applied, regardless of the reason for the critiquing and a lot of
 * CPU time is basically wasted.  <p>
 *
 * TODO: I am moving toward a more reactionary scheme in
 * which specific design manipulations in the editor cause critics
 * relevant to those manipulations to be applied.  This transition is
 * still half done.  Triggers are the critiquing requests.  The code
 * for triggers is currently dormant (latent?).<p>
 *
 * TODO: There is a strong dependency cycle between Agency and Designer.  They
 * either need to be merged into a single class or partitioned differently,
 * perhaps using an interface to break the cycle.  The Designer singleton gets
 * passed to almost every single part of the Critic subsystem, creating strong
 * coupling throughout. - tfm 20070620
 *
 * @author Jason Robbins
 */
public class Agency extends Observable { //implements java.io.Serialization
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(Agency.class.getName());


    /**
     * A registry of all critics that are currently loaded into the
     * design environment.
     */
    private static Hashtable<Class, List<Critic>> criticRegistry =
        new Hashtable<Class, List<Critic>>(100);

    private static List<Critic> critics = new ArrayList<Critic>();

    /**
     * The main control mechanism for determining which critics should
     * be active.
     */
    private ControlMech controlMech;

    private static Hashtable<String, Critic> singletonCritics =
        new Hashtable<String, Critic>(40);


    /**
     * Construct a new Agency instance with the given ControlMech as the
     * main control mechanism for determining which critics should be
     * active.
     *
     * @param cm the given controlMech
     */
    public Agency(ControlMech cm) {
        controlMech = cm;
    }

    /**
     * Construct a new Agency instance and use a StandardCM as the main
     * control mechanism for determining which critics should be
     * active.
     */
    public Agency() {
        controlMech = new StandardCM();
    }

    /**
     * Since Java does not really support classes as first class
     * objects, there is one instance of Agency that is passed around as
     * needed.<p>
     *
     * theAgency is actually stored in <code>Designer.theDesigner()</code>.
     *
     * @see Designer#theDesigner
     *
     * @return Agency the Agency instance
     */
    public static Agency theAgency() {
        Designer dsgr = Designer.theDesigner();
        if (dsgr == null) {
            return null;
	}
        return dsgr.getAgency();
    }

    /**
     * @return the registry.
     */
    private static Hashtable<Class, List<Critic>> getCriticRegistry() {
        return criticRegistry;
    }


    /**
     * @return the critics
     */
    public static List<Critic> getCriticList() {
        return critics;
    }

    /**
     * @param cr the critic to add/register
     */
    protected static void addCritic(Critic cr) {
        if (critics.contains(cr)) {
            return;
	}
        if (!(cr instanceof CompoundCritic)) {
            critics.add(cr);
	} else {
            for (Critic c : ((CompoundCritic) cr).getCriticList()) {
                addCritic(c);
            }
            return;
        }
    }

    /**
     * @param crClassName the critic class name
     * @param dmClassName the design material class name
     */
    public static void register(String crClassName, String dmClassName) {
        Class dmClass;
        try {
            dmClass = Class.forName(dmClassName);
        } catch (java.lang.ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "Error loading dm " + dmClassName, e);
            return;
        }
        Critic cr = singletonCritics.get(crClassName);
        if (cr == null) {
            Class crClass;
            try {
                crClass = Class.forName(crClassName);
            } catch (java.lang.ClassNotFoundException e) {
                LOG.log(Level.SEVERE,
                        "Error loading cr " + crClassName, e);
                return;
            }
            try {
                cr = (Critic) crClass.newInstance();
            } catch (java.lang.IllegalAccessException e) {
                LOG.log(Level.SEVERE,
                        "Error instancating cr " + crClassName, e);
                return;
            } catch (java.lang.InstantiationException e) {
                LOG.log(Level.SEVERE,
                        "Error instancating cr " + crClassName, e);
                return;
            }
            singletonCritics.put(crClassName, cr);
            addCritic(cr);
        }
        register(cr, dmClass);
    }

    /**
     * Register a critic in the global table of critics that have been
     * loaded. Critics are associated with one or more design material
     * classes. One way to do registration is in a static initializer of
     * the design material class. But additional (after-market) critics
     * could added through a menu command in some control panel...
     *
     * @param cr the critic to register
     * @param clazz the design material class that is to be criticized
     */
    public static void register(Critic cr, Class clazz) {
        List<Critic> theCritics = getCriticRegistry().get(clazz);
        if (theCritics == null) {
            theCritics = new ArrayList<Critic>();
            criticRegistry.put(clazz, theCritics);
        }
        if (!theCritics.contains(cr)) {
            theCritics.add(cr);
            notifyStaticObservers(cr);
            LOG.log(Level.FINE, "Registered: {0}", theCritics );
            cachedCritics.remove(clazz);
            addCritic(cr);
        }
    }

    /**
     * Transitional method for migration purposes.  Don't use!
     * @param cr the critic to register
     * @param clazz the UML class to be criticized
     */
    public static void register(Critic cr, Object clazz) {
        register(cr, (Class) clazz);
    }

    /**
     * Register a critic in the global table of critics that have been
     * loaded.
     *
     * @param cr the critic to register
     */
    public static void register(Critic cr) {
        Set<Object> metas = cr.getCriticizedDesignMaterials();
        for (Object meta : metas) {
            register(cr, meta);
        }
    }

    private static Hashtable<Class, Collection<Critic>> cachedCritics =
        new Hashtable<Class, Collection<Critic>>();

    /**
     * Return a collection of all critics that can be applied to the
     * design material subclass, including inherited critics.
     *
     * @param clazz the design material to criticize
     * @return the collection of critics
     */
    public static Collection<Critic> criticsForClass(Class clazz) {
        Collection<Critic> col = cachedCritics.get(clazz);
        if (col == null) {
            col = new ArrayList<Critic>();
	    col.addAll(criticListForSpecificClass(clazz));
	    Collection<Class> classes = new ArrayList<Class>();
	    if (clazz.getSuperclass() != null) {
		classes.add(clazz.getSuperclass());
	    }
	    if (clazz.getInterfaces() != null) {
		classes.addAll(Arrays.asList(clazz.getInterfaces()));
	    }
            for (Class c : classes) {
		col.addAll(criticsForClass(c));
	    }
	    cachedCritics.put(clazz, col);
        }
        return col;

    }


    /**
     * Return the List of all critics that are directly
     * associated with the given design material subclass.<p>
     *
     * If there aren't any an empty List is returned.
     *
     * @param clazz the design material
     * @return the critics
     */
    protected static List<Critic> criticListForSpecificClass(Class clazz) {
        List<Critic> theCritics = getCriticRegistry().get(clazz);
        if (theCritics == null) {
            theCritics = new ArrayList<Critic>();
            criticRegistry.put(clazz, theCritics);
        }
        return theCritics;
    }


    /**
     * Apply all critics that can be applied to the given
     * design material instance as appropriate for the given
     * Designer. <p>
     *
     * I would call this critique, but it causes a compilation error
     * because it conflicts with the instance method critique!
     *
     * @param dm the design material
     * @param d the designer
     * @param reasonCode the reason
     */
    public static void applyAllCritics(
        Object dm,
        Designer d,
        long reasonCode) {
        Class dmClazz = dm.getClass();
        Collection<Critic> c = criticsForClass(dmClazz);
        applyCritics(dm, d, c, reasonCode);
    }

    /**
     * @param dm the design material
     * @param d the designer
     */
    public static void applyAllCritics(Object dm, Designer d) {
        Class dmClazz = dm.getClass();
        Collection<Critic> c = criticsForClass(dmClazz);
        applyCritics(dm, d, c, -1L);
    }

    /**
     * @param dm the design material
     * @param d the designer
     * @param theCritics the critics
     * @param reasonCode the reason
     */
    public static void applyCritics(
        Object dm,
        Designer d,
        Collection<Critic> theCritics,
        long reasonCode) {

        for (Critic c : theCritics) {
            if (c.isActive() && c.matchReason(reasonCode)) {
                try {
                    c.critique(dm, d);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE,
                            "Disabling critique due to exception\n"
                            + c + "\n" + dm,
                            ex);
                    c.setEnabled(false);
                }
            }
        }
    }

    /**
     * Compute which critics should be active (i.e., they can be
     * applied by applyAllCritics) for a given Designer. <p>
     *
     * Note: I am setting global data, i.e. the
     * isEnabled bit in each critic, based on the needs of one designer.
     * I don't really support more than one Designer.
     * Which is why each designer
     * (if we would support more than one designer)
     * has his own Agency.
     *
     * TODO: should loop over simpler list of critics, not CompoundCritics
     *
     * @param d the designer
     */
    public void determineActiveCritics(Designer d) {
        for (Critic c : critics) {
            if (controlMech.isRelevant(c, d)) {
                c.beActive();
            } else {
                c.beInactive();
            }
        }
    }

    /**
     * Let some object receive notifications when the Agency changes
     * state.  Static observers are normal Observers on the singleton
     * instance of this class.
     *
     * @param obs the notified object
     */
    public static void addStaticObserver(Observer obs) {
        Agency a = theAgency();
        if (a == null) {
            return;
	}
        a.addObserver(obs);
    }

    /**
     * When the agency changes, notify observers.
     *
     * @param o the notified object
     */
    public static void notifyStaticObservers(Object o) {
        if (theAgency() != null) {
            theAgency().setChanged();
            theAgency().notifyObservers(o);
        }
    }

}

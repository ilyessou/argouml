// Copyright (c) 1996-2002 The Regents of the University of California. All
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

import java.lang.reflect.*;

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.foundation.core.MModelElement;
import org.argouml.model.uml.foundation.extensionmechanisms.ExtensionMechanismsFactory;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;

/**
 * Instances of this class is supposed to be attached to other instances
 * of other classes to uniquely identify them. It is intended that such
 * a tagging should be persistent over saving and loading, if applicable.
 *
 * <P>The class also harbors the
 * {@link #getIDOfObject getIDOfObject(Object, boolean)} which provides
 * a way to get the ItemUID of any object with a method 
 * <code>ItemUID getItemUID()</code>
 * and creating new ItemUIDs for any object with a method
 * <code>setItemUID(ItemUID)</code>
 * using reflection in java.
 *
 * @author Michael Stockman
 */
public class ItemUID
{
	/** Keeps a reference to the Class object of this class */
	protected final static Class _myclass = (new ItemUID()).getClass();

	/** This actual ID of this instance. */
	protected String _id;

	/**
	 * Constructs a new ItemUID and creates a new ID for it.
	 */
	public ItemUID()
	{
		_id = generateID();
	}

	/**
	 * Constructs a new ItemUID and uses the String param as the ID.
	 * Mainly intended to be used during loading of saved objects.
	 *
	 * @param	param	The ID to used for the new instance.
	 * @see		#toString()
	 */
	public ItemUID(String param)
	{
		_id = param;
	}

	/**
	 * Returns the ID of this ItemUID as a String. If everything works all
	 * such Strings will be unique. It is possible to created a new
	 * identical ItemUID using this String.
	 *
	 * @return	The ID as a String.
	 * @see		#ItemUID(String)
	 */
	public String toString()
	{
		return _id;
	}

	/**
	 * Generates a new unique ID and returns it as a String. The contents
	 * of the String is supposed to be unique with respect to all Strings
	 * generated by other instances of this class.
	 *
	 * @return	A String with unique content.
	 */
	public static String generateID()
	{
		return (new java.rmi.server.UID()).toString();
	}

	/**
	 * Obtains the ID of an object and returns it as a String. If
	 * canCreate is true it will try to create a new ID for the object
	 * if it has none.
	 *
	 * @return	The ID of the object, or null.
	 */
	public static String getIDOfObject(Object obj, boolean canCreate)
	{
		String s = readObjectID(obj);

		if (s == null && canCreate)
			s = createObjectID(obj);

		return s;
	}

	/**
	 * Tries to read the ID of the object. It uses the reflective
	 * properties of java to access a method named getItemUID of the
	 * object which is expected to return an ItemUID.
	 *
	 * @return	The ID of the object, or null.
	 */
	protected static String readObjectID(Object obj)
	{
		if (obj instanceof MBase)
			return ((MBase)obj).getUUID();
		/*
		// Want to use the "built in" UID of the MXxx instances
		// d00mst 2002-10-08
		if (obj instanceof MModelElement)
		{
			String id = ((MModelElement)obj).getTaggedValue("org.argouml.uid");
			//System.out.println("Read UID " + id + " from an object!");
			return id;
		}
		*/

		Object rv;
		try
		{
			Method m = obj.getClass().getMethod("getItemUID", null);
			rv = m.invoke(obj, null);
		}
		catch (NoSuchMethodException nsme)
		{
			return null;
		}
		catch (SecurityException se)
		{
			return null;
		}
		catch (InvocationTargetException tie)
		{
			System.out.println("getItemUID for " + obj.getClass() + " threw: " + tie);
			return null;
		}
		catch (IllegalAccessException iace)
		{
			return null;
		}
		catch (IllegalArgumentException iare)
		{
			System.out.println("getItemUID for " + obj.getClass() + " takes strange parameter: " + iare);
			return null;
		}
		catch (ExceptionInInitializerError eiie)
		{
			return null;
		}

		if (rv == null)
			return null;

		if (!(rv instanceof ItemUID))
		{
			System.out.println("getItemUID for " + obj.getClass() + " returns strange value: " + rv.getClass());
			return null;
		}

		return rv.toString();
	}

	/**
	 * Tries to create a new ID for the object. It uses the reflective 
	 * properties of java to access a method named setItemUID(ItemUID).
	 * If that method exist and doesn't throw when called, then the call
	 * is assumed to have been successful and the object is responsible
	 * for remembering the ID.
	 *
	 * @return	The new ID of the object, or null.
	 */
	protected static String createObjectID(Object obj)
	{
		if (obj instanceof MBase)
			return null;
		/*
		// Want to use the "built in" UID of the MXxx instances
		// d00mst 2002-10-08
		if (obj instanceof MModelElement)
		{
			MTaggedValue mtv = ExtensionMechanismsFactory.getFactory().createTaggedValue();;
			String id = generateID();
			mtv.setTag("org.argouml.uid");
			mtv.setValue(id);
			mtv.setModelElement((MModelElement)obj);
			//System.out.println("Added UID " + id + " to an object");
			return id;
		}
		*/

		Class params[] = new Class[1];
		Object mparam[];
		params[0] = _myclass;
		try
		{
			Method m = obj.getClass().getMethod("setItemUID", params);
			mparam = new Object[1];
			mparam[0] = new ItemUID();
			m.invoke(obj, mparam);
		}
		catch (NoSuchMethodException nsme)
		{
			return null;
		}
		catch (SecurityException se)
		{
			return null;
		}
		catch (InvocationTargetException tie)
		{
			System.out.println("setItemUID for " + obj.getClass() + " threw: " + tie);
			return null;
		}
		catch (IllegalAccessException iace)
		{
			return null;
		}
		catch (IllegalArgumentException iare)
		{
			System.out.println("setItemUID for " + obj.getClass() + " takes strange parameter: " + iare);
			return null;
		}
		catch (ExceptionInInitializerError eiie)
		{
			return null;
		}

		return mparam[0].toString();
	}
}


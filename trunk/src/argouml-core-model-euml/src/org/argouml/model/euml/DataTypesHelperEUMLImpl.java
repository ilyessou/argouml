// $Id$
// Copyright (c) 2007, The ArgoUML Project
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the ArgoUML Project nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE ArgoUML PROJECT ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE ArgoUML PROJECT BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.argouml.model.euml;

import org.argouml.model.DataTypesHelper;
import org.eclipse.uml2.uml.MultiplicityElement;


/**
 * The implementation of the DataTypesHelper for EUML2.
 */
class DataTypesHelperEUMLImpl implements DataTypesHelper {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;

    /**
     * Constructor.
     *
     * @param implementation The ModelImplementation.
     */
    public DataTypesHelperEUMLImpl(EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public void copyTaggedValues(Object from, Object to) {
        throw new NotYetImplementedException();
    }

    public boolean equalsCHOICEKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean equalsDeepHistoryKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean equalsFORKKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean equalsINITIALKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean equalsJOINKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean equalsJUNCTIONKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean equalsShallowHistoryKind(Object kind) {
        // TODO Auto-generated method stub
        return false;
    }

    public String getBody(Object handle) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLanguage(Object handle) {
        // TODO Auto-generated method stub
        return null;
    }

    public String multiplicityToString(Object multiplicity) {
        if (!(multiplicity instanceof MultiplicityElement)) {
            throw new IllegalArgumentException("multiplicity must be instance of MultiplicityElement"); //$NON-NLS-1$
        }
        MultiplicityElement mult = (MultiplicityElement) multiplicity;
        if (mult.getLower() == mult.getUpper()) {
            return DataTypesFactoryEUMLImpl.boundToString(mult.getLower());
        } else {
            return DataTypesFactoryEUMLImpl.boundToString(
                    mult.getLower())
                    + ".."
                    + DataTypesFactoryEUMLImpl.boundToString(mult.getUpper());
        }
    }
    
    public Object setBody(Object handle, String body) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object setLanguage(Object handle, String language) {
        // TODO Auto-generated method stub
        return null;
    }

}

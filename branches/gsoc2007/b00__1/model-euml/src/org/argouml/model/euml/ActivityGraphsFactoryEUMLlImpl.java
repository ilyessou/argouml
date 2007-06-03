// $Id:ActivityGraphsFactoryEUMLlImpl.java 12721 2007-05-30 18:14:55Z tfmorris $
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

import java.util.Collection;

import org.argouml.model.ActivityGraphsFactory;

/**
 * The implementation of the ActivityGraphsFactory for EUML2.
 */
class ActivityGraphsFactoryEUMLlImpl implements ActivityGraphsFactory {

    /**
     * The model implementation.
     */
    private EUMLModelImplementation modelImpl;

    /**
     * Constructor.
     *
     * @param implementation The ModelImplementation.
     */
    public ActivityGraphsFactoryEUMLlImpl(
            EUMLModelImplementation implementation) {
        modelImpl = implementation;
    }

    public Object buildActivityGraph(Object theContext) {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object buildClassifierInState(Object classifier, Collection state) {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object buildObjectFlowState(Object compositeState) {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createActionState() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createActivityGraph() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createCallState() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createClassifierInState() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createObjectFlowState() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createPartition() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

    public Object createSubactivityState() {
        // TODO: Auto-generated method stub
        throw new NotYetImplementedException();
    }

}

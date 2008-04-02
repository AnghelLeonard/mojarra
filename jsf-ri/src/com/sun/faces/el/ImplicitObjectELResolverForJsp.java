/*
 * $Id: ImplicitObjectELResolverForJsp.java,v 1.9 2006/03/29 22:38:33 rlubke Exp $
 */
/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * https://javaserverfaces.dev.java.net/CDDL.html or
 * legal/CDDLv1.0.txt. 
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.    
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.faces.el;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;

public class ImplicitObjectELResolverForJsp extends ImplicitObjectELResolver {

    // ------------------------------------------------------------ Constructors


    public ImplicitObjectELResolverForJsp() {
    }

    // ---------------------------------------------------------- Public Methods


    public Class getCommonPropertyType(ELContext context, Object base) {

        if (base != null) {
            return null;
        }
        return String.class;

    }


    public Iterator getFeatureDescriptors(ELContext context, Object base) {

        if (base != null) {
            return null;
        }

        ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(2);
        list.add(Util.getFeatureDescriptor("facesContext",
                                           "facesContext",
                                           "facesContext",
                                           false,
                                           false,
                                           true,
                                           FacesContext.class,
                                           Boolean.TRUE));
        list.add(Util.getFeatureDescriptor("view",
                                           "view",
                                           "root",
                                           false,
                                           false,
                                           true,
                                           UIViewRoot.class,
                                           Boolean.TRUE));
        return list.iterator();

    }


    public Class getType(ELContext context, Object base, Object property)
          throws ELException {

        if (base != null) {
            return null;
        }
        if (property == null) {
            String message = MessageUtils.getExceptionMessageString
                  (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID);
            message = message + " base " + base + " property " + property;
            throw new PropertyNotFoundException(message);
        }

        int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
        if (index < 0) {
            return null;
        }
        switch (index) {
            case FACES_CONTEXT:
            case VIEW:
                context.setPropertyResolved(true);
                return null;
            default:
                return null;
        }

    }


    public Object getValue(ELContext context, Object base, Object property)
          throws ELException {

        // variable resolution is a special case of property resolution
        // where the base is null.
        if (base != null) {
            return null;
        }
        if (property == null) {
            String message = MessageUtils.getExceptionMessageString
                  (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID);
            message = message + " base " + base + " property " + property;
            throw new PropertyNotFoundException(message);
        }

        FacesContext facesContext =
              (FacesContext) context.getContext(FacesContext.class);

        int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
        if (index < 0) {
            return null;
        }
        switch (index) {
            case FACES_CONTEXT:
                context.setPropertyResolved(true);
                return facesContext;
            case VIEW:
                context.setPropertyResolved(true);
                return facesContext.getViewRoot();
            default:
                return null;
        }

    }


    public boolean isReadOnly(ELContext context, Object base, Object property)
          throws ELException {

        if (base != null) {
            return false;
        }
        if (property == null) {
            String message = MessageUtils.getExceptionMessageString
                  (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID);
            message = message + " base " + base + " property " + property;
            throw new PropertyNotFoundException(message);
        }
        // return value will be ignored unless context.propertyResolved is
        // set to true.
        int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
        if (index < 0) {
            return false;
        }
        switch (index) {
            case FACES_CONTEXT:
                context.setPropertyResolved(true);
                return true;
            case VIEW:
                context.setPropertyResolved(true);
                return true;
            default:
                return false;
        }

    }


    public void setValue(ELContext context, Object base, Object property,
                         Object val) throws ELException {

        if (base != null) {
            return;
        }
        if (property == null) {
            String message = MessageUtils.getExceptionMessageString
                  (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID);
            message = message + " base " + base + " property " + property;
            throw new PropertyNotFoundException(message);
        }

        int index = Arrays.binarySearch(IMPLICIT_OBJECTS, property);
        if (index < 0) {
            return;
        }
        switch (index) {
            case FACES_CONTEXT:
                throw new PropertyNotWritableException(MessageUtils.getExceptionMessageString
                      (MessageUtils.OBJECT_IS_READONLY, "facesContext"));
            case VIEW:
                throw new PropertyNotWritableException(MessageUtils.getExceptionMessageString
                      (MessageUtils.OBJECT_IS_READONLY, "view"));
            default:
        }

    }

}

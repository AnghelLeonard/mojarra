/*
 * $Id: MockMethodBinding.java,v 1.1 2005/10/18 17:47:58 edburns Exp $
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

package com.sun.faces.mock;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;



/**
 * <p>Mock implementation of {@link MethodBinding} that supports a limited
 * subset of expression evaluation functionality:</p>
 * <ul>
 * <li>The portion of the method reference expression before the final
 *     "." must conform to the limitations of {@link MockValueBinding}.</li>
 * <li>The name of the method to be executed cannot be delimited by "[]".</li>
 * </ul>
 */

public class MockMethodBinding extends MethodBinding implements StateHolder {


    // ------------------------------------------------------------ Constructors

    public MockMethodBinding() {
    }


    public MockMethodBinding(Application application, String ref,
                             Class args[]) {

	this.application = application;
        this.args = args;
	if (ref.startsWith("#{") && ref.endsWith("}")) {
	    ref = ref.substring(2, ref.length() - 1);
	}
        this.ref = ref;
        int period = ref.lastIndexOf(".");
        if (period < 0) {
            throw new ReferenceSyntaxException(ref);
        }
        vb = application.createValueBinding(ref.substring(0, period));
        name = ref.substring(period + 1);
        if (name.length() < 1) {
            throw new ReferenceSyntaxException(ref);
        }

    }


    // ------------------------------------------------------ Instance Variables


    private Application application;
    private Class args[];
    private String name;
    private String ref;
    private ValueBinding vb;


    // --------------------------------------------------- MethodBinding Methods


    public Object invoke(FacesContext context, Object params[])
        throws EvaluationException, MethodNotFoundException {

        if (context == null) {
            throw new NullPointerException();
        }
        Object base = vb.getValue(context);
        Method method = method(base);
        try {
            return (method.invoke(base, params));
        } catch (IllegalAccessException e) {
            throw new EvaluationException(e);
        } catch (InvocationTargetException e) {
            throw new EvaluationException(e.getTargetException());
        }

    }


    public Class getType(FacesContext context) {

        Object base = vb.getValue(context);
        Method method = method(base);
        Class returnType = method.getReturnType();
        if ("void".equals(returnType.getName())) {
            return (null);
        } else {
            return (returnType);
        }

    }

    public String getExpressionString() {
	return "#{" + ref + "}";
    }

    // ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
	Object values[] = new Object[4];
	values[0] = name;
	values[1] = ref;
	values[2] = UIComponentBase.saveAttachedState(context, vb);
	values[3] = args;
	return (values);
    }


    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	name = (String) values[0];
	ref = (String) values[1];
	vb = (ValueBinding) UIComponentBase.restoreAttachedState(context, 
								 values[2]);
	args = (Class []) values[3];
    }


    private boolean transientFlag = false;


    public boolean isTransient() {
	return (this.transientFlag);
    }


    public void setTransient(boolean transientFlag) {
	this.transientFlag = transientFlag;
    }

    public boolean equals(Object otherObj) {
	MockMethodBinding other = null;

	if (!(otherObj instanceof MockMethodBinding)) {
	    return false;
	}
	other = (MockMethodBinding) otherObj;
	// test object reference equality
	if (this.ref != other.ref) {
	    // test object equality
	    if (null != this.ref && null != other.ref) {
		if (!this.ref.equals(other.ref)) {
		    return false;
		}
	    }
	    return false;
	}
	// no need to test name, since it flows from ref.
	// test our args array
	if (this.args != other.args) {
	    if (this.args.length != other.args.length) {
		return false;
	    }
	    for (int i = 0, len = this.args.length; i < len; i++) {
		if (this.args[i] != other.args[i]) {
		    if (!this.ref.equals(other.ref)) {
			return false;
		    }

		}
	    }
	}
	return true;
    }


    // --------------------------------------------------------- Private Methods


    // Package private so that unit tests can call this
    Method method(Object base) {

        Class clazz = base.getClass();
        try {
            return (clazz.getMethod(name, args));
        } catch (NoSuchMethodException e) {
            throw new MethodNotFoundException(ref + ": " + e.getMessage());
        }

    }



}

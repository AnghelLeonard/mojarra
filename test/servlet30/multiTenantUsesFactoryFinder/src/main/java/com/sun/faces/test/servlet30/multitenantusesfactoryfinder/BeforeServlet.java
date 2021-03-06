/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2014 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 * 
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 * 
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.

 */
package com.sun.faces.test.servlet30.multitenantusesfactoryfinder;

import java.io.IOException;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BeforeServlet extends HttpServlet {

    private static final long serialVersionUID = 2759083267171493482L;

    private static final String INIT_HAS_LIFECYCLE_KEY = "BeforeServlet_hasLifecycle";
    private static final String INIT_HAS_INITFACESCONTEXT_KEY = "BeforeServlet_hasInitFacesContext";

    private static final String REQUEST_HAS_LIFECYCLE = "BeforeServlet_requestHasLifecycle";
    private static final String REQUEST_HAS_FACESCONTEXT = "BeforeServlet_requestHasFacesContext";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        LifecycleFactory lifecycle = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        ServletContext sc = config.getServletContext();
        sc.setAttribute(INIT_HAS_LIFECYCLE_KEY,
                (null != lifecycle) ? "TRUE" : "FALSE");
        FacesContext initFacesContext = FacesContext.getCurrentInstance();
        sc.setAttribute(INIT_HAS_INITFACESCONTEXT_KEY,
                (null != initFacesContext) ? "TRUE" : "FALSE");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LifecycleFactory lifecycle = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        req.setAttribute(REQUEST_HAS_LIFECYCLE,
                (null != lifecycle) ? "TRUE" : "FALSE");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        req.setAttribute(REQUEST_HAS_FACESCONTEXT,
                (null != facesContext) ? "TRUE" : "FALSE");

        getServletContext().getRequestDispatcher("/faces/index.xhtml").forward(req, resp);
    }
}

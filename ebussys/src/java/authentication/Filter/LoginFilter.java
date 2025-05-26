package Authentication.Filters;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Authentication.Beans.AutenticationBean;
import jakarta.inject.Inject;

public class LoginFilter implements Filter {
    
    //Use the following for GlassFish 7.0.9  
    @Inject
    AutenticationBean session;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {  }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
       HttpServletRequest req = (HttpServletRequest) request;
       HttpServletResponse resp = (HttpServletResponse) response;
       //Use the following for GlassFish 6.2.5  
        //Retrieve the authtication bean
       //AutenticationBean session = (AutenticationBean) req.getSession(true).getAttribute("authBean");
       //Any after login accessible pages should be listed here
       String[] afterLog = {"logout.xhtml", "dashboard.xhtml"};
       String url=req.getRequestURI();
       if (session==null || !session.isLogged()) {
           boolean risk=false;
           for (int i=0; i<afterLog.length; i++) {
                if (url.indexOf(afterLog[i])>=0) 
                {risk=true;
                break;
                }
           }
           if (risk) {
               resp.sendRedirect(req.getServletContext().getContextPath()+"/login.xhtml");
           } 
           else {
               chain.doFilter(request, response);
           }
       } else {
           if (url.indexOf("register.xhtml")>=0 || url.indexOf("login.xhtml")>=0
                   || url.indexOf("emailVerification.xhtml")>=0 || url.indexOf("emailRecovery.xhtml")>=0
                   || url.indexOf("userRecovery.xhtml")>=0 ) {
               resp.sendRedirect(req.getServletContext().getContextPath()+"/dashboard.xhtml");
           }
           else if (url.indexOf("logout.xhtml")>=0) {
               //Use the following for GlassFish 6.2.5  
                //req.getSession().removeAttribute("authBean");
                //Use the following for GlassFish 7.0.9  
               req.getSession(false).invalidate();
               resp.sendRedirect(req.getServletContext().getContextPath()+"/login.xhtml");
           }
           else {
               chain.doFilter(request, response);
           }
       }
    }
    @Override
    public void destroy() {  }
    
}

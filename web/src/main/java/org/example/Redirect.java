package org.example;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Redirect {
    private String viewId;
    private Map<String, Object> parameters = new HashMap<>();

    public void execute()  {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(ec.getRequestContextPath() + viewId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    public void captureCurrentView() {
        parameters.clear();
        FacesContext context = FacesContext.getCurrentInstance();
        parameters.putAll( context.getExternalContext().getRequestParameterMap() );
        viewId = getViewId(context);
    }

    public static String getViewId(FacesContext facesContext)
    {
        if (facesContext!=null)
        {
            UIViewRoot viewRoot = facesContext.getViewRoot();
            if (viewRoot!=null) return viewRoot.getViewId();
        }
        return null;
    }
}

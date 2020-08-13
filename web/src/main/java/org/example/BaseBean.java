package org.example;

import javax.faces.context.FacesContext;
import java.io.Serializable;

public abstract class BaseBean implements Serializable {

    public String getSessionId () {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionId(true);
    }
}

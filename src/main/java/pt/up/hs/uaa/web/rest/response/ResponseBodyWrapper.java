package pt.up.hs.uaa.web.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseBodyWrapper {
    private Object object;
    private Class<?> view;

    public ResponseBodyWrapper(Object object, Class<?> view) {
        this.object = object;
        this.view = view;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @JsonIgnore
    public Class<?> getView() {
        return view;
    }

    @JsonIgnore
    public void setView(Class<?> view) {
        this.view = view;
    }
}

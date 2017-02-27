package za.co.knonchalant.elresolver;

import za.co.knonchalant.WindowScoped;
import za.co.knonchalant.exception.CouldNotCreateBeanException;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.Cookie;
import java.beans.FeatureDescriptor;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WindowBeanELResolver extends ELResolver {

    private Map<String, Object> beans = new HashMap<>();


    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (base != null || !(property instanceof String)) {
            return null;
        }

        String beanName = property + getWindowId();

        if (beans.containsKey(beanName)) {
            try {
                Object result = beans.get(beanName);
                context.setPropertyResolved(null, property);
                return result;
            } catch (Exception ex) {
                throw new ELException(ex);
            }
        } else {
            BeanManager beanManager = getBeanManager();
            Set<Bean<?>> beans1 = beanManager.getBeans(za.co.knonchalant.Bean.class, new AnnotationLiteral<Any>() {
            });
            for (Bean<?> bean : beans1) {
                Annotation annotation = bean.getBeanClass().getAnnotation(ManagedBean.class);
                Annotation windowScoped = bean.getBeanClass().getAnnotation(WindowScoped.class);
                if (windowScoped != null && annotation != null) {
                    ManagedBean managedAnnotation = (ManagedBean) annotation;
                    if (managedAnnotation.name().equals(property) || ("".equals(managedAnnotation.name()) && getBeanName(bean.getBeanClass()).equals(property))) {
                        Object actualBean = instantiate(bean.getBeanClass());
                        beans.put(beanName, actualBean);
                        context.setPropertyResolved(null, property);
                        return actualBean;
                    }
                }
            }
        }

        return null;
    }

    private Object instantiate(Class<?> beanClass) {
        try {
            return beanClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CouldNotCreateBeanException("Could not create " + beanClass.getCanonicalName());
        }
    }

    private String getBeanName(Class<?> beanClass) {
        String simpleName = beanClass.getSimpleName();
        return simpleName.substring(0,1).toLowerCase() + simpleName.substring(1);
    }

    private BeanManager getBeanManager() {
        try {
            InitialContext context = new InitialContext();
            return (BeanManager) context.lookup("java:comp/BeanManager");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getWindowId() {
        Cookie windowId = (Cookie) FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap().get("windowId");
        if (windowId == null) {
            return "";
        }
        return windowId.getValue();
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null || !(property instanceof String)) {
            return null;
        }

        String beanName = property + getWindowId();

        try {
            if (beans.containsKey(beanName)) {
                Class<?> result = beans.get(beanName).getClass();
                context.setPropertyResolved(null, property);
                return result;
            }
        } catch (Exception ex) {
            throw new ELException(ex);
        }

        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property,
                         Object value) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null || !(property instanceof String)) {
            return;
        }

        String beanName = property + getWindowId();

        boolean isResolved = context.isPropertyResolved();

        boolean isReadOnly;
        try {
            isReadOnly = isReadOnly(context, null, property);
        } catch (Exception e) {
            throw new ELException(e);
        } finally {
            context.setPropertyResolved(isResolved);
        }

        if (isReadOnly) {
            throw new PropertyNotWritableException(beanName);
        }

        try {
            beans.put(beanName, value);
            context.setPropertyResolved(null, property);
        } catch (Exception ex) {
            throw new ELException(ex);
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        // Standard EL context returns false
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object o) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext elContext, Object o) {
        return String.class;
    }
}

package za.co.knonchalant;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean
public class WindowAction {
    @ManagedProperty("#{windowBean}")
    private WindowBean windowBean;

    public void test() {
        windowBean.setCounter(windowBean.getCounter()+1);
    }

    public WindowBean getWindowBean() {
        return windowBean;
    }

    public void setWindowBean(WindowBean windowBean) {
        this.windowBean = windowBean;
    }
}

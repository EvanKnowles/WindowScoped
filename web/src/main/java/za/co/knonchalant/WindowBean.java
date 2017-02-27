package za.co.knonchalant;

import javax.faces.bean.ManagedBean;

@ManagedBean
@WindowScoped
public class WindowBean extends Bean {
    private int counter;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}

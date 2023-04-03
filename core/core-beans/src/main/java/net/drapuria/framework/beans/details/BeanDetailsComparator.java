package net.drapuria.framework.beans.details;

import java.util.Comparator;

public class BeanDetailsComparator implements Comparator<BeanDetails> {
    @Override
    public int compare(BeanDetails o1, BeanDetails o2) {
        if (o1 instanceof ServiceBeanDetails && !(o2 instanceof ServiceBeanDetails)) {
            return 1;
        } else if (!(o1 instanceof ServiceBeanDetails) && o2 instanceof ServiceBeanDetails) {
            return -1;
        }
        return 0;
    }
}

package box.bookstorebe.util;

import box.bookstorebe.exception.ResourceNotFoundException;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class BeanUtils implements ApplicationContextAware {
    @Getter
    private static ApplicationContext context;

    public static void setContext(ApplicationContext context) {
        BeanUtils.context = context;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        setContext(context);
    }

    public static <T> T getBean(Class<T> beanClass) throws ResourceNotFoundException {
        if (getContext() == null)
            throw new ResourceNotFoundException(BeanUtils.class.getName() + " - " + beanClass.getName());

        return (T) getContext().getBean(beanClass);
    }

    public static <T> T getBean(Class<T> beanClass, T clazz) throws ResourceNotFoundException {
        if (clazz != null)
            return clazz;
        log.info("getBean by class: [{}]", beanClass.getCanonicalName());
        return getBean(beanClass);
    }

    public static <T> T getBean(String beanClass) throws ResourceNotFoundException {
        if (getContext() == null)
            throw new ResourceNotFoundException(BeanUtils.class.getName() + " - " + beanClass);
        return (T) getContext().getBean(beanClass);
    }

    public static <T> T getBean(String beanClass, T clazz) throws ResourceNotFoundException {
        if (clazz != null)
            return clazz;
        log.info("getBean by name: [{}]", beanClass);
        return getBean(beanClass);
    }

    public static void copyProperties(Object source, Object target) throws BeansException {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }
}

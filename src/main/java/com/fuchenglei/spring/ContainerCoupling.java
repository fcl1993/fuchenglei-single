package com.fuchenglei.spring;

import com.fuchenglei.core.container.Container;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 耦合spring容器
 *
 * @author 付成垒
 */
public class ContainerCoupling implements BeanFactoryPostProcessor
{

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException
    {
        Container.startSpring(configurableListableBeanFactory);
    }

}

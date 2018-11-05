package com.fuchenglei.db.core;

import com.fuchenglei.core.process.PostProxy;
import com.fuchenglei.db.annotation.Transaction;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 * ServicePlugin 生成器
 *
 * @author 付成垒
 */
final class ServiceProxy<T> extends PostProxy<T> implements MethodInterceptor
{

    private T object;

    public T createProxy(T object)
    {
        this.object = object;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.object.getClass());
        CallbackFilter callbackFilter = new TransactionCall();
        enhancer.setCallbacks(new Callback[]{this, NoOp.INSTANCE});
        enhancer.setCallbackFilter(callbackFilter);
        Object o = enhancer.create();
        super.process(o, object);
        return (T) o;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable
    {
        Object obj = null;
        try
        {
            AbstractDBQuery.connectStart(this.object.getClass().getName() + "." + method.getName(), method.isAnnotationPresent(Transaction.class));
            obj = methodProxy.invoke(this.object, objects);
        }
        catch (Exception e)
        {
            AbstractDBQuery.rollback();
            e.printStackTrace();
        }
        finally
        {
            AbstractDBQuery.close(this.object.getClass().getName() + "." + method.getName());
        }
        return obj;
    }

}

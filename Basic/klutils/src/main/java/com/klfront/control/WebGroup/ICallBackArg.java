package com.klfront.control.WebGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lujinlong on 2016/5/30.
 */


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ICallBackArg
{

}

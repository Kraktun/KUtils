package com.kraktun.jutils.exceptions;

public class ExceptionWrapper {

    public static<T, S> T nullOnException(ExceptionWrapperInterface<T, S> i, S s) {
        try {
            return i.execute(s);
        } catch (Exception e) {
            return null;
        }
    }

    public interface ExceptionWrapperInterface<P, Q> {
        P execute(Q q);
    }
	
	// usage example
	//Utils.ExceptionWrapperInterface<Object, Long> i = (a) -> longToObject(a);
    //return ExceptionWrapper.nullOnException(i, id);
}

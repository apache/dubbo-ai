package org.apache.dubbo.ai.core.stream;

import org.apache.dubbo.common.stream.StreamObserver;

import java.io.Serializable;

/**
 * @author liuzhifei
 * @since 1.0
 */
public class AiStreamObserver<T> implements StreamObserver<T>, Serializable {


    @Override
    public void onNext(T data) {
        System.out.println("get AI data:" + data);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}

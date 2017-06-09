package moe.xing.rxfilepicker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.File;

import moe.xing.baseutils.Init;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by hehanbo on 16-9-29.
 * <p>
 * 获取图片
 */

@SuppressWarnings("WeakerAccess")
public class RxGetFile {


    private static RxGetFile sSingleton;
    private SparseArray<Subscriber<? super File>> mSubscribers = new SparseArray<>();

    public RxGetFile() {

    }

    /**
     * 获取单例
     */
    public static RxGetFile getInstance() {
        if (sSingleton == null) {
            synchronized (RxGetFile.class) {
                if (sSingleton == null) {
                    sSingleton = new RxGetFile();
                }
            }
        }
        return sSingleton;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * 设置返回的图片
     *
     * @param file 返回的图片 可能为空(用户放弃)
     */
    void onAns(@Nullable File file, int subscriberID) {
        Subscriber<? super File> subscriber = mSubscribers.get(subscriberID);
        if (subscriber != null && file != null) {
            subscriber.onNext(file);
        }
    }

    /**
     * 设置返回错误
     *
     * @param message 错误信息
     */
    void onError(Throwable message, int subscriberID) {
        Subscriber<? super File> subscriber = mSubscribers.get(subscriberID);
        if (subscriber != null) {
            subscriber.onError(message);
        }
    }

    void onComplete(int subscriberID) {
        Subscriber<? super File> subscriber = mSubscribers.get(subscriberID);
        if (subscriber != null) {
            subscriber.onCompleted();
        }
    }

    private Observable<File> fromBuild(final Builder builder) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                synchronized (RxGetFile.class) {
                    //get Subscribers ID
                    int i = 1;
                    while (mSubscribers.get(i) != null) {
                        i++;
                    }
                    //add intent
                    Intent intent = GetFileActivity.getStartIntent(Init.getApplication(), i,
                            builder.isSingle, builder.maxCount);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Init.getApplication().startActivity(intent);
                    mSubscribers.append(i, subscriber);
                }
            }
        });
    }

    public static final class Builder {

        private boolean isSingle;
        private int maxCount;

        public Builder() {
            isSingle = true;
            maxCount = Integer.MAX_VALUE;
        }

        @NonNull
        public Builder isSingle(boolean val) {
            isSingle = val;
            return this;
        }

        @NonNull
        public Builder maxCount(int val) {
            maxCount = val;
            return this;
        }

        @NonNull
        public Observable<File> build() {
            return RxGetFile.getInstance().fromBuild(this);
        }
    }


}

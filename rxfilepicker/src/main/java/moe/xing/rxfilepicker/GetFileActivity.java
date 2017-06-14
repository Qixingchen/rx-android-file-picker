package moe.xing.rxfilepicker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import moe.xing.baseutils.Init;
import moe.xing.baseutils.utils.IntentUtils;
import moe.xing.rx_utils.RxFileUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Qi Xingchen on 17-6-9.
 */

public class GetFileActivity extends Activity {

    private static final int GET_FILE_REQUEST_CODE = 0x3453;
    private static final String SUBSCRIBER_ID = "SubscriberID";
    private static final String IS_SINGLE = "IS_SINGLE";
    private static final String MAX_COUNT = "MAX_COUNT";

    /**
     * 获取启动 Intent
     *
     * @param context      上下文
     * @param subscriberID subscriberID
     */
    public static Intent getStartIntent(Context context, int subscriberID,
                                        boolean isSingle, int maxCount) {
        Intent intent = new Intent(context, GetFileActivity.class);
        intent.putExtra(SUBSCRIBER_ID, subscriberID);
        intent.putExtra(IS_SINGLE, isSingle);
        intent.putExtra(MAX_COUNT, maxCount);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            doGet();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        doGet();
    }

    /**
     * 获取文件
     */
    private void doGet() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        if (!getIntent().getBooleanExtra(IS_SINGLE, true)) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        IntentUtils.startIntentForResult(intent, this, GET_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (getIntent().getBooleanExtra(IS_SINGLE, true)) {
                getSingle(data.getData());
            } else {
                getMultiple(data, getIntent().getIntExtra(MAX_COUNT, 1));
            }
        } else {
            RxGetFile.getInstance().onError(new Throwable("用户放弃"), getSubscriberID());
            finish();
        }
    }

    private void getMultiple(Intent data, int max) {
        final ClipData clipData = data.getClipData();

        final ProgressDialog mDialog = new ProgressDialog(this, R.style.AppTheme_Dialog_Light);
        WindowManager.LayoutParams params = mDialog.getWindow()
                .getAttributes();
        params.dimAmount = 0f;
        mDialog.getWindow().setAttributes(params);
        mDialog.setTitle("正在获取文件");
        mDialog.setCancelable(false);
        mDialog.show();

        if (clipData != null) {
            int size = data.getClipData().getItemCount();
            if (size > max) {
                Toast.makeText(this, String.format(Locale.getDefault(),
                        "选择数量超过%d个,%d个未保存",
                        max, size - max), Toast.LENGTH_LONG).show();
            }
            final int finalSize = Math.min(size, max);

            Observable.create(new Observable.OnSubscribe<Uri>() {
                @Override
                public void call(Subscriber<? super Uri> subscriber) {
                    for (int i = 0; i < finalSize; i++) {
                        subscriber.onNext(clipData.getItemAt(i).getUri());
                    }
                    subscriber.onCompleted();
                }
            }).lift(new Observable.Operator<File, Uri>() {
                @Override
                public Subscriber<? super Uri> call(final Subscriber<? super File> subscriber) {

                    return new Subscriber<Uri>() {
                        @Override
                        public void onCompleted() {
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(Throwable e) {
                            subscriber.onError(e);
                        }

                        @Override
                        public void onNext(Uri uri) {
                            RxFileUtils.getFileUrlWithAuthority(Init.getApplication(), uri)
                                    .subscribe(new Action1<File>() {
                                        @Override
                                        public void call(File file) {
                                            subscriber.onNext(file);
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            subscriber.onError(throwable);
                                        }
                                    });
                        }
                    };
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<File>() {
                        @Override
                        public void onCompleted() {
                            RxGetFile.getInstance().onComplete(getSubscriberID());
                            mDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            RxGetFile.getInstance().onError(e, getSubscriberID());
                            mDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void onNext(File file) {
                            RxGetFile.getInstance().onAns(file, getSubscriberID());
                        }
                    });
        } else {
            getSingle(data.getData());
        }
    }

    private void getSingle(Uri data) {
        RxFileUtils.getFileUrlWithAuthority(this, data)
                .first().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        RxGetFile.getInstance().onError(e, getSubscriberID());
                        finish();
                    }

                    @Override
                    public void onNext(File file) {
                        RxGetFile.getInstance().onAns(file, getSubscriberID());
                        RxGetFile.getInstance().onComplete(getSubscriberID());
                        finish();
                    }
                });
    }

    private int getSubscriberID() {
        return getIntent().getIntExtra(SUBSCRIBER_ID, 0);
    }
}

package moe.xing.rxandroidfilepicker;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.io.File;

import moe.xing.baseutils.Init;
import moe.xing.rvutils.DivItemDecoration;
import moe.xing.rxandroidfilepicker.databinding.ActivityMainBinding;
import moe.xing.rxfilepicker.RxGetFile;
import rx.Subscriber;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Init.getInstance(getApplication(), true, "1", "");
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_main, null, false);
        setContentView(mBinding.getRoot());

        mAdapter = new Adapter();
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.addItemDecoration(new DivItemDecoration(4));
        mBinding.recyclerView.setAdapter(mAdapter);

        final Subscriber<File> fileSubscriber = new Subscriber<File>() {
            @Override
            public void onCompleted() {
                Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "error:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(File file) {
                mAdapter.addData(file);
                Toast.makeText(MainActivity.this, "file add", Toast.LENGTH_SHORT).show();
            }
        };

        RxView.clicks(mBinding.single).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                RxGetFile.newBuilder().isSingle(true).build().subscribe(fileSubscriber);
            }
        });

        RxView.clicks(mBinding.multiple).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                RxGetFile.newBuilder().isSingle(false).build().subscribe(fileSubscriber);
            }
        });


    }
}

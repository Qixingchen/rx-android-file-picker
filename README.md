Rx-Get-image

[![Release](https://jitpack.io/v/Qixingchen/rx-android-file-picker.svg?style=flat-square)](https://jitpack.io/#Qixingchen/rx-android-file-picker)
[![Build Status](https://travis-ci.org/Qixingchen/rx-android-file-picker.svg?branch=master)](https://travis-ci.org/Qixingchen/rx-android-file-picker)
[![Coverage Status](https://coveralls.io/repos/github/Qixingchen/rx-android-file-picker/badge.svg)](https://coveralls.io/github/Qixingchen/rx-android-file-picker)

---
### download

 use [jitpack](https://jitpack.io/#Qixingchen/rx-android-file-picker)

### how to use

``` java
    RxGetFile.newBuilder().isSingle(true).build()
    .subscribe(new Subscriber<File>() {
        @Override
        public void onCompleted() {
             // todo
        }

        @Override
        public void onError(Throwable e) {
            // todo
        }

        @Override
        public void onNext(File file) {
             // todo
        }
    });
```

default config:
``` java
isSingle = true;
maxArraySize = Integer.MAX_VALUE;
```
read javadoc in [jitpack](https://jitpack.io/com/github/Qixingchen/rx-android-file-picker/-SNAPSHOT/javadoc/)

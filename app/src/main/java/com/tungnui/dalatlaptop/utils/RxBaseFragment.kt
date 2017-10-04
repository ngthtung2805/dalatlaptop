package com.tungnui.dalatlaptop.utils

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by thanh on 23/09/2017.
 */
open class RxBaseFragment(): Fragment(){
    protected var mCompositeDisposable= CompositeDisposable()
    override fun onResume() {
        super.onResume()
        mCompositeDisposable=CompositeDisposable()
    }

    override fun onPause() {
        super.onPause()
        if(!mCompositeDisposable.isDisposed)
            mCompositeDisposable.dispose()

        mCompositeDisposable.clear()
    }
}
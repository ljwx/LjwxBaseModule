package com.ljwx.baseapp.vm.model

import com.ljwx.baseapp.response.BaseResponse
import com.ljwx.baseapp.response.DataResult
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseDataRepository<Server> {

    companion object {

        private var observerOnError: ((e: Throwable) -> Unit)? = null

        private var responseFail: ((data: Any?) -> Unit)? = null

        /**
         * 通用的请求错误逻辑(代码执行错误)
         */
        fun setCommonOnError(onError: (e: Throwable) -> Unit) {
            observerOnError = onError
        }

        /**
         * 通用的请求失败逻辑(接口返回结果失败)
         */
        fun setCommonResponseFail(responseFail: (data: Any?) -> Unit) {
            Companion.responseFail = responseFail
        }
    }

    open val TAG = this.javaClass.simpleName

    private var mCompositeDisposable: CompositeDisposable? = null

    protected val mApiServer
        get() = createServer()


    abstract fun createServer(): Server

    /**
     * 页面销毁时,自动取消网络请求
     *
     * @param disposable Rxjava2版本的dispose
     */
    open fun autoClear2(disposable: io.reactivex.disposables.Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
    }

    /**
     * 页面销毁时,自动取消网络请求
     *
     * @param disposable Rxjava3版本的dispose
     */
    open fun autoClear3(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 自动取消
     */
    open fun onCleared() {
        mCompositeDisposable?.clear()
    }

    /**
     * RxJava3版本的结果监听
     */
    abstract inner class QuickObserver3<T : Any> : io.reactivex.rxjava3.core.Observer<T>,
        IQuickObserver<T> {
        override fun onSubscribe(d: Disposable) {
            autoClear3(d)
        }

        override fun onError(e: Throwable) {
            observerOnError?.invoke(e)
        }

        override fun onComplete() {

        }

        override fun onNext(value: T) {
            onResponse(value)
        }

        /**
         * 接口结果响应
         *
         * @param response 结果
         */
        override fun onResponse(response: T) {
            if (response is BaseResponse<*>) {
                if (response.isSuccess()) {
                    onResponseSuccess(DataResult.Success(response))
                } else {
                    onResponseFail(DataResult.Fail(response))
                }
            }
        }

        /**
         * 接口数据成功
         *
         * @param result 成功的结果
         */
        abstract override fun onResponseSuccess(result: DataResult.Success<T>)

        /**
         * 接口数据失败
         *
         * @param result 失败的结果
         */
        override fun onResponseFail(result: DataResult.Fail<T>) {
            responseFail?.invoke(result.data)
        }

    }

    /**
     * RxJava2版本的结果监听
     */
    abstract inner class QuickObserver<T : Any> : io.reactivex.Observer<T>, IQuickObserver<T> {
        override fun onSubscribe(d: io.reactivex.disposables.Disposable) {
            autoClear2(d)
        }

        override fun onError(e: Throwable) {
            observerOnError?.invoke(e)
        }

        override fun onComplete() {

        }

        override fun onNext(value: T) {
            onResponse(value)
        }

        /**
         * 接口结果响应
         *
         * @param response 结果
         */
        override fun onResponse(response: T) {
            if (response is BaseResponse<*>) {
                if (response.isSuccess()) {
                    onResponseSuccess(DataResult.Success(response))
                } else {
                    onResponseFail(DataResult.Fail(response))
                }
            }
        }

        /**
         * 接口数据成功
         *
         * @param result 成功的结果
         */
        abstract override fun onResponseSuccess(result: DataResult.Success<T>)

        /**
         * 接口数据失败
         *
         * @param result 失败的结果
         */
        override fun onResponseFail(result: DataResult.Fail<T>) {
            responseFail?.invoke(result.data)
        }

    }

}
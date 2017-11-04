package com.tungnui.dalatlaptop.views.loopViewPager

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
class LoopViewPager : ViewPager {

    internal var mOuterPageChangeListener: ViewPager.OnPageChangeListener? = null
    private var mAdapter: LoopPagerAdapterWrapper? = null
    private var mBoundaryCaching = DEFAULT_BOUNDARY_CASHING

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        private var mPreviousOffset = -1f
        private var mPreviousPosition = -1f

        override fun onPageSelected(position: Int) {

            val realPosition = mAdapter!!.toRealPosition(position)
            if (mPreviousPosition != realPosition.toFloat()) {
                mPreviousPosition = realPosition.toFloat()
                if (mOuterPageChangeListener != null) {
                    mOuterPageChangeListener!!.onPageSelected(realPosition)
                }
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float,
                                    positionOffsetPixels: Int) {
            var realPosition = position
            if (mAdapter != null) {
                realPosition = mAdapter!!.toRealPosition(position)

                if (positionOffset == 0f
                        && mPreviousOffset == 0f
                        && (position == 0 || position == mAdapter!!.count - 1)) {
                    setCurrentItem(realPosition, false)
                }
            }

            mPreviousOffset = positionOffset
            if (mOuterPageChangeListener != null) {
                if (mAdapter != null && realPosition != mAdapter!!.realCount - 1) {
                    mOuterPageChangeListener!!.onPageScrolled(realPosition,
                            positionOffset, positionOffsetPixels)
                } else {
                    if (positionOffset > .5) {
                        mOuterPageChangeListener!!.onPageScrolled(0, 0f, 0)
                    } else {
                        mOuterPageChangeListener!!.onPageScrolled(realPosition,
                                0f, 0)
                    }
                }
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (mAdapter != null) {
                val position = super@LoopViewPager.getCurrentItem()
                val realPosition = mAdapter!!.toRealPosition(position)
                if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == mAdapter!!.count - 1)) {
                    setCurrentItem(realPosition, false)
                }
            }
            if (mOuterPageChangeListener != null) {
                mOuterPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }
    }

    /**
     * If set to true, the boundary views (i.e. first and last) will never be destroyed
     * This may help to prevent "blinking" of some views
     *
     * @param flag
     */
    fun setBoundaryCaching(flag: Boolean) {
        mBoundaryCaching = flag
        if (mAdapter != null) {
            mAdapter!!.setBoundaryCaching(flag)
        }
    }

    override fun setAdapter(adapter: PagerAdapter) {
        mAdapter = LoopPagerAdapterWrapper(adapter)
        mAdapter!!.setBoundaryCaching(mBoundaryCaching)
        super.setAdapter(mAdapter)
        setCurrentItem(0, false)
    }

    override fun getAdapter(): PagerAdapter? {
        return if (mAdapter != null) mAdapter!!.realAdapter else mAdapter
    }

    override fun getCurrentItem(): Int {
        return if (mAdapter != null) mAdapter!!.toRealPosition(super.getCurrentItem()) else 0
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        val realItem = mAdapter!!.toInnerPosition(item)
        super.setCurrentItem(realItem, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        if (currentItem != item) {
            setCurrentItem(item, true)
        }
    }

    override fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mOuterPageChangeListener = listener
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        super.setOnPageChangeListener(onPageChangeListener)
    }

    companion object {

        private val DEFAULT_BOUNDARY_CASHING = false
        fun toRealPosition(position: Int, count: Int): Int {
            var position = position
            position = position - 1
            if (position < 0) {
                position += count
            } else {
                position = position % count
            }
            return position
        }
    }

}

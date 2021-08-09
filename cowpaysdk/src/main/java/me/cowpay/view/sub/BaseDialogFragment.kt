package me.cowpay.view.sub

import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager


open class BaseDialogFragment : AppCompatDialogFragment() {

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            Log.d("ABSDIALOGFRAG", "Exception", e)
        }

    }

    internal var mIsStateAlreadySaved = false
    internal var mPendingShowDialog = false

    override fun onResume() {
        mIsStateAlreadySaved = false
        if (activity != null && !activity!!.isFinishing) {
            onResumeFragments()
        }
        super.onResume()
    }

    fun onResumeFragments() {
        if (mPendingShowDialog) {
            if (activity != null && !activity!!.isFinishing) {
                mPendingShowDialog = false
                showDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mIsStateAlreadySaved = true
    }

    private fun showDialog() {
        if (mIsStateAlreadySaved) {
            mPendingShowDialog = true
        } else {
            val fm = fragmentManager
            val baseDialogFragment = BaseDialogFragment()
            baseDialogFragment.show(fm!!, "BaseDialogFragment")
        }
    }
}
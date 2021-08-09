package me.cowpay.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import me.cowpay.CowPayBaseActivity
import me.cowpay.R
import me.cowpay.databinding.FragmentMenuBinding


class MenuFragment : androidx.fragment.app.Fragment() {
    lateinit var binding: FragmentMenuBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_menu, container, false)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CowPayBaseActivity) {
            activityCowPay = context as CowPayBaseActivity
        }
    }

    override fun onResume() {
        super.onResume()
    }

    lateinit var activityCowPay: CowPayBaseActivity


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activityCowPay = getActivity() as CowPayBaseActivity

        initializeView()
        setListener()
    }

    fun initializeView() {
    }

    fun setListener() {
    }
}
package me.cowpay.observer

interface OnAskUserAction {
    abstract fun onPositiveAction()
    abstract fun onNegativeAction()
}
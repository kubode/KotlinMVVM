package com.teamlab.kotlin.mvvm.ext

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.teamlab.kotlin.mvvm.util.HasObjectGraphFinder

fun HasObjectGraphFinder.Companion.of(context: Context): HasObjectGraphFinder {
    return HasObjectGraphFinder({ context }, { context.applicationContext })
}

fun HasObjectGraphFinder.Companion.of(activity: Activity): HasObjectGraphFinder {
    return HasObjectGraphFinder({ activity.applicationContext })
}

fun HasObjectGraphFinder.Companion.of(fragment: Fragment): HasObjectGraphFinder {
    return HasObjectGraphFinder({ fragment.activity }, { fragment.activity?.application })
}

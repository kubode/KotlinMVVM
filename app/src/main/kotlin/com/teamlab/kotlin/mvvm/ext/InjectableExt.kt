package com.teamlab.kotlin.mvvm.ext

import android.content.Context
import android.support.v4.app.Fragment
import com.teamlab.kotlin.mvvm.util.Injectable
import com.teamlab.kotlin.mvvm.util.HasObjectGraphFinder

fun HasObjectGraphFinder.Companion.of(context: Context): HasObjectGraphFinder {
    return HasObjectGraphFinder({ context }, { context.applicationContext })
}

fun <T> HasObjectGraphFinder.Companion.of(fragment: T): HasObjectGraphFinder where T : Fragment, T : Injectable {
    return HasObjectGraphFinder({ fragment.targetFragment }, { fragment.activity }, { fragment.activity?.application })
}

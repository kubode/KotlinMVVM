package com.teamlab.kotlin.mvvm.model

import com.teamlab.kotlin.mvvm.MyApplicationComponent
import dagger.Component
import javax.inject.Scope

@Scope
annotation class PerAccount

@PerAccount
@Component(dependencies = arrayOf(MyApplicationComponent::class))
interface AccountComponent

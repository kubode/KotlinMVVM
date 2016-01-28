package com.teamlab.kotlin.mvvm.di

import dagger.Component

@PerAccount
@Component(dependencies = arrayOf(MyApplicationComponent::class))
interface AccountComponent

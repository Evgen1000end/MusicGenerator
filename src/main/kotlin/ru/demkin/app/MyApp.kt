package ru.demkin.app

import ru.demkin.view.MainView
import tornadofx.App
import tornadofx.reloadViewsOnFocus
import tornadofx.property
import tornadofx.getProperty

class MyApp: App(MainView::class, Styles::class)

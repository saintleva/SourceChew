import com.github.saintleva.sourcechew.di.appModule
import com.github.saintleva.sourcechew.di.domainModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(domainModule, appModule)
    }
}
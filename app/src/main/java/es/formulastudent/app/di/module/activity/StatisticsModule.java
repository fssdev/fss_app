package es.formulastudent.app.di.module.activity;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.business.BusinessModule;
import es.formulastudent.app.di.module.business.SharedPreferencesModule;
import es.formulastudent.app.mvp.data.business.statistics.StatisticsBO;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.screen.statistics.StatisticsPresenter;

@Module(includes = {ContextModule.class, BusinessModule.class, SharedPreferencesModule.class})
public class StatisticsModule {

    private StatisticsPresenter.View view;

    public StatisticsModule(StatisticsPresenter.View view) {
        this.view = view;
    }

    @Provides
    public StatisticsPresenter.View provideView() {
        return view;
    }

    @Provides
    public StatisticsPresenter providePresenter(StatisticsPresenter.View categoryView,
                                                Context context, StatisticsBO statisticsBO,
                                                User loggedUser) {
        return new StatisticsPresenter(categoryView, context, statisticsBO, loggedUser);
    }


}

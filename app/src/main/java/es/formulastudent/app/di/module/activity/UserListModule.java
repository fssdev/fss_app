package es.formulastudent.app.di.module.activity;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.di.module.business.BusinessModule;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.data.business.user.UserBO;
import es.formulastudent.app.mvp.data.business.userrole.UserRoleBO;
import es.formulastudent.app.mvp.view.activity.user.UserPresenter;

@Module(includes = {ContextModule.class, BusinessModule.class})
public class UserListModule {

    private UserPresenter.View view;

    public UserListModule(UserPresenter.View view) {
        this.view = view;
    }

    @Provides
    public UserPresenter.View provideView() {
        return view;
    }

    @Provides
    public UserPresenter providePresenter(UserPresenter.View categoryView, Context context, UserBO userBO, TeamBO teamBO, UserRoleBO userRoleBO) {
        return new UserPresenter(categoryView, context, userBO, teamBO, userRoleBO);
    }
}

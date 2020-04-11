package es.formulastudent.app.di.module.business;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import es.formulastudent.app.di.module.ContextModule;
import es.formulastudent.app.mvp.data.business.auth.AuthBO;
import es.formulastudent.app.mvp.data.business.auth.impl.AuthBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.briefing.BriefingBO;
import es.formulastudent.app.mvp.data.business.briefing.impl.BriefingBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.conecontrol.ConeControlBO;
import es.formulastudent.app.mvp.data.business.conecontrol.impl.ConeControlBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.dynamicevent.DynamicEventBO;
import es.formulastudent.app.mvp.data.business.dynamicevent.impl.DynamicEventBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.egress.EgressBO;
import es.formulastudent.app.mvp.data.business.egress.impl.EgressBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.imageuploader.ImageBO;
import es.formulastudent.app.mvp.data.business.imageuploader.impl.ImageBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.mailsender.MailSender;
import es.formulastudent.app.mvp.data.business.mailsender.MailSenderImpl;
import es.formulastudent.app.mvp.data.business.racecontrol.RaceControlBO;
import es.formulastudent.app.mvp.data.business.racecontrol.impl.RaceControlBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.statistics.StatisticsBO;
import es.formulastudent.app.mvp.data.business.statistics.impl.StatisticsBOImpl;
import es.formulastudent.app.mvp.data.business.team.TeamBO;
import es.formulastudent.app.mvp.data.business.team.impl.TeamBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.teammember.TeamMemberBO;
import es.formulastudent.app.mvp.data.business.teammember.impl.TeamMemberBOFirebaseImpl;
import es.formulastudent.app.mvp.data.business.user.UserBO;
import es.formulastudent.app.mvp.data.business.user.impl.UserBOFirebaseImpl;
import es.formulastudent.app.mvp.data.model.User;
import es.formulastudent.app.mvp.view.utils.LoadingDialog;
import es.formulastudent.app.mvp.view.utils.Messages;


@Module(includes = {FirebaseModule.class, ContextModule.class, SharedPreferencesModule.class, UtilsModule.class})
public class BusinessModule {

    /**
     * Provide Team business
     * @param firebaseFirestore
     * @return
     */
    @Provides
    public TeamBO provideTeamBO(FirebaseFirestore firebaseFirestore,
                                LoadingDialog loadingDialog, Messages messages) {
        return new TeamBOFirebaseImpl(firebaseFirestore, loadingDialog, messages);
    }


    /**
     * Provide Briefing business
     * @param firebaseFirestore
     * @return
     */
    @Provides
    public BriefingBO provideBriefingBO(FirebaseFirestore firebaseFirestore,
                                        LoadingDialog loadingDialog, Messages messages) {
        return new BriefingBOFirebaseImpl(firebaseFirestore, loadingDialog, messages);
    }


    /**
     * Provide TeamMember business
     * @param firebaseFirestore
     * @return
     */
    @Provides
    public TeamMemberBO provideTeamMemberBO(FirebaseFirestore firebaseFirestore,
                                            LoadingDialog loadingDialog, Messages messages) {
        return new TeamMemberBOFirebaseImpl(firebaseFirestore, loadingDialog, messages);
    }


    /**
     * Provide auth business
     * @param firebaseAuth
     * @return
     */
    @Provides
    public AuthBO provideAuthBO(FirebaseAuth firebaseAuth, UserBO userBO) {
        return new AuthBOFirebaseImpl(firebaseAuth, userBO);
    }


    /**
     * Provide Dynamic event business
     * @param firebaseFirestore
     * @return
     */
    @Provides
    public DynamicEventBO provideDynamicEventBO(FirebaseFirestore firebaseFirestore, FirebaseAuth firebaseAuth,
                                                LoadingDialog loadingDialog, Messages messages) {
        return new DynamicEventBOFirebaseImpl(firebaseFirestore, firebaseAuth, loadingDialog, messages);
    }

    /**
     * Provide Egress business
     * @param firebaseFirestore
     * @return
     */
    @Provides
    public EgressBO provideEgressBO(FirebaseFirestore firebaseFirestore) {
        return new EgressBOFirebaseImpl(firebaseFirestore);
    }

    /**
     * Provide Statistics business
     * @return
     */
    @Provides
    public StatisticsBO provideStatisticsBO(DynamicEventBO dynamicEventBO, TeamMemberBO teamMemberBO, Context context) {
        return new StatisticsBOImpl(dynamicEventBO, teamMemberBO, context);
    }

    /**
     * Provide Race Control Endurance business
     * @return
     */
    @Provides
    public RaceControlBO provideRaceControlBO(FirebaseFirestore firebaseFirestore, TeamBO teamBO, ConeControlBO coneControlBO) {
        return new RaceControlBOFirebaseImpl(firebaseFirestore, teamBO, coneControlBO);
    }

    /**
     * Provide User business
     * @param firebaseFirestore
     * @return
     */
    @Provides
    public UserBO provideUserBO(FirebaseFirestore firebaseFirestore) {
        return new UserBOFirebaseImpl(firebaseFirestore);
    }

    /**
     * Provide image uploader business
     * @param firebaseStorage
     * @return
     */
    @Provides
    public ImageBO provideImageUploaderBO(FirebaseStorage firebaseStorage) {
        return new ImageBOFirebaseImpl(firebaseStorage);
    }

    /**
     * Provide Cone control business
     * @param firebaseFirestore
     * @param context
     * @return
     */
    @Provides
    public ConeControlBO provideConeControlBO(FirebaseFirestore firebaseFirestore, Context context) {
        return new ConeControlBOFirebaseImpl(firebaseFirestore, context);
    }

    /**
     * Provide mail sender
     * @param loggedUser
     * @param context
     * @return
     */
    @Provides
    public MailSender provideMailSender(User loggedUser, Context context) {
        return new MailSenderImpl(context, loggedUser);
    }
}

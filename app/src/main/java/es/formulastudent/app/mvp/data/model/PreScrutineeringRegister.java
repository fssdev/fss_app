package es.formulastudent.app.mvp.data.model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class PreScrutineeringRegister extends EventRegister implements Serializable {

    //Database constants
    public static final String TIME = "time";
    private Long time;

    public PreScrutineeringRegister(String teamID, String team, String userID, String user, String userImage,
                                    Date date, String carType, Long carNumber, Boolean briefingDone, EventType type, Long time) {
        super(teamID, team, userID, user, userImage, date, carType, carNumber, briefingDone, type);
        this.time = time;
    }

    public Map<String, Object> toObjectData(){

        Map<String, Object> docData = super.toObjectData();
        docData.put(PreScrutineeringRegister.TIME, this.time);

        return docData;
    }

    public PreScrutineeringRegister(DocumentSnapshot object, EventType type){
        super(object, type);
        this.time = object.getLong(PreScrutineeringRegister.TIME);
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
package re.adjustme.de.readjustme.Bean.Entity;

import android.content.Context;

import org.json.JSONObject;

import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;

/**
 * Wrapper class for the data we send to backend.
 * <p>
 * Created by semmel on 27.01.2018.
 */
@Persistence(name = "backend", type = PersistenceType.BACKEND)
public class BackendDataEntity {

    private JSONObject payload;
    private Context context;

    public BackendDataEntity(JSONObject jsonObject, Context context) {
        this.context = context;
        this.payload = jsonObject;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public JSONObject getPayload() {

        return payload;
    }

}

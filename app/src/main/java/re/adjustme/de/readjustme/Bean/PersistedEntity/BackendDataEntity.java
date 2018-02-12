package re.adjustme.de.readjustme.Bean.PersistedEntity;

import android.content.Context;

import org.json.JSONObject;

import re.adjustme.de.readjustme.Persistence.Persistence;
import re.adjustme.de.readjustme.Predefined.PersistenceType;

/**
 * Wrapper class for the data we will send to the backend.
 * <p>
 * Created  on 27.01.2018.
 * @author Sebastian Selmke
 * @version 1.0
 * @since 1.0
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

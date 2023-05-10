package forte.buzl.uk;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import forte.buzl.uk.MediaSessionAPI;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(MediaSessionAPI.class);
        super.onCreate(savedInstanceState);
    }
}
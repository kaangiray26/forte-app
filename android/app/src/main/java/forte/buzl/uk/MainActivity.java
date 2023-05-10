package forte.buzl.uk;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import forte.buzl.uk.MediaControl;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(MediaControl.class);
        super.onCreate(savedInstanceState);
    }
}
import com.nativedevelopment.smartgrid.Action;
import com.nativedevelopment.smartgrid.Data;
import com.nativedevelopment.smartgrid.IDevice;

import java.util.UUID;


public class FakeDevice implements IDevice {

    public FakeDevice(UUID deviceId) {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public void performAction(Action a) {

    }
}

package basic.fiducials;

import org.mt4j.MTApplication;

public class StartFiducialExample extends MTApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize();
	}
	
	@Override
	public void startUp() {
		this.addScene(new FiducialScene(this, "Fiducial Scene"));
	}

}

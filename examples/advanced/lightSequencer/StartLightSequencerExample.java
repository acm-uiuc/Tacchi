package advanced.lightSequencer;

import org.mt4j.MTApplication;

public class StartLightSequencerExample extends MTApplication{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String args[]){
		initialize();
	}
	
	@Override
	public void startUp(){
		this.addScene(new LightSequencerScene(this, "Light Sequencer scene"));
	}
}

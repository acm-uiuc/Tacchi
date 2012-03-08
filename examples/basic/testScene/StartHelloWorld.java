package basic.testScene;

import org.mt4j.MTApplication;

public class StartHelloWorld extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addScene(new JoelsGame(this, "Hello World Scene"));
	}
}

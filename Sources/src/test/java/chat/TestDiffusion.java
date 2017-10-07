package chat;

import static chat.common.Log.LOGGER_NAME_TEST;
import static chat.common.Log.LOG_ON;
import static chat.common.Log.TEST;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import chat.client.Client;
import chat.common.Interceptor;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestDiffusion extends Scenario {

	private static final int WAIT = 500;

  @Test
	@Override
	public void constructAndRun() throws Exception {
	  
		Log.configureALogger(LOGGER_NAME_TEST, Level.INFO);

		Server s0 = instanciateAServer("0");
		sleep(WAIT);
		Client c0 = instanciateAClient(2050);
		sleep(WAIT);
		Client c1 = instanciateAClient(2050);
		sleep(WAIT);
		Client c2 = instanciateAClient(2050);
		sleep(2* WAIT);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the test of the diffusion algorithm...");
		}

		Interceptor.setInterceptionEnabled(true);
		emulateAnInputLineFromTheConsoleForAClient(c0, "message 1 from 0");
		sleep(WAIT);
		Interceptor.setInterceptionEnabled(false);
		emulateAnInputLineFromTheConsoleForAClient(c1, "message 2 from 1");
		sleep(WAIT);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("end of the scenario.");
		}

		Assert.assertEquals(c0.getState().MsgBag.size(), 0);
		Assert.assertEquals(c1.getState().MsgBag.size(), 0);
		Assert.assertEquals(c2.getState().MsgBag.size(), 0);

		emulateAnInputLineFromTheConsoleForAServer(s0, "quit");
		sleep(WAIT);
		// finish properly
		emulateAnInputLineFromTheConsoleForAClient(c0, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAClient(c1, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAClient(c2, "quit");
		sleep(WAIT);

		sleep(WAIT);

	}

}

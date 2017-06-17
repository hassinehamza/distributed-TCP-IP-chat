package chat;

import static chat.common.Log.LOGGER_NAME_TEST;
import static chat.common.Log.LOG_ON;
import static chat.common.Log.TEST;
import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import chat.client.Client;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestDiffusion extends Scenario{

	@Test
	@Override
	public void constructAndRun() throws Exception {
		// TODO Auto-generated method stub
		Log.configureALogger(LOGGER_NAME_TEST, Level.INFO);

		Server s0 = instanciateAServer("0");
		sleep(500);
		Server s1 = instanciateAServer("1 localhost 0");
		sleep(500);
		//Interceptor.setInterceptionEnabled(true);
		sleep(500);
		sleep(15000);
		Client c0 = instanciateAClient(2050);
		sleep(500);
		Client c1 = instanciateAClient(2051);
		sleep(500);
		Client c2 = instanciateAClient(2051);
		sleep(1000);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the test of the diffusion algorithm...");
		}
		
		
		
		emulateAnInputLineFromTheConsoleForAClient(c0, "question from 0");
		
		emulateAnInputLineFromTheConsoleForAClient(c1, "reponse from 1");
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("end of the scenario.");
		}

		// finish properly
		emulateAnInputLineFromTheConsoleForAClient(c0, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s0, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(1000);

	}
	
}

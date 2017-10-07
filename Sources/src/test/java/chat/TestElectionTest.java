package chat;



import static chat.common.Log.LOGGER_NAME_TEST;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import chat.common.Interceptor;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestElectionTest extends Scenario {

  private static final String LEADER = "leader";
  private static final String NON_LEADER = "non-leader";
  private static final String INITIATOR = "Initiator";
  private static final int WAIT = 500;

  @Test
	@Override
	public void constructAndRun() throws Exception {
		// TODO Auto-generated method stub
		Log.configureALogger(LOGGER_NAME_TEST, Level.WARN);

		Server s1 = instanciateAServer("0");
		sleep(WAIT);
		Server s2 = instanciateAServer("1 localhost 0");
		sleep(WAIT);
		Server s5 = instanciateAServer("4 localhost 0 localhost 1");
		sleep(WAIT);
		Server s3 = instanciateAServer("2 localhost 1");
		sleep(WAIT);
		Server s4 = instanciateAServer("3 localhost 2");
		sleep(WAIT);
		Server s6 = instanciateAServer("5 localhost 2");
		sleep(WAIT);
		Interceptor.setInterceptionEnabled(true);

		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAServer(s3, INITIATOR);

		emulateAnInputLineFromTheConsoleForAServer(s6, INITIATOR);

		emulateAnInputLineFromTheConsoleForAServer(s5, INITIATOR);
	
		emulateAnInputLineFromTheConsoleForAServer(s2, INITIATOR);
		
		emulateAnInputLineFromTheConsoleForAServer(s1, INITIATOR);
		sleep(4 * WAIT);
		Assert.assertEquals(LEADER , s1.getState().getStatus() );
		Assert.assertEquals(NON_LEADER , s2.getState().getStatus() );
		Assert.assertEquals(NON_LEADER , s3.getState().getStatus() );
		Assert.assertEquals(NON_LEADER , s4.getState().getStatus() );
		Assert.assertEquals(NON_LEADER , s5.getState().getStatus() );
		Assert.assertEquals(NON_LEADER , s6.getState().getStatus() );

		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAServer(s2, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAServer(s3, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAServer(s4, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAServer(s5, "quit");
		sleep(WAIT);
		emulateAnInputLineFromTheConsoleForAServer(s6, "quit");
		sleep(2 *WAIT);

	}

}

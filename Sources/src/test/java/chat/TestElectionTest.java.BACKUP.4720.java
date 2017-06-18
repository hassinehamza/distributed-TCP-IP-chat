package chat;


<<<<<<< HEAD
import static chat.common.Log.LOGGER_NAME_TEST;
=======
import chat.common.Interceptor;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;
>>>>>>> diffusioncausale

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import chat.common.Interceptor;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestElectionTest extends Scenario {

	@Test
	@Override
	public void constructAndRun() throws Exception {
		// TODO Auto-generated method stub
		Log.configureALogger(LOGGER_NAME_TEST, Level.WARN);

		Server s1 = instanciateAServer("0");
		sleep(500);
		Server s2 = instanciateAServer("1 localhost 0");
		sleep(500);
		Server s5 = instanciateAServer("4 localhost 0 localhost 1");
		sleep(500);
		Server s3 = instanciateAServer("2 localhost 1");
		sleep(500);
		Server s4 = instanciateAServer("3 localhost 2");
		sleep(500);
		Server s6 = instanciateAServer("5 localhost 2");
		sleep(500);
		Interceptor.setInterceptionEnabled(true);
<<<<<<< HEAD
		sleep(500);
		emulateAnInputLineFromTheConsoleForAServer(s3, "Initiator");
=======
		emulateAnInputLineFromTheConsoleForAServer(s6, "Initiator");
		sleep(100);
>>>>>>> diffusioncausale
		emulateAnInputLineFromTheConsoleForAServer(s5, "Initiator");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s2, "Initiator");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s1, "Initiator");
		sleep(18000);
		Assert.assertEquals("leader" , s1.getState().getStatus() );
		Assert.assertEquals("non-leader" , s2.getState().getStatus() );
		Assert.assertEquals("non-leader" , s3.getState().getStatus() );
		Assert.assertEquals("non-leader" , s4.getState().getStatus() );
		Assert.assertEquals("non-leader" , s5.getState().getStatus() );
		Assert.assertEquals("non-leader" , s6.getState().getStatus() );

		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s3, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s4, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s5, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s6, "quit");
		sleep(1000);

	}

}

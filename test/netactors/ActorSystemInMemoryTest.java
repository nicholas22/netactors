// /////////////////////////////////////////////////////////
// This file is part of netactors.
//
// netactors is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// netactors is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with netactors. If not, see <http://www.gnu.org/licenses/>.
// /////////////////////////////////////////////////////////
// Authored by: Nikolaos Tountas -> salam.kaser-at-gmail.com
// /////////////////////////////////////////////////////////
package netactors;

import static org.junit.Assert.assertEquals;
import lombok.val;
import netactors.actor.CachingActor;
import netactors.actor.CachingPingPongActor;
import netactors.actor.FailingActor;
import netactors.actor.InvalidConstructorProtectedActor;
import netactors.actor.InvalidNoArgConstructorActor;
import netactors.config.ActorConfiguration;
import netactors.exception.ActorCreationException;
import netactors.nexus.ActorURL;
import netactors.nexus.AdapterFactory;
import netactors.nexus.IAdapterFactory;
import netactors.nexus.ProtocolType;
import netactors.registry.AdapterRegistry;
import org.junit.Test;
import propel.core.functional.tuples.Pair;
import common.TestAspectDecorator;

public class ActorSystemInMemoryTest
    extends TestAspectDecorator
{
  private final ActorURL URL1 = new ActorURL(ProtocolType.MEM, "localhost", 12345);
  private final ActorURL URL2 = new ActorURL(ProtocolType.MEM, "localhost", 12346);
  private final ActorURL URL3 = new ActorURL(ProtocolType.MEM, "localhost", 12347);
  private final int DELAY_MS = 150;

  @Test
  public void givenActorSystemWithNoActors_whenActorsLookedUp_thenNoActorRefIsFound()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      assertEquals(null, as.actorFor(getUrl1()));
      assertEquals(null, as.actorFor(getUrl2()));
      assertEquals(null, as.actorFor(getUrl3()));
    }
    finally
    {
      as.shutdown();
    }
  }

  @Test
  public void givenActorSystemWithOneActor_whenExistingActorLookedUp_thenActorIsFound()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      // create actor
      val actor = as.actorOf(CachingActor.class);

      assertEquals(actor, as.actorFor(getUrl1()));
      assertEquals(null, as.actorFor(getUrl2()));
      assertEquals(null, as.actorFor(getUrl3()));

      assertEquals(actor, as.actorFor(getUrl1()));
      assertEquals(null, as.actorFor(getUrl2()));
      assertEquals(null, as.actorFor(getUrl3()));
    }
    finally
    {
      as.shutdown();
    }

    assertEquals(null, as.actorFor(getUrl1()));
    assertEquals(null, as.actorFor(getUrl2()));
    assertEquals(null, as.actorFor(getUrl3()));
  }

  @Test(expected = ActorCreationException.class)
  public void givenActorSystemWithNoActors_whenInvisibleConstructorActorClassGiven_thenNoActorIsCreated()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      as.actorOf(InvalidConstructorProtectedActor.class);
    }
    finally
    {
      as.shutdown();
    }
  }

  @Test(expected = ActorCreationException.class)
  public void givenActorSystemWithNoActors_whenNoArgConstructorActorClassGiven_thenNoActorIsCreated()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      as.actorOf(InvalidNoArgConstructorActor.class);
    }
    finally
    {
      as.shutdown();
    }
  }

  @Test(expected = ActorCreationException.class)
  public void givenActorSystemWithActor_whenSameActorCreated_thenExceptionThrown()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      as.actorOf(CachingActor.class);
      as.actorOf(CachingActor.class);
    }
    finally
    {
      as.shutdown();
    }
  }

  @Test
  public void givenTwoActors_whenDataExchangedBetweenActorRefs_thenDataReceived()
      throws Exception
  {
    val as = createTwoSystems();

    val as1 = as.getFirst();
    try
    {
      val as2 = as.getSecond();
      try
      {
        // create local actors for each system
        val act1 = as1.actorOf(CachingActor.class);
        val act2 = as2.actorOf(CachingActor.class);

        // obtain reference to remote actors and send some data
        val act1Ref = as2.actorFor(getUrl1());
        act1Ref.send("123");

        val act2Ref = as1.actorFor(getUrl2());
        act2Ref.send("234");
        Thread.sleep(getDelayMillis());

        assertEquals(1, act1.getCache().size());
        assertEquals("123", act1.getCache().get(0));
        assertEquals(1, act2.getCache().size());
        assertEquals("234", act2.getCache().get(0));

        // obtain reference to local actors
        val act1RefLocal = as1.actorFor(getUrl1());
        val act2RefLocal = as2.actorFor(getUrl2());

        // send some data
        act1RefLocal.send("123local");
        act2RefLocal.send("234local");
        Thread.sleep(getDelayMillis());

        assertEquals(2, act1.getCache().size());
        assertEquals("123local", act1.getCache().get(1));
        assertEquals(2, act2.getCache().size());
        assertEquals("234local", act2.getCache().get(1));
      }
      finally
      {
        as2.shutdown();
      }
    }
    finally
    {
      as1.shutdown();
    }
  }

  @Test
  public void givenTwoPingPongActors_whenDataExchangedBetweenActorRefs_thenDataReceivedAndSent()
      throws Exception
  {
    val as = createTwoSystems();

    val as1 = as.getFirst();
    try
    {
      val as2 = as.getSecond();
      try
      {
        // create local actors for each system (passing them the URL of each other)
        val act1 = as1.actorOf(CachingPingPongActor.class, new Object[] {getUrl2()});
        val act2 = as2.actorOf(CachingPingPongActor.class, new Object[] {getUrl1()});

        // obtain reference to remote actors
        val act1Ref = as2.actorFor(getUrl1());
        val act2Ref = as1.actorFor(getUrl2());

        // send some data
        act1Ref.send("123");
        Thread.sleep(getDelayMillis());

        assertEquals(1, act1.getCache().size());
        assertEquals("123", act1.getCache().get(0));
        assertEquals(1, act2.getCache().size());
        assertEquals(CachingPingPongActor.PREFIX + "123", act2.getCache().get(0));

        act2Ref.send("234");
        Thread.sleep(getDelayMillis());

        assertEquals(2, act1.getCache().size());
        assertEquals(CachingPingPongActor.PREFIX + "234", act1.getCache().get(1));
        assertEquals(2, act2.getCache().size());
        assertEquals("234", act2.getCache().get(1));

        // obtain reference to local actors
        val act1RefLocal = as1.actorFor(getUrl1());
        val act2RefLocal = as2.actorFor(getUrl2());

        // send some data
        act1RefLocal.send("123FromLocal");
        Thread.sleep(getDelayMillis());

        assertEquals(3, act1.getCache().size());
        assertEquals("123FromLocal", act1.getCache().get(2));
        assertEquals(3, act2.getCache().size());
        assertEquals(CachingPingPongActor.PREFIX + "123FromLocal", act2.getCache().get(2));

        act2RefLocal.send("234FromLocal");
        Thread.sleep(getDelayMillis());

        assertEquals(4, act1.getCache().size());
        assertEquals(CachingPingPongActor.PREFIX + "234FromLocal", act1.getCache().get(3));
        assertEquals(4, act2.getCache().size());
        assertEquals("234FromLocal", act2.getCache().get(3));
      }
      finally
      {
        as2.shutdown();
      }
    }
    finally
    {
      as1.shutdown();
    }
  }

  @Test
  public void givenTwoActors_whenOneActorThrows_thenNoExceptionsThrown_withDataNotSentAndNotQueued()
      throws Exception
  {
    val as = createTwoSystems();

    val as1 = as.getFirst();
    try
    {
      val as2 = as.getSecond();
      try
      {
        // create local actors for each system
        val act1 = as1.actorOf(FailingActor.class);
        val act2 = as2.actorOf(CachingActor.class);

        // obtain reference to remote actors and send some data
        val act1Ref = as2.actorFor(getUrl1());
        act1Ref.send("123");

        val act2Ref = as1.actorFor(getUrl2());
        act2Ref.send("234");
        Thread.sleep(getDelayMillis());

        assertEquals(1, act2.getCache().size());
        assertEquals("234", act2.getCache().get(0));
      }
      finally
      {
        as2.shutdown();
      }
    }
    finally
    {
      as1.shutdown();
    }
  }

  protected Pair<IActorSystem, IActorSystem> createTwoSystems()
  {
    // systems must share the same adapter registry, if ran in-memory, so that they can communicate and share data
    val adapterRegistry = new AdapterRegistry();
    val adapterFactory = new AdapterFactory(adapterRegistry);
    return new Pair<IActorSystem, IActorSystem>(createSystem1(adapterFactory), createSystem2(adapterFactory));
  }

  // used for single actor system tests
  protected IActorSystem createSystem1()
  {
    return createSystem1(new AdapterFactory());
  }

  protected IActorSystem createSystem1(IAdapterFactory factory)
  {
    val actorConfig1 = new ActorConfiguration(getUrl1());
    val actorSystem1 = new ActorSystem(actorConfig1, factory);
    return actorSystem1;
  }

  protected IActorSystem createSystem2(IAdapterFactory factory)
  {
    val actorConfig2 = new ActorConfiguration(getUrl2());
    val actorSystem2 = new ActorSystem(actorConfig2, factory);
    return actorSystem2;
  }

  protected ActorURL getUrl1()
  {
    return URL1;
  }

  protected ActorURL getUrl2()
  {
    return URL2;
  }

  protected ActorURL getUrl3()
  {
    return URL3;
  }

  protected int getDelayMillis()
  {
    return DELAY_MS;
  }
}

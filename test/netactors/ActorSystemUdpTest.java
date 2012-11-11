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
import static org.junit.Assert.assertNotNull;
import lombok.val;
import netactors.actor.CachingActor;
import netactors.nexus.ActorURL;
import netactors.nexus.ProtocolType;

public class ActorSystemUdpTest
    extends ActorSystemInMemoryTest
{
  private final ActorURL URL1 = new ActorURL(ProtocolType.UDP, "localhost", 12345);
  private final ActorURL URL2 = new ActorURL(ProtocolType.UDP, "localhost", 12346);
  private final ActorURL URL3 = new ActorURL(ProtocolType.UDP, "localhost", 12347);
  private final int DELAY_MS = 250;

  @Override
  protected ActorURL getUrl1()
  {
    return URL1;
  }

  @Override
  protected ActorURL getUrl2()
  {
    return URL2;
  }

  @Override
  protected ActorURL getUrl3()
  {
    return URL3;
  }

  @Override
  protected int getDelayMillis()
  {
    return DELAY_MS;
  }

  @Override
  public void givenActorSystemWithNoActors_whenActorsLookedUp_thenNoActorRefIsFound()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      assertEquals(null, as.actorFor(getUrl1()));
      // with UDP we send without knowing if anyone is listening, so these are non-null
      assertNotNull(as.actorFor(getUrl2()));
      assertNotNull(as.actorFor(getUrl3()));
    }
    finally
    {
      as.shutdown();
    }
  }

  @Override
  public void givenActorSystemWithOneActor_whenExistingActorLookedUp_thenActorIsFound()
      throws Exception
  {
    val as = createSystem1();
    try
    {
      // create actor
      val actor = as.actorOf(CachingActor.class);

      assertEquals(actor, as.actorFor(getUrl1()));
      // with UDP we send without knowing if anyone is listening, so these are non-null
      assertNotNull(as.actorFor(getUrl2()));
      assertNotNull(as.actorFor(getUrl3()));

      assertEquals(actor, as.actorFor(getUrl1()));
      // with UDP we send without knowing if anyone is listening, so these are non-null
      assertNotNull(as.actorFor(getUrl2()));
      assertNotNull(as.actorFor(getUrl3()));
    }
    finally
    {
      as.shutdown();
    }

    assertEquals(null, as.actorFor(getUrl1()));
    assertNotNull(as.actorFor(getUrl2()));
    assertNotNull(as.actorFor(getUrl3()));
  }
}

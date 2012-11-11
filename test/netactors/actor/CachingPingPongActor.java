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
package netactors.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.val;
import netactors.IActorSystem;
import netactors.config.IActorConfiguration;
import netactors.nexus.ActorURL;

public final class CachingPingPongActor
    extends UntypedActor
{
  public static final String PREFIX = "Pong";

  @Getter
  private final List<Object> cache = new ArrayList<Object>();

  private final ActorURL otherActorUrl;

  /**
   * Constructor
   */
  public CachingPingPongActor(final IActorSystem system, final IActorConfiguration config, final ActorURL otherActorUrl)
  {
    super(system, config);
    this.otherActorUrl = otherActorUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onReceive(final Serializable message)
  {
    cache.add(message);
    if (!message.toString().startsWith(PREFIX))
    {
      // get other actor
      val otherActor = this.getSystem().actorFor(otherActorUrl);
      if (otherActor != null)
        otherActor.send(PREFIX + message);
    }
  }
}

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
import netactors.ActorSystem;
import netactors.config.ActorConfiguration;
import netactors.config.IActorConfiguration;
import netactors.nexus.ActorURL;
import netactors.nexus.ProtocolType;

public final class InvalidNoArgConstructorActor
    extends UntypedActor
{
  /**
   * Constructor
   */
  protected InvalidNoArgConstructorActor()
  {
    super(new ActorSystem(createDummy()), createDummy());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onReceive(final Serializable message)
  {
  }

  private static IActorConfiguration createDummy()
  {
    return new ActorConfiguration(new ActorURL(ProtocolType.TCP, "test", 123));
  }

}

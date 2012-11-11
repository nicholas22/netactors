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

import java.io.Serializable;
import netactors.actor.IActor;
import netactors.exception.ActorCreationException;
import netactors.nexus.ActorURL;

/**
 * Interface of the actor system
 */
public interface IActorSystem
{
  /**
   * Creates a local actor of specified type
   * 
   * @throws NullPointerException An argument is null
   * @throws ActorCreationException The actor could not be created
   */
  <T extends IActor> T actorOf(Class<T> actorType)
      throws ActorCreationException;

  /**
   * Creates a local actor of specified type, passing it extra arguments
   * 
   * @throws NullPointerException An argument is null
   * @throws ActorCreationException The actor could not be created
   */
  <T extends IActor> T actorOf(Class<T> actorType, Object[] args)
      throws ActorCreationException;

  /**
   * Returns the actor at the specified URL, or null if no such actor exists
   * 
   * @throws NullPointerException An argument is null
   */
  IActor actorFor(ActorURL url);

  /**
   * Sends an asynchronous message to an actor, if it exists
   * 
   * @throws NullPointerException An argument is null
   */
  void tell(ActorURL actorUrl, Serializable message);

  /**
   * Shuts down all actors and cleans up all resources
   */
  void shutdown();
}

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
package netactors.registry;

import netactors.context.IActorContext;
import netactors.exception.ActorAlreadyExistsException;
import netactors.exception.AdapterStartException;
import netactors.nexus.ActorURL;

/**
 * Interface of an actor registry, responsible for maintaining and managing the life cycle of actors
 */
public interface IActorRegistry
{
  /**
   * Performs a non case-sensitive search and returns an actor's context, if existent. Otherwise returns null.
   * 
   * @throws NullPointerException An argument is null
   */
  IActorContext get(ActorURL url);

  /**
   * Adds a new actor context, which if successfully added is started
   * 
   * @throws NullPointerException An argument is null
   * @throws ActorAlreadyExistsException An actor with the given URL already exists
   * @throws AdapterStartException The actor's adapter could not be started 
   */
  void addAndStart(IActorContext context)
      throws ActorAlreadyExistsException, AdapterStartException;

  /**
   * Shuts down all actors and clears the registry
   */
  void shutdownAndClear();
}

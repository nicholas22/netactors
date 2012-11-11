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

import java.util.concurrent.ThreadFactory;
import netactors.nexus.ActorURL;
import netactors.nexus.IActorAdapter;

/**
 * Interface of an adapter registry, for maintaining created client/server adapters
 */
public interface IAdapterRegistry
{
  /**
   * Adds a client, returning true if successful, false otherwise
   * 
   * @throws NullPointerException An argument is null
   */
  boolean addClient(ActorURL url, IActorAdapter adapter);

  /**
   * Removes a client from the registry
   * 
   * @throws NullPointerException An argument is null
   */
  void removeClient(ActorURL url);

  /**
   * Adds a server, returning true if successful, false otherwise
   * 
   * @throws NullPointerException An argument is null
   */
  boolean addServer(ActorURL url, IActorAdapter adapter);

  /**
   * Removes a server from the registry
   * 
   * @throws NullPointerException An argument is null
   */
  void removeServer(ActorURL url);

  /**
   * Returns true if an adapter exists, false otherwise
   * 
   * @throws NullPointerException An argument is null
   */
  boolean exists(AdapterType type, ActorURL url);

  /**
   * Returns an adapter, if it exists, otherwise null is returned
   * 
   * @throws NullPointerException An argument is null
   */
  IActorAdapter get(AdapterType type, ActorURL url);

  /**
   * Getter for the adapter worker thread factory
   */
  ThreadFactory getThreadFactory();
}

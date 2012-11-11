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
package netactors.nexus;

import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.exception.UnsupportedProtocolException;
import netactors.registry.IAdapterRegistry;

/**
 * Interface of a factory capable of creating an adapter nexus, for various client/server communication types
 */
public interface IAdapterFactory
{
  /**
   * Creates a client adapter based on the protocol required
   * 
   * @throws NullPointerException An argument is null
   * @throws UnsupportedProtocolException The specified protocol is unsupported
   */
  IActorAdapter createClient(IActorConfiguration actorConfig);

  /**
   * Creates a server adapter based on the protocol required
   * 
   * @throws NullPointerException An argument is null
   * @throws UnsupportedProtocolException The specified protocol is unsupported
   */
  IActorAdapter createServer(IActorConfiguration actorConfig, IActor actor);

  /**
   * Returns the adapter registry, holding created clients/servers
   */
  IAdapterRegistry getAdapterRegistry();
}

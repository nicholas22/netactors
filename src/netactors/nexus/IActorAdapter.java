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
import netactors.exception.AdapterStartException;
import netactors.registry.AdapterType;

/**
 * Interface of an actor adapter
 */
public interface IActorAdapter
    extends IActor
{
  /**
   * Returns the type of the adapter
   */
  AdapterType getType();

  /**
   * Starts the adapter
   * 
   * @throws AdapterStartException The adapter failed to start
   */
  void start()
      throws AdapterStartException;

  /**
   * Shuts down the adapter
   */
  void shutdown();
}

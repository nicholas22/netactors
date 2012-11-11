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
import netactors.nexus.ActorURL;

/**
 * Interface of an actor
 */
public interface IActor
{
  /**
   * Gets the URL of this actor
   */
  ActorURL getUrl();

  /**
   * Called when the actor has received a message
   */
  void onReceive(Serializable message);

  /**
   * Can be used to send a message to an actor. This is an asynchronous method.
   * 
   * @throws NullPointerException An argument is null
   */
  void send(Serializable message);
}

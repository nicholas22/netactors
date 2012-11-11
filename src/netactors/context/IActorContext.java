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
package netactors.context;

import netactors.actor.IActor;
import netactors.nexus.ActorURL;
import netactors.nexus.IActorAdapter;

/**
 * Interface of an actor context
 */
public interface IActorContext
{
  /**
   * Getter for the actor URL
   */
  ActorURL getUrl();

  /**
   * Getter for the actor
   */
  IActor getActor();

  /**
   * Getter for the actor connector
   */
  IActorAdapter getConnector();
}

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.IActorSystem;
import netactors.config.IActorConfiguration;
import netactors.nexus.ActorURL;

/**
 * Abstract implementation of an actor
 */
public abstract class UntypedActor
    implements IActor
{
  @Getter(AccessLevel.PROTECTED)
  private final IActorSystem system;
  @Getter(AccessLevel.PROTECTED)
  private final IActorConfiguration config;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  protected UntypedActor(@NotNull final IActorSystem system, @NotNull final IActorConfiguration config)
  {
    this.system = system;
    this.config = config;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public final void send(@NotNull final Serializable message)
  {
    system.tell(getUrl(), message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final ActorURL getUrl()
  {
    return config.getUrl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }
}

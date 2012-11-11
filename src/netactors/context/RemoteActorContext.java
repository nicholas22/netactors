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

import lombok.Getter;
import lombok.Validate;
import lombok.Validate.NotNull;
import lombok.val;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.exception.UnsupportedProtocolException;
import netactors.nexus.ActorURL;
import netactors.nexus.IActorAdapter;
import netactors.nexus.IAdapterFactory;

/**
 * Implementation of a remote actor context
 */
public final class RemoteActorContext
    implements IActorContext
{
  private final ActorURL remoteUrl;

  @Getter
  private final IActor actor;
  @Getter
  private final IActorAdapter connector;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   * @throws UnsupportedProtocolException The specified protocol is unsupported
   */
  @Validate
  public RemoteActorContext(@NotNull final IAdapterFactory adapterFactory, @NotNull final IActorConfiguration actorConfig)
  {
    this.remoteUrl = actorConfig.getUrl();
    val adapter = adapterFactory.createClient(actorConfig);
    this.connector = adapter;
    this.actor = adapter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActorURL getUrl()
  {
    return remoteUrl;
  }
}

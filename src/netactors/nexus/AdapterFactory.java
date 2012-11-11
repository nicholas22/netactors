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

import lombok.Getter;
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.exception.UnsupportedProtocolException;
import netactors.nexus.mem.InMemoryClientAdapter;
import netactors.nexus.mem.InMemoryServerAdapter;
import netactors.nexus.tcp.TcpClientAdapter;
import netactors.nexus.tcp.TcpServerAdapter;
import netactors.nexus.udp.UdpClientAdapter;
import netactors.nexus.udp.UdpServerAdapter;
import netactors.registry.AdapterRegistry;
import netactors.registry.IAdapterRegistry;

/**
 * Implementation of the client/server adapter factory
 */
public final class AdapterFactory
    implements IAdapterFactory
{
  @Getter
  private final IAdapterRegistry adapterRegistry;

  /**
   * Default constructor
   */
  public AdapterFactory()
  {
    this(new AdapterRegistry());
  }

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public AdapterFactory(@NotNull final IAdapterRegistry adapterRegistry)
  {
    this.adapterRegistry = adapterRegistry;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public IActorAdapter createClient(@NotNull final IActorConfiguration actorConfig)
  {
    switch(actorConfig.getUrl().getProtocol())
    {
      case MEM:
        return new InMemoryClientAdapter(adapterRegistry, actorConfig);
      case TCP:
        return new TcpClientAdapter(adapterRegistry, actorConfig);
      case UDP:
        return new UdpClientAdapter(adapterRegistry, actorConfig);
      default:
        throw new UnsupportedProtocolException("Unsupported protocol: " + actorConfig.getUrl().getProtocol());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public IActorAdapter createServer(@NotNull final IActorConfiguration actorConfig, @NotNull final IActor actor)
  {
    switch(actorConfig.getUrl().getProtocol())
    {
      case MEM:
        return new InMemoryServerAdapter(adapterRegistry, actorConfig, actor);
      case TCP:
        return new TcpServerAdapter(adapterRegistry, actorConfig, actor);
      case UDP:
        return new UdpServerAdapter(adapterRegistry, actorConfig, actor);
      default:
        throw new UnsupportedProtocolException("Unsupported protocol: " + actorConfig.getUrl().getProtocol());
    }
  }
}

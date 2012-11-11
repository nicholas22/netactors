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
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.nexus.ActorURL;
import netactors.nexus.IActorAdapter;
import netactors.threading.ActorThreadFactory;
import propel.core.collections.maps.multi.ISharedMapMultimap;
import propel.core.collections.maps.multi.SharedMapMultimap;

/**
 * Implementation of the adapter registry
 */
public final class AdapterRegistry
    implements IAdapterRegistry
{
  private final ISharedMapMultimap<AdapterType, ActorURL, IActorAdapter> map = new SharedMapMultimap<AdapterType, ActorURL, IActorAdapter>() {};
  private final ThreadFactory threadFactory = new ActorThreadFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public boolean addClient(@NotNull final ActorURL url, @NotNull final IActorAdapter adapter)
  {
    return map.putIfAbsent(AdapterType.CLIENT, url, adapter) == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void removeClient(@NotNull final ActorURL url)
  {
    map.remove(AdapterType.CLIENT, url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public boolean addServer(@NotNull final ActorURL url, @NotNull final IActorAdapter adapter)
  {
    return map.putIfAbsent(AdapterType.SERVER, url, adapter) == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void removeServer(@NotNull final ActorURL url)
  {
    map.remove(AdapterType.SERVER, url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public boolean exists(@NotNull final AdapterType type, @NotNull final ActorURL url)
  {
    return map.get(type, url) != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public IActorAdapter get(@NotNull final AdapterType type, @NotNull final ActorURL url)
  {
    return map.get(type, url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ThreadFactory getThreadFactory()
  {
    return threadFactory;
  }
}

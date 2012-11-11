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
package netactors.nexus.common;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.nexus.ActorURL;
import netactors.nexus.IActorAdapter;
import netactors.registry.AdapterType;
import netactors.registry.IAdapterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation of a server adapter
 */
public abstract class AbstractServerAdapter
    implements IActorAdapter
{
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected final IAdapterRegistry adapterRegistry;
  protected final IActorConfiguration actorConfig;
  protected final IActor actor;
  protected final ExecutorService workerPool;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  protected AbstractServerAdapter(@NotNull final IAdapterRegistry adapterRegistry, @NotNull final IActorConfiguration actorConfig,
                                  @NotNull final IActor actor)
  {
    this.adapterRegistry = adapterRegistry;
    this.actorConfig = actorConfig;
    this.actor = actor;

    // create pool
    workerPool = Executors.newCachedThreadPool(adapterRegistry.getThreadFactory());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AdapterType getType()
  {
    return AdapterType.SERVER;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActorURL getUrl()
  {
    return actorConfig.getUrl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void send(@NotNull final Serializable message)
  {
    throw new IllegalStateException(this + " should never send data: " + message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + " (of " + getUrl() + ")";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    adapterRegistry.removeServer(getUrl());
    workerPool.shutdownNow();
  }
}

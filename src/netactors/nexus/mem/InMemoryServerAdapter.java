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
package netactors.nexus.mem;

import java.io.Serializable;
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.exception.AdapterStartException;
import netactors.exception.StackTraceLogging;
import netactors.nexus.common.AbstractServerAdapter;
import netactors.registry.IAdapterRegistry;
import propel.core.common.StackTraceLevel;

/**
 * Implementation of an in-memory actor server
 */
public final class InMemoryServerAdapter
    extends AbstractServerAdapter
{
  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  public InMemoryServerAdapter(final IAdapterRegistry adapterRegistry, final IActorConfiguration actorConfig, final IActor actor)
  {
    super(adapterRegistry, actorConfig, actor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start()
      throws AdapterStartException
  {
    if (!adapterRegistry.addServer(getUrl(), this))
      throw new AdapterStartException(this + " already exists");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void onReceive(@NotNull final Serializable message)
  {
    workerPool.submit(new Runnable() {
      public void run()
      {
        try
        {
          actor.onReceive(message);
        }
        catch(Exception e)
        {
          if (actorConfig.isLoggingEnabled())
            logger.info(actor + " could not process a message: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));
        }
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    try
    {
      adapterRegistry.removeServer(getUrl());
      super.shutdown();
    }
    catch(Exception e)
    {
      logger.warn(this + " shutdown caught an exception: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));
    }
  }
}

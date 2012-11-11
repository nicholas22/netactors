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

import java.util.concurrent.ConcurrentHashMap;
import lombok.Validate;
import lombok.Validate.NotNull;
import lombok.val;
import netactors.context.IActorContext;
import netactors.exception.ActorAlreadyExistsException;
import netactors.exception.AdapterStartException;
import netactors.nexus.ActorURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the actor registry
 */
public final class ActorRegistry
    implements IActorRegistry
{
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ConcurrentHashMap<ActorURL, IActorContext> actors;

  /**
   * Default constructor
   */
  public ActorRegistry()
  {
    actors = new ConcurrentHashMap<ActorURL, IActorContext>(16, 0.75f, Runtime.getRuntime().availableProcessors());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void addAndStart(@NotNull final IActorContext context)
      throws ActorAlreadyExistsException, AdapterStartException
  {
    // attempt to insert
    val url = context.getUrl();
    val prev = actors.putIfAbsent(url, context);

    // check if already present
    if (prev != null)
      throw new ActorAlreadyExistsException("Actor URL already present: " + url);

    logger.info("Starting actor context " + context.getUrl() + " (" + context.getConnector().getType() + ")");   
    try
    {
      context.getConnector().start();
    }
    catch(AdapterStartException e)
    {
      actors.remove(url, context);
      context.getConnector().shutdown();
      
      throw e;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public IActorContext get(@NotNull final ActorURL url)
  {
    return actors.get(url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdownAndClear()
  {
    // iterate over entries
    while (actors.size() > 0)
    {
      for (val entry : actors.entrySet())
      {
        // remove
        val url = entry.getKey();
        val connector = entry.getValue().getConnector();

        logger.info("Shutting down actor context " + url + " (" + connector.getType() + ")");
        actors.remove(url);

        // shutdown
        connector.shutdown();
      }

    }
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

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
package netactors;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Validate;
import lombok.Validate.NotNull;
import lombok.val;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.context.IActorContext;
import netactors.context.LocalActorContext;
import netactors.context.RemoteActorContext;
import netactors.exception.ActorAlreadyExistsException;
import netactors.exception.ActorCreationException;
import netactors.exception.AdapterStartException;
import netactors.exception.StackTraceLogging;
import netactors.nexus.ActorURL;
import netactors.nexus.AdapterFactory;
import netactors.nexus.IAdapterFactory;
import netactors.registry.ActorRegistry;
import netactors.registry.IActorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import propel.core.common.StackTraceLevel;
import propel.core.utils.ArrayUtils;
import propel.core.utils.ReflectionUtils;

/**
 * Actor system implementation
 */
public final class ActorSystem
    implements IActorSystem
{
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Getter(AccessLevel.PACKAGE)
  private final IActorConfiguration actorConfig;
  @Getter(AccessLevel.PACKAGE)
  private final IActorRegistry actorRegistry;
  @Getter(AccessLevel.PACKAGE)
  private final IAdapterFactory actorAdapterFactory;

  /**
   * Default constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public ActorSystem(@NotNull final IActorConfiguration actorConfig)
  {
    this(actorConfig, new AdapterFactory());
  }

  /**
   * Default constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public ActorSystem(@NotNull final IActorConfiguration actorConfig, @NotNull final IAdapterFactory actorAdapterFactory)
  {
    this(actorConfig, actorAdapterFactory, new ActorRegistry());
  }

  /**
   * Default constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public ActorSystem(@NotNull final IActorConfiguration actorConfig, @NotNull final IAdapterFactory actorAdapterFactory,
                     @NotNull final IActorRegistry actorRegistry)
  {
    this.actorConfig = actorConfig;
    this.actorRegistry = actorRegistry;
    this.actorAdapterFactory = actorAdapterFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public <T extends IActor> T actorOf(@NotNull final Class<T> actorType)
      throws ActorCreationException
  {
    return actorOf(actorType, new Object[0]);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public <T extends IActor> T actorOf(@NotNull final Class<T> actorType, @NotNull final Object[] args)
      throws ActorCreationException
  {
    if (actorConfig.isLoggingEnabled())
      logger.info("Creating actor " + actorType.getSimpleName());

    try
    {
      // instantiate
      T actor = null;
      try
      {
        actor = (T) ReflectionUtils.activate(actorType, ArrayUtils.join(new Object[] {this, actorConfig}, args));
      }
      catch(Exception e)
      {
        throw new ActorCreationException("Actor activation failed. Note that the actor implementation " + actorType.getSimpleName()
            + " must provide a public constructor, accepting an " + IActorSystem.class.getSimpleName() + " and an "
            + IActorConfiguration.class.getSimpleName() + " argument", e);
      }

      // create local actor context
      val context = new LocalActorContext(actorAdapterFactory, actorConfig, actor);

      // maintain and start it
      actorRegistry.addAndStart(context);

      return actor;
    }
    catch(Exception e)
    {
      throw new ActorCreationException("Actor creation failed", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public IActor actorFor(@NotNull final ActorURL url)
  {
    // attempt to retrieve a previously cached actor reference
    IActorContext context = actorRegistry.get(url);
    if (context != null)
      return context.getActor();

    // if the local actor, it is not created yet
    if (actorConfig.getUrl().equals(url))
      return null;

    // attempt to connect to remote actor
    context = new RemoteActorContext(actorAdapterFactory, actorConfig.cloneFor(url));

    // maintain and start it
    try
    {
      actorRegistry.addAndStart(context);
    }
    catch(ActorAlreadyExistsException e)
    {
      // another thread probably has already added it, attempt to get it again
      context = actorRegistry.get(url);
    }
    catch(AdapterStartException e)
    {
      // could not connect
      if (actorConfig.isLoggingEnabled())
        logger.info("Could not start remoting adapter: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));
      return null;
    }

    // if found, return it
    return context != null ? context.getActor() : null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void tell(@NotNull final ActorURL url, @NotNull final Serializable message)
  {
    val context = actorRegistry.get(url);
    if (context != null)
    {
      val connector = context.getConnector();
      switch(connector.getType())
      {
        case CLIENT:
          connector.send(message);
          break;
        case SERVER:
          connector.onReceive(message);
          break;
        default:
          throw new UnsupportedOperationException("Unsupported connector: " + connector.getType());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    actorRegistry.shutdownAndClear();
  }
}

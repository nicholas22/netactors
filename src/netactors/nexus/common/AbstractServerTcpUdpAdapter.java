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
import lombok.NoArgsConstructor;
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.exception.StackTraceLogging;
import netactors.registry.IAdapterRegistry;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import propel.core.common.StackTraceLevel;

/**
 * Abstract implementation containing structures and functionality common between TCP and UDP client adapters
 */
public abstract class AbstractServerTcpUdpAdapter
    extends AbstractServerAdapter
{
  protected final ChannelGroup group;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  protected AbstractServerTcpUdpAdapter(final IAdapterRegistry adapterRegistry, final IActorConfiguration actorConfig, final IActor actor)
  {
    super(adapterRegistry, actorConfig, actor);

    // create channel group
    group = new DefaultChannelGroup("ActorServer (" + actorConfig.getUrl() + ")");
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

  private synchronized void attemptRestart(final Throwable e)
  {
    if (actorConfig.isLoggingEnabled())
      logger.info(this + " message handler encountered an exception: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));

    // shutdown group
    try
    {
      partialShutdown();
    }
    catch(Exception sde)
    {
      if (actorConfig.isLoggingEnabled())
        logger.error(this + " has encountered an error while shutting down", sde);
    }

    // start server
    try
    {
      start();
    }
    catch(Exception ste)
    {
      // problem starting
      if (actorConfig.isLoggingEnabled())
        logger.info(this + " could not restart: " + StackTraceLogging.format(ste, StackTraceLevel.ABBREVIATED));

      // clean up
      try
      {
        partialShutdown();
      }
      catch(Exception ex)
      {
        // this is OK at this stage
      }
    }
  }

  /**
   * Shuts everything but the thread pools down
   */
  private void partialShutdown()
  {
    adapterRegistry.removeServer(getUrl());
    if (!group.isEmpty())
    {
      group.close().awaitUninterruptibly();
      group.clear();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    partialShutdown();
    super.shutdown();
  }

  /**
   * Server-side message handler, handles incoming messages by forwarding them to the actor
   */
  @NoArgsConstructor
  protected class MessageHandler
      extends SimpleChannelHandler
  {
    /**
     * {@inheritDoc}
     */
    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e)
    {
      // add opened connections to the group
      group.add(ctx.getChannel());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
        throws Exception
    {
      // handle with actor
      onReceive((Serializable) e.getMessage());

      // forward to any further handlers
      super.messageReceived(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent evt)
        throws Exception
    {
      attemptRestart(evt.getCause());
    }
  }

}

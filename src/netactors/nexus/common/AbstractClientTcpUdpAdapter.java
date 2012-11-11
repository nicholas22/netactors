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
import java.util.concurrent.RejectedExecutionException;
import lombok.NoArgsConstructor;
import lombok.Validate;
import lombok.Validate.NotNull;
import netactors.config.IActorConfiguration;
import netactors.exception.StackTraceLogging;
import netactors.registry.IAdapterRegistry;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import propel.core.common.StackTraceLevel;

/**
 * Abstract implementation containing structures and functionality common between TCP and UDP client adapters
 */
public abstract class AbstractClientTcpUdpAdapter
    extends AbstractClientAdapter
{
  protected static final int INITIAL_BUFFER_SIZE = 512;
  protected volatile Channel channel;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  protected AbstractClientTcpUdpAdapter(final IAdapterRegistry adapterRegistry, final IActorConfiguration actorConfig)
  {
    super(adapterRegistry, actorConfig);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public void send(@NotNull final Serializable message)
  {
    try
    {
      channel.write(message);
    }
    catch(Exception e)
    {
      attemptReconnect(e);
    }
  }

  /**
   * Attempts to re-open the communication channel with the server
   */
  private synchronized void attemptReconnect(final Throwable e)
  {
    try
    {
      // attempt to reconnect if channel not connected
      workerPool.submit(new Runnable() {
        public void run()
        {
          if (actorConfig.isLoggingEnabled())
            logger.info(this + " failed to write message to channel: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));

          // shutdown channel
          try
          {
            partialShutdown();
          }
          catch(Exception sde)
          {
            if (actorConfig.isLoggingEnabled())
              logger.error(this + " has encountered an error while shutting down", sde);
          }

          // start channel
          try
          {
            start();
          }
          catch(Exception ste)
          {
            // problem starting (server could be down)
            if (actorConfig.isLoggingEnabled())
              logger.info(this + " could not restart: " + StackTraceLogging.format(ste, StackTraceLevel.ABBREVIATED));

            // clean up
            try
            {
              partialShutdown();
            }
            catch(Exception e)
            {
              // this is OK at this stage
            }
          }

        }
      });
    }
    catch(RejectedExecutionException re)
    {
      if (actorConfig.isLoggingEnabled())
        logger.debug("Execution rejected, most likely due to shutdown: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));
    }
  }

  /**
   * Shuts everything exception threadpools down
   */
  private void partialShutdown()
  {
    adapterRegistry.removeClient(getUrl());
    if (channel != null)
    {
      channel.close().awaitUninterruptibly();
      channel = null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    super.shutdown();
    partialShutdown();
  }

  /**
   * Client-side message handler, does not expect any messages and attempts to reconnect upon failure
   */
  @NoArgsConstructor
  protected class MessageHandler
      extends SimpleChannelHandler
  {
    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
        throws Exception
    {
      // forward to any further handlers
      super.messageReceived(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e)
        throws Exception
    {
      attemptReconnect(e.getCause());
    }
  }

}

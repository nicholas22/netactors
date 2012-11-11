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
package netactors.nexus.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.val;
import netactors.actor.IActor;
import netactors.config.IActorConfiguration;
import netactors.exception.AdapterStartException;
import netactors.exception.StackTraceLogging;
import netactors.nexus.common.AbstractServerTcpUdpAdapter;
import netactors.registry.IAdapterRegistry;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import propel.core.common.StackTraceLevel;

/**
 * Implementation of a TCP actor server
 */
public final class TcpServerAdapter
    extends AbstractServerTcpUdpAdapter
{
  private final ExecutorService bossPool;
  private ServerBootstrap bootstrap;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  public TcpServerAdapter(final IAdapterRegistry adapterRegistry, final IActorConfiguration config, final IActor actor)
  {
    super(adapterRegistry, config, actor);

    // pool creation
    val threadFactory = adapterRegistry.getThreadFactory();
    bossPool = Executors.newCachedThreadPool(threadFactory);

    bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(bossPool, workerPool));

    // setup the pipeline factory
    bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
      public ChannelPipeline getPipeline()
          throws Exception
      {
        return Channels.pipeline(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())), new MessageHandler());
      };
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start()
      throws AdapterStartException
  {
    try
    {
      // bind and start to accept incoming connections
      val channel = bootstrap.bind(new InetSocketAddress(getUrl().getHost(), getUrl().getPort()));
      // add the server socket to the group (of all sockets)
      group.add(channel);
    }
    catch(Exception e)
    {
      throw new AdapterStartException("Could not start " + this, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    try
    {
      super.shutdown();
      bossPool.shutdownNow();
      bootstrap.releaseExternalResources();
    }
    catch(Exception e)
    {
      logger.warn(this + " shutdown caught an exception: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));
    }
  }

}

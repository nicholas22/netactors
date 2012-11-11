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
package netactors.nexus.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import lombok.val;
import netactors.config.IActorConfiguration;
import netactors.exception.AdapterStartException;
import netactors.exception.StackTraceLogging;
import netactors.nexus.common.AbstractClientTcpUdpAdapter;
import netactors.registry.IAdapterRegistry;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import propel.core.common.StackTraceLevel;

/**
 * Implementation of a UDP actor client, for interacting with a remote actor's TCP server
 */
public final class UdpClientAdapter
    extends AbstractClientTcpUdpAdapter
{
  private final ConnectionlessBootstrap bootstrap;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  public UdpClientAdapter(final IAdapterRegistry adapterRegistry, final IActorConfiguration actorConfig)
  {
    super(adapterRegistry, actorConfig);

    // client setup
    val channelFactory = new NioDatagramChannelFactory(workerPool);
    val pipelineFactory = new ChannelPipelineFactory() {
      public ChannelPipeline getPipeline()
          throws Exception
      {
        return Channels.pipeline(new ObjectEncoder(INITIAL_BUFFER_SIZE), new MessageHandler());
      }
    };

    // connection-less bootstrap
    bootstrap = new ConnectionlessBootstrap(channelFactory);
    bootstrap.setPipelineFactory(pipelineFactory);
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
      // attempt to establish connection
      val addressToConnectTo = new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
      val future = bootstrap.connect(addressToConnectTo);

      // wait interruptibly with a timeout
      future.await(actorConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);

      channel = future.getChannel();
      if (!channel.isConnected())
        throw new AdapterStartException(this + " channel not connected");
    }
    catch(Exception e)
    {
      throw new AdapterStartException(this + " could not connect to remote server", e);
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
      bootstrap.releaseExternalResources();
    }
    catch(Exception e)
    {
      logger.warn(this + " shutdown caught an exception: " + StackTraceLogging.format(e, StackTraceLevel.ABBREVIATED));
    }
  }

}

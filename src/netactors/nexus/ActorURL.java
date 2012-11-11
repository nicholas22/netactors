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

/**
 * POJO encapsulating an actor's URL
 */
public final class ActorURL
    implements Comparable<ActorURL>
{
  @Getter
  private final ProtocolType protocol;
  @Getter
  private final String host;
  @Getter
  private final int port;
  @Getter
  private final String url;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  public ActorURL(@NotNull final ProtocolType protocol, @NotNull final String host, final int port)
  {
    this.protocol = protocol;
    this.host = host;
    this.port = port;
    this.url = (protocol + "://" + host + ":" + port).toLowerCase();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ActorURL other = (ActorURL) obj;
    if (url == null)
    {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public int compareTo(@NotNull final ActorURL other)
  {
    return url.compareTo(other.getUrl());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return url;
  }
}

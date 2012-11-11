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
package netactors.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import lombok.val;

/**
 * Thread factory for netactor-specific tasks
 */
public final class ActorThreadFactory
    implements ThreadFactory
{
  private static final AtomicLong counter = new AtomicLong();

  /**
   * {@inheritDoc}
   */
  @Override
  public Thread newThread(final Runnable runnable)
  {
    val result = new Thread(runnable, "actor-worker-" + counter.incrementAndGet());
    result.setDaemon(true);
    return result;
  }

}

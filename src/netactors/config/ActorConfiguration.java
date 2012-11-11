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
package netactors.config;

import lombok.Getter;
import lombok.Validate;
import lombok.Validate.NotNull;
import lombok.val;
import netactors.nexus.ActorURL;

/**
 * POJO that holds actor configuration
 */
public final class ActorConfiguration
    implements IActorConfiguration
{
  private static final String LOGGING_ENABLED_PROPERTY = "netactorLog";
  private static final String TIMEOUT_MILLIS_PROPERTY = "netactorTimeoutMillis";

  @Getter
  private final ActorURL url;
  @Getter
  private final int timeoutMillis;
  @Getter
  private final boolean loggingEnabled;

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public ActorConfiguration(@NotNull final ActorURL url)
  {
    this(url, getDefaultTimeoutMillis());
  }

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public ActorConfiguration(@NotNull final ActorURL url, final int timeoutMillis)
  {
    this(url, timeoutMillis, getDefaultIsLoggingEnabled());
  }

  /**
   * Constructor
   * 
   * @throws NullPointerException An argument is null
   */
  @Validate
  public ActorConfiguration(@NotNull final ActorURL url, final int timeoutMillis, final boolean loggingEnabled)
  {
    this.url = url;
    this.timeoutMillis = timeoutMillis;
    this.loggingEnabled = loggingEnabled;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Validate
  public IActorConfiguration cloneFor(@NotNull final ActorURL url)
  {
    return new ActorConfiguration(url, getTimeoutMillis(), isLoggingEnabled());
  }

  /**
   * Returns a configured property, or false if property is not set
   */
  private static boolean getDefaultIsLoggingEnabled()
  {
    val value = System.getProperty(LOGGING_ENABLED_PROPERTY);
    return Boolean.parseBoolean(value);
  }

  /**
   * Returns a configured property, or a default value if property is not set
   */
  private static int getDefaultTimeoutMillis()
  {
    try
    {
      val value = System.getProperty(TIMEOUT_MILLIS_PROPERTY);
      val intValue = Integer.parseInt(value);
      if (intValue < 0)
        throw new IllegalArgumentException(intValue + " is negative");

      return intValue;
    }
    catch(Exception e)
    {
      return 2000;
    }
  }
}

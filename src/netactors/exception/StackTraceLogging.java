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
package netactors.exception;

import lombok.val;
import propel.core.common.CONSTANT;
import propel.core.common.StackTraceLevel;
import propel.core.common.StackTraceLogger;
import propel.core.utils.StringUtils;

/**
 * Exception logging helper
 */
public final class StackTraceLogging
{
  /**
   * Formats an exception message, based on the stack trace level specified
   * 
   * @throws NullPointerException An argument is null
   */
  public static String format(final Throwable e, final StackTraceLevel level)
  {
    val result = new StackTraceLogger(e, level).toString();
    return StringUtils.trim(result, CONSTANT.CRLF_CHARS);
  }

  /**
   * Private constructor
   */
  private StackTraceLogging()
  {
  }
}

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
package common;

import lombok.val;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import propel.core.common.CONSTANT;
import propel.core.utils.StringUtils;

/**
 * Super-class of all tests, logs the test being run
 */
public abstract class TestAspectDecorator
{
  private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

  @Rule
  public MethodRule watchman = new TestWatchman() {
    public void starting(FrameworkMethod method)
    {
      val msg = method.getName() + " is running...";

      logger.debug(StringUtils.repeat(CONSTANT.HYPHEN, 80));
      logger.debug(msg);
    }
  };
}

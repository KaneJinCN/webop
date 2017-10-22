/*
 * Copyright (c) 2017 Kane Jin
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package cn.kanejin.webop.core.def;

import cn.kanejin.webop.core.exception.IllegalConfigException;

import java.util.Locale;

/**
 * @author Kane Jin
 */
public class MultipartDef {
    private static final long ONE_KB = 1024L;
    private static final long ONE_MB = ONE_KB * ONE_KB;
    private static final long ONE_GB = ONE_MB * ONE_KB;

    private final Long maxSize;

    public MultipartDef(String maxSizeString) {
        this.maxSize = parseMaxSize(maxSizeString);
    }

    private Long parseMaxSize(String maxSizeString) {
        if (maxSizeString != null) {
            String number = maxSizeString.toLowerCase(Locale.ENGLISH);
            long factor = 1L;
            if (number.endsWith("g")) {
                factor = ONE_GB;
                number = number.substring(0, number.length() - 1);
            } else if (number.endsWith("m")) {
                factor = ONE_MB;
                number = number.substring(0, number.length() - 1);
            } else if (number.endsWith("k")) {
                factor = ONE_KB;
                number = number.substring(0, number.length() - 1);
            }
            try {
                return Long.parseLong(number.trim()) * factor;
            } catch (NumberFormatException e) {
                throw new IllegalConfigException("<max-size> of <multipart> error", e);
            }
        }

        return 2 * ONE_MB;
    }

    public Long getMaxSize() {
        return maxSize;
    }
}

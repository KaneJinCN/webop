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

package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.OperationStep;
import cn.kanejin.webop.core.annotation.Param;
import cn.kanejin.webop.core.annotation.StepMethod;
import org.apache.commons.fileupload.FileItem;

/**
 * @author Kane Jin
 */
public class UploadFile implements OperationStep {

    @StepMethod
    public int execute(OperationContext context,
                       @Param(name = "name") String name,
                       @Param(name = "avatar") FileItem avatar) {

        System.out.println(name);

        System.out.println(avatar.getName());
        System.out.println(avatar.getSize());

        return 0;
    }
}
package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.OperationStep;
import cn.kanejin.webop.core.annotation.Param;
import cn.kanejin.webop.core.annotation.StepMethod;
import org.apache.commons.fileupload.FileItem;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

public class ReturnActionStep implements OperationStep {

    @Resource(name = "author")
    private String author;

    @StepMethod
    public int execute(OperationContext context,
                       @Param(name = "name") String name,
                       @Param(name = "age", ifEmpty = "20") Integer age,
                       @Param(name = "dob", pattern = "yyyy-MM-dd") Date dob,
                       @Param(name = "height") Float height,
                       @Param(name = "weight") Double weight,
                       @Param(name = "male", ifEmpty = "true") Boolean isMale,
                       @Param(name = "avatar") FileItem avatar,
                       @Param(name = "followers") String[] followers,
                       @Param(name = "favoriteIds") Long[] favoriteIds,
                       @Param(name = "balance") BigDecimal balance) {

        System.out.println(author);

        System.out.println(name);
        System.out.println(age);
        System.out.println(dob);
        System.out.println(height);
        System.out.println(weight);
        System.out.println(isMale);
        System.out.println(avatar);
        System.out.println(Arrays.toString(followers));
        System.out.println(Arrays.toString(favoriteIds));
        System.out.println(balance);

        return 0;
    }
}

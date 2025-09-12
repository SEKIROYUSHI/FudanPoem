package org.example.fudanPoem.tool;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;


public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/fudanpoem?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai", "root", "050805WOAINIwlf")
                .globalConfig(builder -> {
                    builder.author("SEKIROYUSHI") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 指定输出目录
                            .disableOpenDir(); // 禁止打开输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("org.example") // 设置父包名
                            .moduleName("fudanPoem") // 设置模块名
                            .pathInfo(null); // 设置路径信息
                })
                .strategyConfig(builder -> {
                    builder.addInclude(Collections.emptyList()) // 设置需要生成的表名
                            .addTablePrefix("t_"); // 设置表前缀过滤
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 引擎
                .execute();
    }
}
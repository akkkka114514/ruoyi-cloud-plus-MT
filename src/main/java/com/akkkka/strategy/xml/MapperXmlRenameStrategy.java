package com.akkkka.strategy.xml;

import com.akkkka.Constants;
import org.dom4j.*;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.RUOYI_GROUP_ID;
import static com.akkkka.RenameConfig.MY_GROUP_ID;

/**
 * @author: akkkka114514
 * @create: 2025-07-28 13:40
 * @description: 修改mybatis mapper.xml文件中的字段
 */
public class MapperXmlRenameStrategy extends XmlRenameStrategy {
    private static final Logger logger = Logger.getLogger(MapperXmlRenameStrategy.class.getName());
    static {
        logger.setLevel(Constants.LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith("Mapper.xml");
    }

    @Override
    public void rename(File file) {
        Document document = parse(file);
        if(document!=null){
            renameNamespace(document);
            renameTypeOfResultMap(document);
            renameResultType(document);

            saveXml(file, document);
            logger.info("mybatis mapper文件:" + file.getAbsolutePath() + "重命名成功");
        }
    }

    private void renameNamespace(Document document) {
        getNodes("//mapper", document)
                .forEach(node -> {
                    Element element = (Element) node;
                    String oldNamespace = element.attribute("namespace").getValue();
                    String newNamespace = oldNamespace.replace(RUOYI_GROUP_ID, MY_GROUP_ID);
                    element.addAttribute("namespace", newNamespace);
                });
    }
    private void renameTypeOfResultMap(Document document){
        getNodes("//resultMap", document)
                .forEach(node -> {
                    Element element = (Element) node;
                    String oldType = element.attribute("type").getValue();
                    String newType = oldType.replace(RUOYI_GROUP_ID, MY_GROUP_ID);
                    element.addAttribute("type", newType);
                });
    }
    private void renameResultType(Document document){
        //任意包含resultType属性的元素
        getNodes("//*[@resultType]", document)
                .forEach(node -> {
                    Element element = (Element) node;
                    String oldResultType = element.attribute("resultType").getValue();
                    String newResultType = oldResultType.replace(RUOYI_GROUP_ID, MY_GROUP_ID);
                    element.addAttribute("resultType", newResultType);
                });

    }
}

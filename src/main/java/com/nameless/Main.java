package com.nameless;

import com.aspose.threed.ObjSaveOptions;
import com.aspose.threed.Scene;
import com.aspose.threed.TrialException;

import java.io.File;
import java.util.Scanner;

public class Main {

    // 会将这个文件夹下的所有 fbx 文件转换为 obj+mtl，同时删除原来的 fbx
    public static void convertAll(String thePath) {
        File theFile = new File(thePath);
        if (!theFile.exists()) {
            return;
        }
        if (theFile.isDirectory()) {
            File[] fileList = theFile.listFiles();
            for (File item: fileList) {
                convertAll(thePath + File.separator + item.getName());
            }
        } else if (theFile.getName().endsWith(".fbx")) {
            boolean isSuccess = convert(thePath, thePath.replace(".fbx", ".obj"));
            if (isSuccess) {
                theFile.delete();
            } else {
                System.out.println("Error: 文件转换失败：" + thePath);
            }
        }
    }

    public static boolean convert(String fbxFilePath, String objOutputPath) {
        try {
            // 在 Scene 对象中加载 FBX
            Scene document = new Scene(fbxFilePath);

            // 将 FBX 保存为 OBJ
            // 发现个问题，如果 objOutputPath 中有路径分隔符，那么 mtl 文件会丢失，很奇怪
            // 当保存到根目录下时就正常，所以这里先保存到根目录下，再移动到目标文件夹

            String objName = new File(objOutputPath).getName();
            String mtlName = objName.replace(".obj", ".mtl");
            String mtlOutputPath = objOutputPath.replace(".obj", ".mtl");

            document.save(objName, new ObjSaveOptions());
            if (!new File(objName).renameTo(new File(objOutputPath))) {
                System.out.println("移动文件失败：" + objOutputPath);
            }
            if (!new File(mtlName).renameTo(new File(mtlOutputPath))) {
                System.out.println("移动文件失败-2：" + mtlOutputPath);
            }
            return true;
        } catch (Exception e) {
            System.out.println("转换失败：" + fbxFilePath);
            System.out.println(e);
            return false;
        }
    }

    public static void main(String[] args) {
        TrialException.setSuppressTrialException(true);

        System.out.println("注意先备份文件，因为程序会在转换后删除原fbx文件！");
        System.out.println("输入文件夹路径：");
        Scanner input = new Scanner(System.in);
        String dirPath = input.nextLine();
        input.close();

        Main.convertAll(dirPath);

        System.out.println("转换完成");
    }
}
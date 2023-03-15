package com.nameless;

import com.aspose.threed.ObjSaveOptions;
import com.aspose.threed.Scene;
import com.aspose.threed.TrialException;

import java.io.File;
import java.util.Scanner;

public class Main {

    private int successNum = 0;
    private int totalNum = 0;

    // 会将这个文件夹下的所有 fbx 文件转换为 obj+mtl，同时删除原来的 fbx
    public void convertAll(String thePath) {
        File theFile = new File(thePath);
        if (!theFile.exists()) {
            return;
        }
        if (theFile.isDirectory()) {
            File[] fileList = theFile.listFiles();
            for (File item: fileList) {
                convertAll(theFile.getPath() + File.separator + item.getName());
            }
        } else if (theFile.getName().endsWith(".fbx")) {
            totalNum += 1;
            boolean isSuccess = convert(theFile.getPath(), theFile.getPath().replace(".fbx", ".obj"));
            if (isSuccess) {
                theFile.delete();
                successNum += 1;
            } else {
                System.out.println("Error: 文件转换失败：" + theFile.getPath());
            }
        }
    }

    public boolean convert(String fbxFilePath, String objOutputPath) {
        if (!fbxFilePath.endsWith(".fbx") || !objOutputPath.endsWith(".obj")) {
            return false;
        }
        try {
            Scene document = new Scene(fbxFilePath);

            String objName = new File(objOutputPath).getName();
            String mtlName = objName.replace(".obj", ".mtl");
            String mtlOutputPath = objOutputPath.replace(".obj", ".mtl");

            // 发现个问题，如果 save 时候用相对路径，则正常保存了 obj 和 mtl；如果用绝对路径，那么 mtl 文件会丢失，很奇怪
            // 所以这里先保存到程序目录下，再移动到目标路径
            document.save(objName, new ObjSaveOptions());

            if (!new File(objName).renameTo(new File(objOutputPath))) {
                System.out.println("Error: 移动obj文件失败：" + objOutputPath);
                return false;
            }
            if (!new File(mtlName).renameTo(new File(mtlOutputPath))) {
                System.out.println("Error: 移动mtl文件失败：" + mtlOutputPath);
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: 转换失败原因：" + e + "\n文件路径：" + fbxFilePath);
            return false;
        }
    }

    public void summarize() {
        System.out.println("Total: " + totalNum + ", Success: " + successNum + ".");
    }

    public static void main(String[] args) {
        TrialException.setSuppressTrialException(true);

        System.out.println("注意先备份文件，因为程序会在转换后删除原fbx文件！");
        System.out.println("输入文件或文件夹路径：");
        Scanner input = new Scanner(System.in);
        String path = input.nextLine();
        input.close();

        Main tool = new Main();
        tool.convertAll(path);
        tool.summarize();
    }
}
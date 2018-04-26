### 视频采集功能模块
视频采集功能模块，主要包含startCapture（视频采集,采集数据进行处理)，stopCapture （停止采集）

### 集成说明
直接将srpaas_capture目录下的.jar文件拷贝到application项目的libs目录下，并将在app目录下的build.gradle中配置：

dependencies {

    compile fileTree(include: ['*.jar'], dir: 'libs')

    compile files('libs/srpaas_capture_v0.1.0.jar') //对应libs中的jar文件
    ##//如果编译出错，将上面的配置修改为
    provided fileTree(include: ['*.jar'], dir: 'libs')
}

## 注意
如果项目中引用该模块编译出错，需要修改项目路径下的build.gradle 脚本文件：
 classpath 'com.android.tools.build:gradle:xxx' 加注释


 ##打包命令
  chmod +x gradlew
  项目根目录下执行：./gradlew makeJar




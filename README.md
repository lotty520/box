## box 1.0.5
### 一款基于gradle插件实现对android项目中的字符串加密项目
项目基于TransformApi和Gradle Plugin，支持`gradle1.5.0`及以上版本

插件Groovy 依赖路径为：`com.github.box:plugin:1.0.5`

加解密库Groovy 依赖路径为：`com.github.box:string:1.0.5`

插件名称：`encryption `

### 集成方式
#### 项目已经上传到jcenter，通过gradle 插件集成
在项目的根目录`build.gradle`中添加插件依赖

```
dependencies {
    classpath 'com.android.tools.build:gradle:3.5.3'
    // 插件路径
    classpath "com.github.box:plugin:1.0.5"
    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
  }
```

#### Application 以及 Module 集成
在待集成模块的`build.gradle`中引入插件即可

```
apply plugin: 'encryption'
```


#### sdk集成
在待集成模块的`build.gradle`中引入插件

```
apply plugin: 'encryption'
```

引入加解密Lib，有两种方式：

1. 显示通知app方集成Lib:
```
implementation 'com.github.box:string:1.0.5'
```

需要注意的是，Lib版本应该与Plugin版本保持一致

2. 将加解密库文件Jar打包到SDK的`libs`中，添加运行时依赖：

```
  runtimeOnly files('libs/string-1.0.5.jar')

```
文件位置：项目目录`app-lib/libs`中。具体集成方式可以参照项目中module `app-lib` 的集成方式
#### 插件支持扩展配置

```
stringExt {
  encType = "base64"
  exclude = ["androidx"]
  include = ["com.github.boxapp"]
}
```

配置说明：

字段 | 类型|说明 
----|----|----
encType|字符串|加密类型：目前支持：base64、hex、xor、aes
exclude|字符数组|声明不参加加密的类名路径：startWith匹配规则
include|字符数组|声明强制参与加密的类名路径：startWith匹配规则

### 加密规则说明
1. Base64 编码采用的NO_WRAP模式，且解密方法是通过反射调用的`android.util.Base64`下的静态方法，所以不适用通用java项目(主要是因为java base64api 在android sdk api26 以后才支持)
2. Hex 16进制编码会将原始字符串的体积增倍，不建议使用
3. Xor 异或加密采用的8位随机秘钥，一次一密
4. Aes 加密采用的16位随机秘钥 和 16位随机IV，一次一密

### License
```
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
```
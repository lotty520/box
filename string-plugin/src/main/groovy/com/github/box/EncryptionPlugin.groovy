package com.github.box

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class EncryptionPlugin extends Transform implements Plugin<Project> {

    def app
    def lib

    private final static String EXT = "stringExt"

    private Project mProject;

    @Override
    void apply(Project project) {
        println("=======================")
        println("      box插件      ")
        println("=======================")

        if (!project.android) {
            throw new IllegalStateException(
                    'Must apply \'com.android.application\' or \'com.android.library\' first!')
        }
        mProject = project

        app = project.plugins.withType(AppPlugin)
        lib = project.plugins.withType(LibraryPlugin)
        if (!app && !lib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }
        project.extensions.create(EXT, PluginConfig)
        if (app) {
            project.extensions.getByType(AppExtension).registerTransform(this)
        } else {
            project.extensions.getByType(LibraryExtension).registerTransform(this)
        }
    }

    @Override
    String getName() {
        return "encryption"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        if (lib) {
            return TransformManager.PROJECT_ONLY
        }
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        println("==============================")
        println("      transform begin      ")
        println("===============================")
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider

        PluginConfig config = mProject.extensions.getByName(EXT)
        println("===============config below================")
        println("include: " + (config.include == null ? "[]" : config.include))
        println("encType: " + (config.encType == null ? "" : config.encType))
        println("pkg: " + (config.pkg == null ? "" : config.pkg))
        println("method: " + (config.method == null ? "" : config.method))
        println("logOpen: " + config.logOpen)
        //删除之前的输出
        if (outputProvider != null) {
            outputProvider.deleteAll()
        }
        //遍历inputs
        inputs.each { TransformInput input ->
            //遍历directoryInputs
            input.directoryInputs.each { DirectoryInput directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider, config)
            }

            //遍历jarInputs
            input.jarInputs.each { JarInput jarInput ->
                handleJarInputs(jarInput, outputProvider, config)
            }
        }
    }

    /**
     * 处理文件目录下的class文件
     */
    private static void handleDirectoryInput(DirectoryInput directoryInput,
                                             TransformOutputProvider outputProvider, PluginConfig config) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                if (file.isFile() && checkClassInDir(name, file.absolutePath, config)) {
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new InjectClassVisitor(classWriter, config)
                    classReader.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG)
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream writer = new FileOutputStream(file.absolutePath)
                    //覆写
                    writer.write(code)
                    writer.close()
                }
            }
        }
        // 将注入完成的目录拷贝到编译的结果输出目录
        def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                directoryInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    /**
     * 处理Jar中的class文件
     */
    private static void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider,
                                        PluginConfig config) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            // 创建新的jar文件
            File jar = new File(jarInput.file.getParent() + File.separator +
                    "classes_posture" +
                    System.currentTimeMillis() +
                    ".jar")
            //避免上次的缓存被重复插入
            if (jar.exists()) {
                jar.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jar))
            //用于保存
            JarFile originJar = new JarFile(jarInput.file)
            Enumeration enumeration = originJar.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry inEntry = (JarEntry) enumeration.nextElement()
                //名称
                String name = inEntry.getName()
                //创建zipEntry对象
                ZipEntry outEntry = new ZipEntry(name)
                //获取当前entry的输入流
                InputStream inputStream = originJar.getInputStream(inEntry)
                //将当前entry添加到临时文件中
                jarOutputStream.putNextEntry(outEntry)
                //插桩class
                if (checkClassInJar(name, config)) {
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new InjectClassVisitor(classWriter, config)
                    //原始的CR接收CW的访问，开始重排字节码
                    classReader.accept(cv, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG)
                    //回写
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    // 直接拷贝，不插桩
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            originJar.close()
            //重命名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            //替换输出
            def dest = outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes,
                    Format.JAR)
            FileUtils.copyFile(jar, dest)
            jar.delete()
        }
    }

    private static boolean checkClassInJar(String name, PluginConfig config) {
        //eg: file in jar like: com/github/applib/Lib.class
        String dotClassName = name.replace("/", ".")
        boolean shoot = false;
        if (isClassFile(dotClassName)) {
            config.include.each { String configEle ->
                if (dotClassName.startsWith(configEle)) {
                    if (config.logOpen) {
                        println("file in jar like: " + name)
                    }
                    shoot = true
                    return
                }
            }
        }
        return shoot
    }

    private static boolean checkClassInDir(String name, String path, PluginConfig config) {
        //eg: file in dir/module like: /Users/lotty/android/github/box/app/build/intermediates/classes/debug/com/github/boxapp/StringPool.class::StringPool.class
        String dotClassName = name.replace("/", ".")
        String absPath = path.replace("/", ".")
        boolean shoot = false
        if (isClassFile(dotClassName)) {
            config.include.each { String configEle ->
                if (absPath.contains(configEle)) {
                    if (config.logOpen) {
                        println("file in dir/module shoot: " + path)
                    }
                    shoot = true
                    return
                }
            }
        }
        return shoot
    }

    private static boolean isClassFile(String cls) {
        return cls != null && cls.endsWith(".class")
    }

}

package javaprocessor.javaprocessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.squareup.javawriter.JavaWriter;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

//@SupportedAnnotationTypes({ "*" })
public class DataAnnotationProcessor extends AbstractProcessor {
    private Messager messager; // 用于打印日志
    private Elements elementUtils; // 用于处理元素
    private Filer filer; // 用来创建java文件或者class文件

    private Trees trees;
    private TreeMaker treeMaker;
    private com.sun.tools.javac.util.Name.Table names;
    private Context context;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();

        trees = Trees.instance(processingEnv);
        context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Data1.class.getCanonicalName());
        return Collections.unmodifiableSet(set);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        
        System.out.println("ddddddddddddddddd");
        
        System.out.println("cccccccccccccc:cl:" + this);
       
        if (roundEnv.processingOver()) {
            System.out.println("===============a");
        } else {
            System.out.println("===============b");
        }
        
        System.out.println("ddddddddddddddddd");
        
        messager.printMessage(Diagnostic.Kind.NOTE, "-----开始自动生成源代码");
        try {
            // 标识符
            boolean isClass = false;
            // 类的全限定名
            String classAllName = null;
            // 返回被注释的节点
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Data1.class);
            Element element = null;
            for (Element e : elements) {
                // 如果注释在类上
                if (e.getKind() == ElementKind.CLASS && e instanceof TypeElement) {
                    TypeElement t = (TypeElement) e;
                    isClass = true;
                    classAllName = t.getQualifiedName().toString();
                    element = t;
                    JCTree tree = ((JavacElements) elementUtils).getTree(e);
                    tree.accept(new TreeTranslator() {
                        @Override
                        public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                            java.util.List<JCTree> jcTrees = new ArrayList<JCTree>();
                            // 生成参数 例如：private String age;
                            JCTree.JCVariableDecl jcVariableDecl = treeMaker.VarDef(treeMaker.Modifiers(Flags.PRIVATE),
                                    names.fromString("age"), treeMaker.Ident(names.fromString("String")), null);
                            jcTrees.add(jcVariableDecl);

                            JCTree.JCMethodDecl test = getTest1();
                            jcTrees.add(test);

                            JCTree.JCMethodDecl test2 = getTest2();

                            jcTrees.add(test2);

                            JCTree.JCMethodDecl test3 = getTest3();
                            jcTrees.add(test3);

                            jcTrees.forEach(jcTree -> {
                                messager.printMessage(Diagnostic.Kind.NOTE, jcTree.getTree().toString());
                                jcClassDecl.defs = jcClassDecl.defs.prepend(jcTree);
                            });

                            super.visitClassDef(jcClassDecl);
                        }
                    });

                    // treeMaker.VarDef(treeMaker.Modifiers(Flags.PRIVATE), names.fromString("name"),
                    // treeMaker.Ident(names.fromString("String")), treeMaker.Literal("BuXueWuShu"));
                    break;
                }
            }
            // 未在类上使用注释则直接返回，返回false停止编译
            if (!isClass) {
                return true;
            }
            // 返回类内的所有节点
            List<? extends Element> enclosedElements = element.getEnclosedElements();
            // 保存字段的集合
            Map<TypeMirror, Name> fieldMap = new HashMap<>();
            for (Element ele : enclosedElements) {
                if (ele.getKind() == ElementKind.FIELD) {
                    // 字段的类型
                    TypeMirror typeMirror = ele.asType();
                    // 字段的名称
                    Name simpleName = ele.getSimpleName();
                    fieldMap.put(typeMirror, simpleName);
                }
            }
            // classAllName = classAllName + "Test";

            System.out.println("=====================" + classAllName);
            // 生成一个Java源文件

            // classAllName += "Cccccccccccc";

            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "zjtestabc");

            String path11 = resource.toUri().getPath();
            messager.printMessage(Diagnostic.Kind.NOTE, "-----path11:" + path11);

            System.out.println("=====================path11" + path11);

            Writer openWriter = resource.openWriter();
            BufferedWriter bufferedWriter = new BufferedWriter(openWriter);
            bufferedWriter.append("ccccccc");
            bufferedWriter.close();
            openWriter.close();

            // filer.createResource(StandardLocation.SOURCE_PATH, pkg, relativeName, originatingElements)

            // FileObject resource2 =
            // filer.getResource(StandardLocation.SOURCE_PATH, "", "sessiontest/sessiontest/qq/Tetttttt.java");
            // String path2222 = resource2.toUri().getPath();
            // System.out.println("=====================path2222" + path2222);
            //
            // createSourceFile(classAllName, fieldMap, resource2.openWriter());
            // compile(resource2.toUri().getPath());
            // FileObject resource3 = filer.getResource(StandardLocation.CLASS_PATH, "",
            // "sessiontest.sessiontest.qq.Tetttttt");
            // String path44 = resource3.toUri().getPath();
            // System.out.println("=====================path44" + path44);

            // BufferedWriter bufferedWriter2 = new BufferedWriter(openWriter2);
            // bufferedWriter2.append("package sessiontest.sessiontest.qq;\r\n" +
            // "\r\n" +
            // "import javaprocessor.javaprocessor.DataAnnotationProcessor.Data1;\r\n" +
            // "\r\n" +
            // "@Data1\r\n" +
            // "public class Tetttttt {\r\n" +
            // " public String abc;\r\n" +
            // " public String dddddddddddd;\r\n" +
            // "\r\n" +
            // "}");

            // System.out.println("cccccccccccccc");
            // bufferedWriter2.close();

            // messager.printMessage(Diagnostic.Kind.NOTE, "-----path2222:" + path2222);
            // System.out.println("=====================path2222" + path2222);
            // JavaFileObject sourceFile = filer.createSourceFile(getClassName(classAllName));
            // String path = sourceFile.toUri().getPath();
            // messager.printMessage(Diagnostic.Kind.NOTE, "-----path:" + path);
            // // 写入代码
            // createSourceFile(classAllName, fieldMap, sourceFile.openWriter());
            // // 手动编译
            // compile(sourceFile.toUri().getPath());

            // BufferedWriter bufferedWriter2 = new BufferedWriter(new
            // FileWriter("E:\\wrkt\\sessiontest\\src\\main\\java\\sessiontest\\sessiontest\\qq\\Tetttttt.java"));
            // bufferedWriter2.append("package sessiontest.sessiontest.qq;\r\n" +
            // "\r\n" +
            // "import javaprocessor.javaprocessor.DataAnnotationProcessor.Data1;\r\n" +
            // "\r\n" +
            // "@Data1\r\n" +
            // "public class Tetttttt {\r\n" +
            // " \r\n" +
            // " public String abcd;\r\n" +
            // "\r\n" +
            // "}\r\n" +
            // "");
            // bufferedWriter2.close();

        } catch (Exception e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, "-----errorrrrrrr1111111");
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "-----完成自动生成源代码end");
        return false;
    }

    private JCTree.JCMethodDecl getTest2() {
        /*
         * 无参无返回值的方法生成 public void test2(String name){ name = "xxxx"; }
         */

        ListBuffer<JCTree.JCStatement> testStatement2 = new ListBuffer<>();
        testStatement2.append(
                treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(names.fromString("name")), treeMaker.Literal("xxxx"))));
        JCTree.JCBlock testBody2 = treeMaker.Block(0, testStatement2.toList());

        // 生成入参
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("name"),
                treeMaker.Ident(names.fromString("String")), null);
        com.sun.tools.javac.util.List<JCTree.JCVariableDecl> parameters = com.sun.tools.javac.util.List.of(param);

        JCTree.JCMethodDecl test2 = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), // 方法限定值
                names.fromString("test2"), // 方法名
                treeMaker.Type(new Type.JCVoidType()), // 返回类型
                com.sun.tools.javac.util.List.nil(), parameters, // 入参
                com.sun.tools.javac.util.List.nil(), testBody2, null);
        return test2;
    }

    private JCTree.JCMethodDecl getTest3() {
        /*
         * 有参有返回值 public String test3(String name){ return name; }
         */

        ListBuffer<JCTree.JCStatement> testStatement3 = new ListBuffer<>();
        testStatement3.append(treeMaker.Return(treeMaker.Ident(names.fromString("name"))));
        JCTree.JCBlock testBody3 = treeMaker.Block(0, testStatement3.toList());

        // 生成入参
        JCTree.JCVariableDecl param3 = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("name"),
                treeMaker.Ident(names.fromString("String")), null);
        com.sun.tools.javac.util.List<JCTree.JCVariableDecl> parameters3 = com.sun.tools.javac.util.List.of(param3);

        JCTree.JCMethodDecl test3 = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), // 方法限定值
                names.fromString("test4"), // 方法名
                treeMaker.Ident(names.fromString("String")), // 返回类型
                com.sun.tools.javac.util.List.nil(), parameters3, // 入参
                com.sun.tools.javac.util.List.nil(), testBody3, null);
        return test3;
    }

    /*
     * 无参无返回值的方法生成 public void test(){
     * 
     * }
     */
    // 定义方法体

    private JCTree.JCMethodDecl getTest1() {
        ListBuffer<JCTree.JCStatement> testStatement = new ListBuffer<>();
        JCTree.JCBlock testBody = treeMaker.Block(0, testStatement.toList());

        JCTree.JCMethodDecl test = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), // 方法限定值
                names.fromString("test1"), // 方法名
                treeMaker.Type(new Type.JCVoidType()), // 返回类型
                com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(),
                com.sun.tools.javac.util.List.nil(), testBody, // 方法体
                null);
        return test;
    }

    private void createSourceFile(String className, Map<TypeMirror, Name> fieldMap, Writer writer) throws IOException {
        JavaWriter jw = new JavaWriter(writer);
        jw.emitPackage(getPackage(className));
        Set<javax.lang.model.element.Modifier> of = EnumSet.of(javax.lang.model.element.Modifier.PUBLIC);
        jw.beginType(getClassName(className), "class", of);
        for (Map.Entry<TypeMirror, Name> map : fieldMap.entrySet()) {
            String type = map.getKey().toString();
            String name = map.getValue().toString();
            // 字段
            jw.emitField(type, name, EnumSet.of(javax.lang.model.element.Modifier.PRIVATE));
        }
        for (Map.Entry<TypeMirror, Name> map : fieldMap.entrySet()) {
            String type = map.getKey().toString();
            String name = map.getValue().toString();
            // getter
            jw.beginMethod(type, "get" + humpString(name), of).emitStatement("return " + name).endMethod();
            // setter
            jw.beginMethod("void", "set" + humpString(name), of, type, "arg").emitStatement("this." + name + " = arg")
                    .endMethod();
        }
        jw.endType().close();
    }

    /**
     * 编译文件
     * 
     * @param path
     * @throws IOException
     */
    private void compile(String path) throws IOException {
        // 拿到编译器
        JavaCompiler complier = ToolProvider.getSystemJavaCompiler();
        // 文件管理者
        StandardJavaFileManager fileMgr = complier.getStandardFileManager(null, null, null);
        // 获取文件
        Iterable units = fileMgr.getJavaFileObjects(path);
        // 编译任务
        JavaCompiler.CompilationTask t = complier.getTask(null, fileMgr, null, null, null, units);
        // 进行编译
        t.call();
        fileMgr.close();
    }

    /**
     * 驼峰命名
     *
     * @param name
     * @return
     */
    private String humpString(String name) {
        String result = name;
        if (name.length() == 1) {
            result = name.toUpperCase();
        }
        if (name.length() > 1) {
            result = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return result;
    }

    /**
     * 读取类名
     * 
     * @param name
     * @return
     */
    private String getClassName(String name) {
        String result = name;
        if (name.contains(".")) {
            result = name.substring(name.lastIndexOf(".") + 1);
        }
        return result;
    }

    /**
     * 读取包名
     * 
     * @param name
     * @return
     */
    private String getPackage(String name) {
        String result = name;
        if (name.contains(".")) {
            result = name.substring(0, name.lastIndexOf("."));
        } else {
            result = "";
        }
        return result;
    }

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Data1 {

    }

    public static void main(String[] args) throws Exception {
        BufferedWriter bufferedWriter = new BufferedWriter(
                new FileWriter("E:/wrkt/sessiontest/src/main/java/sessiontest/sessiontest/qq/Tetttttt.java"));

        bufferedWriter.append("abc");
        bufferedWriter.flush();
        bufferedWriter.close();

    }

    private class Inliner extends TreeTranslator {

        @Override
        public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
            super.visitMethodDef(jcMethodDecl);
            if (jcMethodDecl.getName().toString().equals("getUserName")) {
                JCTree.JCMethodDecl methodDecl =
                        treeMaker.MethodDef(jcMethodDecl.getModifiers(), names.fromString("testMethod"),
                                jcMethodDecl.restype, jcMethodDecl.getTypeParameters(), jcMethodDecl.getParameters(),
                                jcMethodDecl.getThrows(), jcMethodDecl.getBody(), jcMethodDecl.defaultValue);
                result = methodDecl;
            }
        }
    }

    private java.util.List<JCTree> generateParameters() {
        java.util.List<JCTree> jcTrees = new ArrayList<>();

        // 生成参数 例如：private String age;
        JCTree.JCVariableDecl jcVariableDecl = treeMaker.VarDef(treeMaker.Modifiers(Flags.PRIVATE),
                names.fromString("age"), treeMaker.Ident(names.fromString("String")), null);

        /*
         * 生成方法 例如：public void getAge(){ String name = "BuXueWuShu"; return this.age; }
         */
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        JCTree.JCNewClass combatJCTreeMain = treeMaker.NewClass(null, com.sun.tools.javac.util.List.nil(), // 泛型参数列表
                treeMaker.Ident(names.fromString("CombatJCTreeMain")), // 创建的类名
                com.sun.tools.javac.util.List.nil(), // 参数列表
                null);
        JCTree.JCVariableDecl jcVariableDecl1 =
                treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("combatJCTreeMain"),
                        treeMaker.Ident(names.fromString("CombatJCTreeMain")), combatJCTreeMain);
        statements.append(jcVariableDecl1);
        // 创建一个方法调用 combatJCTreeMain.test();

        JCTree.JCExpressionStatement exec = treeMaker.Exec(treeMaker.Apply(com.sun.tools.javac.util.List.nil(),
                treeMaker.Select(treeMaker.Ident(names.fromString("combatJCTreeMain")), // . 左边的内容
                        names.fromString("test") // . 右边的内容
                ), com.sun.tools.javac.util.List.nil()));
        statements.append(exec);

        // 创建一个方法调用 combatJCTreeMain.test2("hello world!");
        JCTree.JCExpressionStatement exec2 = treeMaker.Exec(treeMaker.Apply(com.sun.tools.javac.util.List.nil(),
                treeMaker.Select(treeMaker.Ident(names.fromString("combatJCTreeMain")), // . 左边的内容
                        names.fromString("test2") // . 右边的内容
                ), com.sun.tools.javac.util.List.of(treeMaker.Literal("hello world!"))));
        statements.append(exec2);

        // 定义发方法体 i++
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("i"),
                treeMaker.Ident(names.fromString("Integer")), treeMaker.Literal(1)));
        statements.append(treeMaker.Exec(treeMaker.Unary(JCTree.Tag.PREINC, treeMaker.Ident(names.fromString("i")))));
        // 定义方法体 add = "a"+"b"
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("add"),
                treeMaker.Ident(names.fromString("String")), null));
        statements.append(treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(names.fromString("add")),
                treeMaker.Binary(JCTree.Tag.PLUS, treeMaker.Literal("a"), treeMaker.Literal("b")))));
        // add += "test"
        statements.append(treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG,
                treeMaker.Ident(names.fromString("add")), treeMaker.Literal("test"))));
        // 定义方法体 String name = "BuXueWuShu"
        statements.append(treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("name"),
                treeMaker.Ident(names.fromString("String")), treeMaker.Literal("BuXueWuShu")));

        /*
         * 创建一个if语句 if("BuXueWuShu".equals(name)){ add = "a" + "b"; }else{ add += "test"; }
         */
        // "BuXueWuShu".equals(name)
        JCTree.JCMethodInvocation apply =
                treeMaker.Apply(com.sun.tools.javac.util.List.nil(), treeMaker.Select(treeMaker.Literal("BuXueWuShu"), // .
                                                                                                                       // 左边的内容
                        names.fromString("equals") // . 右边的内容
                ), com.sun.tools.javac.util.List.of(treeMaker.Ident(names.fromString("name"))));
        // add = "a" + "b"
        JCTree.JCExpressionStatement exec3 = treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(names.fromString("add")),
                treeMaker.Binary(JCTree.Tag.PLUS, treeMaker.Literal("a"), treeMaker.Literal("b"))));
        // add += "test"
        JCTree.JCExpressionStatement exec1 = treeMaker.Exec(treeMaker.Assignop(JCTree.Tag.PLUS_ASG,
                treeMaker.Ident(names.fromString("add")), treeMaker.Literal("test")));

        JCTree.JCIf anIf = treeMaker.If(apply, // if语句里面的判断语句
                exec3, // 条件成立的语句
                exec1 // 条件不成立的语句
        );

        statements.append(anIf);

        // 定义方法体 return this.age
        statements.append(
                treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), names.fromString("age"))));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        // 组成方法，第一个参数意思是public，第二个参数是方法名getAge，第三个参数是方法返回类型String
        JCTree.JCMethodDecl jcMethodDecl =
                treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), names.fromString("getAge"),
                        treeMaker.Ident(names.fromString("String")), com.sun.tools.javac.util.List.nil(),
                        com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(), body, null);

        /*
         * 无参无返回值的方法生成 public void test(){ }
         */
        // 定义方法体
        ListBuffer<JCTree.JCStatement> testStatement = new ListBuffer<>();
        JCTree.JCBlock testBody = treeMaker.Block(0, testStatement.toList());

        JCTree.JCMethodDecl test = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), // 方法限定值
                names.fromString("test"), // 方法名
                treeMaker.Type(new Type.JCVoidType()), // 返回类型
                com.sun.tools.javac.util.List.nil(), com.sun.tools.javac.util.List.nil(),
                com.sun.tools.javac.util.List.nil(), testBody, null);

        /*
         * 无参无返回值的方法生成 public void test2(String name){ name = "xxxx"; }
         */
        ListBuffer<JCTree.JCStatement> testStatement2 = new ListBuffer<>();
        testStatement2.append(
                treeMaker.Exec(treeMaker.Assign(treeMaker.Ident(names.fromString("name")), treeMaker.Literal("xxxx"))));
        JCTree.JCBlock testBody2 = treeMaker.Block(0, testStatement2.toList());

        // 生成入参
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("name"),
                treeMaker.Ident(names.fromString("String")), null);
        com.sun.tools.javac.util.List<JCTree.JCVariableDecl> parameters = com.sun.tools.javac.util.List.of(param);

        JCTree.JCMethodDecl test2 = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), // 方法限定值
                names.fromString("test2"), // 方法名
                treeMaker.Type(new Type.JCVoidType()), // 返回类型
                com.sun.tools.javac.util.List.nil(), parameters, // 入参
                com.sun.tools.javac.util.List.nil(), testBody2, null);

        /*
         * 有参有返回值 public String test3(String name){ return name; }
         */

        ListBuffer<JCTree.JCStatement> testStatement3 = new ListBuffer<>();
        testStatement3.append(treeMaker.Return(treeMaker.Ident(names.fromString("name"))));
        JCTree.JCBlock testBody3 = treeMaker.Block(0, testStatement3.toList());

        // 生成入参
        JCTree.JCVariableDecl param3 = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("name"),
                treeMaker.Ident(names.fromString("String")), null);
        com.sun.tools.javac.util.List<JCTree.JCVariableDecl> parameters3 = com.sun.tools.javac.util.List.of(param3);

        JCTree.JCMethodDecl test3 = treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), // 方法限定值
                names.fromString("test4"), // 方法名
                treeMaker.Ident(names.fromString("String")), // 返回类型
                com.sun.tools.javac.util.List.nil(), parameters3, // 入参
                com.sun.tools.javac.util.List.nil(), testBody3, null);

        jcTrees.add(jcVariableDecl);
        jcTrees.add(test);
        jcTrees.add(test2);
        jcTrees.add(test3);
        jcTrees.add(jcMethodDecl);
        return jcTrees;
    }

}

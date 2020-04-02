package com.grsdev7.covid19.Covid19Soln1.utils;

import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.asm.*;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import static org.springframework.asm.ClassWriter.COMPUTE_FRAMES;
import static org.springframework.asm.Opcodes.*;


@Slf4j
public class ClassUtils {

    public static void main(String[] args) throws IOException {
        //makeMutableProxy(Request.class);

        ClassReader classReader = new ClassReader(Request.class.getName());

        ClassWriter classWriter = new ClassWriter(classReader, COMPUTE_FRAMES);

        /*ClassVisitor fieldVisitor = new AddFieldAdapter(Type.getType(Instant.class).toString(),
                "aNewBooleanField", ACC_PUBLIC, classWriter);*/

        ClassVisitor addMethodVisitor = new AddGetter(classWriter);

        classReader.accept(addMethodVisitor, 0);

        byte[] bytes = classWriter.toByteArray();

        Files.write(Paths.get("test.java"), bytes, StandardOpenOption.CREATE_NEW);

    }

    public static <T> void makeMutableProxy(Class<T> type) {
        CustomClassCreator customClassCreator = null;
        try {
            customClassCreator = new CustomClassCreator(type);
            FieldVisitor createdOn = customClassCreator.getFieldVisitor("createdOn", Type.getDescriptor(Instant.class));
            //log.info("CreatedOn : {}", createdOn.visitAttribute(new Attribute("field")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //DynamicClassLoader.newInstance().defineClass(getNewClassName(type), customClassCreator.getNewClassAsByteArray());
    }

    private static <T> String getNewClassName(Class<T> type) {
        return type.getSimpleName() + "Mutable";
    }

    private static Request createProxy() {
        Callback callBack = new MethodInterceptor() {
            @Override
            public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                return methodProxy.invokeSuper(object, args);
            }
        };
        Object proxyObject = Enhancer.create(Request.class, callBack);
        return (Request) proxyObject;
    }

    private static Sort getSortBy() {
        return
                Sort.sort(createProxy().getClass())
                        .by(Request::getCreatedOn)
                        .descending();
    }


}

class DynamicClassLoader extends ClassLoader {

    public static DynamicClassLoader newInstance() {
        return new DynamicClassLoader();
    }

    public Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}

@Slf4j
class CustomClassCreator {
    private final ClassWriter classWriter;
    private AddFieldAdapter addFieldAdapter;
    private PublicizeMethodAdapter pubMethAdapter;
    private final String className;
    private final static String CLONEABLE = "java/lang/Cloneable";

    CustomClassCreator(Class type) throws IOException {
        this.className = type.getName();
        log.debug("Creating new class from className : " + className);
        try {
            ClassReader classReader = new ClassReader(className);
            log.debug("ClassReader : {}", classReader);
            this.classWriter = new ClassWriter(classReader, COMPUTE_FRAMES);
            log.debug("Classwrite created : {}", classWriter);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    public FieldVisitor getFieldVisitor(String name, String descriptor) {
        return classWriter.visitField(ACC_PRIVATE, name, descriptor, null, null);
    }

    public void addField() {
        // addFieldAdapter = new AddFieldAdapter("aNewBooleanField", ACC_PUBLIC, writer);
        //classWriter.accept(addFieldAdapter, 0);
        //classWriter.
    }

 /*   public byte[] publicizeMethod() {
        pubMethAdapter = new PublicizeMethodAdapter(classWriter);
        classReader.accept(pubMethAdapter, 0);
        return classWriter.toByteArray();
    }*/


    public byte[] getNewClassAsByteArray() {
        addField();
        byte[] bytes = classWriter.toByteArray();
        log.debug("New class byte [] : {}", bytes);
        return bytes;
    }
}

@Slf4j
class AddGetter extends ClassVisitor {
    PrintWriter pw = new PrintWriter(System.out);

    public AddGetter(ClassVisitor cv) {
        super(ASM7, cv);
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {

        if (name.equals("")) {
            log.info("Visiting unsigned method");
            //return tracer.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, signature, exceptions);
        }
        //return tracer.visitMethod(access, name, desc, signature, exceptions);
        return null;
    }

    public void visitEnd() {
        // tracer.visitEnd();
        // System.out.println(tracer.p.getText());
    }

}
@Slf4j
class PublicizeMethodAdapter extends ClassVisitor {
    PrintWriter pw = new PrintWriter(System.out);

    public PublicizeMethodAdapter(ClassVisitor cv) {
        super(ASM4, cv);
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {

        if (name.equals("toUnsignedString0")) {
            log.info("Visiting unsigned method");
            //return tracer.visitMethod(ACC_PUBLIC + ACC_STATIC, name, desc, signature, exceptions);
        }
        //return tracer.visitMethod(access, name, desc, signature, exceptions);
        return null;
    }

    public void visitEnd() {
        // tracer.visitEnd();
        // System.out.println(tracer.p.getText());
    }

}

class AddFieldAdapter extends ClassVisitor {
    private String fieldType;
    private String fieldName;
    private String fieldDefault;
    private int access = ACC_PUBLIC;
    private boolean isFieldPresent;

    public AddFieldAdapter(
            String fieldType, String fieldName, int fieldAccess, ClassVisitor cv) {
        super(ASM7, cv);
        this.cv = cv;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.access = fieldAccess;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (name.equals(fieldName)) {
            isFieldPresent = true;
        }
        return cv.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitEnd() {
        if (!isFieldPresent) {
            FieldVisitor fv = cv.visitField(
                    access, fieldName, fieldType, null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }

}

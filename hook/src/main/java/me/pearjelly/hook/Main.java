package me.pearjelly.hook;

import com.saurik.substrate.MS;

import java.lang.reflect.Method;

/**
 * Created by hxb on 2016/5/18.
 */
public class Main {
    static void initialize() {
        MS.hookClassLoad("android.content.res.Resources", new MS.ClassLoadHook() {
            public void classLoaded(Class<?> resources) {
                Method getColor;
                try {
                    getColor = resources.getMethod("getColor", Integer.TYPE);
                } catch (NoSuchMethodException e) {
                    getColor = null;
                }

                if (getColor != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();

                    MS.hookMethod(resources, getColor, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args)
                                throws Throwable {
                            int color = (Integer) old.invoke(resources, args);
                            return color & ~0x0000ff00 | 0x00ff0000;
                        }
                    }, old);
                }
            }
        });
    }
}

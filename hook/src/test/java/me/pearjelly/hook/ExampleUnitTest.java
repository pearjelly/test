package me.pearjelly.hook;

import android.os.Build;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        Field field = Build.class.getDeclaredField("SERIAL");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, "test123");
        System.out.println(Build.SERIAL);
        assertEquals("success", Build.SERIAL, "test123");
    }
}
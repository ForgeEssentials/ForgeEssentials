package com.forgeessentials.core.preloader.asminjector.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;

import com.forgeessentials.core.preloader.asminjector.ASMUtil;
import com.forgeessentials.core.preloader.asminjector.InjectionPoint;

/**
 * Annotation for specifying {@link InjectionPoint}s
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface At
{

    /**
     * {@link Shift} is used to shift resulting opcodes
     */
    public enum Shift
    {
        /**
         * Shifts the code by {@link At#by() by} instructions. <br>
         * A value of 0 means that instructions are inserted directly before the injection point.
         */
        BY,

        /**
         * Injects code at the last found label
         */
        LAST_LABEL,

        /**
         * Injects code at the next found label
         */
        NEXT_LABEL;

        public AbstractInsnNode doShift(AbstractInsnNode node, int by)
        {
            switch (this)
            {
            case BY:
                for (; by < 0; by++)
                {
                    if (node == null)
                        return null;
                    node = node.getPrevious();
                }
                for (; by > 0; by--)
                {
                    if (node == null)
                        return null;
                    node = node.getNext();
                }
                return node;
            case LAST_LABEL:
                if (node instanceof LabelNode)
                    return node;
                while (node != null && !(node instanceof LabelNode || node instanceof LineNumberNode))
                    node = node.getPrevious();
                return node.getNext();
            case NEXT_LABEL:
                if (node instanceof LabelNode)
                    return node;
                while (node != null && !(node instanceof LabelNode || node instanceof LineNumberNode || ASMUtil.isReturn(node)))
                    node = node.getNext();
                return node;
            }
            return null;
        }

        public static Shift fromAnnotation(String[] annotationValue)
        {
            return annotationValue == null || annotationValue.length < 2 ? BY : valueOf(annotationValue[1]);
        }

    }

    /**
     * Type of this {@link InjectionPoint}
     * 
     * @return
     */
    public String value();

    /**
     * Target member used by INVOKE, INVOKE_STRING, INVOKE_ASSIGN and FIELD. <br>
     * 
     * @return target reference for supported {@link InjectionPoint} types
     */
    public String target() default "";

    /**
     * Shift type for displacing {@link InjectionPoint}s. <br>
     * For example use {@link At.Shift#AFTER AFTER} with an INVOKE InjectionPoint to move the returned opcodes to <i>after</i> the invoation. <br>
     * <br>
     * Use {@link At.Shift#BY BY} in conjunction with the {@link #by} parameter to shift by an arbitrary number of opcodes.
     * 
     * @return Type of shift to apply
     */
    public Shift shift() default Shift.LAST_LABEL;

    /**
     * If {@link #shift} is specified as {@link At.Shift#BY BY}, specifies the number of opcodes to shift by (negative numbers are allowed).
     * 
     * @return Amount of shift to apply for the {@link At.Shift#BY BY} shift
     */
    public int by() default 0;

    /**
     * Ordinal offset. Many InjectionPoints will return every opcode matching their criteria, specifying <em>ordinal</em> allows a particular opcode to be identified from the
     * returned list. The default value of -1 does not alter the behaviour and returns all matching opcodes. Specifying a value of 0 or higher returns <em>only</em> the requested
     * opcode (if one exists: for example specifying an ordinal of 4 when only 2 opcodes are matched by the InjectionPoint is not going to work particularly well!)
     * 
     * @return ordinal value for supported InjectionPoint types
     */
    public int ordinal() default -1;

    /**
     * Target opcode for FIELD and JUMP InjectionPoints. See the javadoc for the relevant injection point for more details.
     * 
     * @return Bytecode opcode for supported InjectionPoints
     */
    // public int opcode() default -1;

}

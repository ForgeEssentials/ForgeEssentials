package com.forgeessentials.core.preloader.injections;

import com.forgeessentials.core.TestClass;
import com.forgeessentials.core.preloader.asminjector.CallbackInfo;
import com.forgeessentials.core.preloader.asminjector.annotation.At;
import com.forgeessentials.core.preloader.asminjector.annotation.At.Shift;
import com.forgeessentials.core.preloader.asminjector.annotation.Inject;
import com.forgeessentials.core.preloader.asminjector.annotation.Local;
import com.forgeessentials.core.preloader.asminjector.annotation.Mixin;

@Mixin(TestClass.class)
public abstract class MixinTest
{

    @Inject(target = "bar()I", aliases = {}, at = @At("HEAD") )
    protected void bar_tweak(CallbackInfo ci)
    {
        System.out.println("Injection replacing return value in bar()I");
        ci.doReturn(42);
    }

    @Inject(target = "test()V", aliases = {}, at = @At("HEAD") )
    protected void test_head(CallbackInfo ci)
    {
        System.out.println("Injection at HEAD");
    }

    @Inject(target = "test()V", aliases = {},
            at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", shift = Shift.LAST_LABEL) , priority = 1)
    protected void test_invoke(CallbackInfo ci)
    {
        System.out.println("Injection at INVOKE");
    }

    @Inject(target = "test()V", aliases = {}, at = @At(value = "INVOKE", target = "Lcom/forgeessentials/core/TestClass;bar()I", shift = Shift.NEXT_LABEL) )
    protected void test_local(CallbackInfo ci, @Local("foo") int foo)
    {
        System.out.println("Injection at INVOKE modifying local variable");
        foo *= 2;
    }

    @Inject(target = "test()V", aliases = {}, at = @At("TAIL") )
    protected void test_tail(CallbackInfo ci)
    {
        System.out.println("Injection at TAIL");
    }

}

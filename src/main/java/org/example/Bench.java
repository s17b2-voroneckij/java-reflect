package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 2, timeUnit = TimeUnit.MILLISECONDS, time = 5000)
@Measurement(iterations = 2, timeUnit = TimeUnit.MILLISECONDS, time = 5000)
public class Bench {
    A a = new A(1, "12332", 9.0);
    B b = new B();

    @Benchmark
    public void nativeConvert(Blackhole bh) {
        b.a = a.a;
        b.b = a.b;
        b.c = a.c;
        bh.consume(b);
    }

    @Benchmark
    public void joorConvert(Blackhole bh) {
        JoorReflectUtils.convert(a, b, Map.of(), Map.of());
        bh.consume(b);
    }

    ReflectUtils.Converter<A, B> conv = new ReflectUtils.Converter<>(A.class, B.class, Map.of(), Map.of());

    @Benchmark
    public void reflectConvertFast(Blackhole bh) throws IllegalAccessException {
        conv.convert(a, b);
        bh.consume(b);
    }

    @Benchmark
    public void reflectConvertFaster(Blackhole bh) throws IllegalAccessException {
        conv.convertFastNoTransform(a, b);
        bh.consume(b);
    }

    @Benchmark
    public void reflectConvertFastNewObject(Blackhole bh) throws IllegalAccessException {
        b = new B();
        conv.convert(a, b);
        bh.consume(b);
    }

    @Benchmark
    public void reflectConvertSlow(Blackhole bh) throws IllegalAccessException {
        ReflectUtils.Converter<A, B> c = new ReflectUtils.Converter<>(A.class, B.class, Map.of(), Map.of());
        c.convert(a, b);
        bh.consume(b);
    }

    Gson gson = new Gson();

    @Benchmark
    public void convertGson(Blackhole bh) {
        b = gson.fromJson(gson.toJsonTree(a), B.class);
        bh.consume(b);
    }

    TypeToken<B> token = new TypeToken<B>(){};

    @Benchmark
    public void convertGsonFast(Blackhole bh) {
        b = gson.fromJson(gson.toJsonTree(a), token);
        bh.consume(b);
    }

    ModelMapper mm = new ModelMapper();

    @Benchmark
    public void convertModelMapper(Blackhole bh) {
        b = mm.map(a, B.class);
        bh.consume(b);
    }

    @Benchmark
    public void convertMapStruct(Blackhole bh) {
        b = AbMapper.INSTANCE.AtoB(a);
        bh.consume(b);
    }

    BiConsumer<A, B> converter = JoorReflectUtils.getConverter(new A(null, null, 0.0), B.class, Map.of());

    @Benchmark
    public void reflectCodeGen(Blackhole bh) {
        converter.accept(a, b);
        bh.consume(b);
    }

    final private BiConsumer<A, B> converter_final = JoorReflectUtils.getConverter(new A(null, null, 0.0), B.class, Map.of());

    @Benchmark
    public void reflectCodeGenFinal(Blackhole bh) {
        converter_final.accept(a, b);
        bh.consume(b);
    }


    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(Bench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}

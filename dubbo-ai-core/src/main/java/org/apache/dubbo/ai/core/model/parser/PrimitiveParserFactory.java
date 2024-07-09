/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.ai.core.model.parser;

import org.apache.dubbo.ai.core.model.Parser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PrimitiveParserFactory {

    private static final Map<Class<?>, Parser<?>> parserMap = new HashMap<>();

    static {
        parserMap.put(Boolean.class, new BoolParser());
        parserMap.put(boolean.class, new BoolParser());
        parserMap.put(Byte.class, new ByteParser());
        parserMap.put(byte.class, new ByteParser());
        parserMap.put(Short.class, new ShortParser());
        parserMap.put(short.class, new ShortParser());
        parserMap.put(Integer.class, new IntegerParser());
        parserMap.put(int.class, new IntegerParser());
        parserMap.put(Long.class, new LongParser());
        parserMap.put(long.class, new LongParser());
        parserMap.put(Double.class, new DoubleParser());
        parserMap.put(double.class, new DoubleParser());
        parserMap.put(Float.class, new FloatParser());
        parserMap.put(float.class, new FloatParser());
        parserMap.put(Character.class,new CharParser());
        parserMap.put(char.class,new CharParser());
    }

    public static Parser<?> getParser(Class<?> returnType) {
        return parserMap.get(returnType);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class;
    }

    static class BoolParser implements Parser<Boolean> {
        @Override
        public Boolean parse(String content) {
            return Boolean.parseBoolean(content);
        }

        @Override
        public String formatDesc() {
            return "true or false str";
        }
    }

    static class ByteParser implements Parser<Byte> {

        @Override
        public Byte parse(String content) {
            return Byte.parseByte(content);
        }

        @Override
        public String formatDesc() {
            return "byte number type";
        }
    }

    static class ShortParser implements Parser<Short> {

        @Override
        public Short parse(String content) {
            return Short.parseShort(content);
        }

        @Override
        public String formatDesc() {
            return "byte number type";
        }
    }

    static class IntegerParser implements Parser<Integer> {

        @Override
        public Integer parse(String content) {
            return Integer.parseInt(content);
        }

        @Override
        public String formatDesc() {
            return "integer number";
        }
    }

    static class LongParser implements Parser<Long> {

        @Override
        public Long parse(String content) {
            return Long.parseLong(content);
        }

        @Override
        public String formatDesc() {
            return "long number";
        }
    }

    static class DoubleParser implements Parser<Double> {

        @Override
        public Double parse(String content) {
            return Double.parseDouble(content);
        }

        @Override
        public String formatDesc() {
            return "double number";
        }
    }


    static class FloatParser implements Parser<Float> {

        @Override
        public Float parse(String content) {
            return Float.parseFloat(content);
        }

        @Override
        public String formatDesc() {
            return "double number";
        }
    }

    static class CharParser implements Parser<Character> {

        @Override
        public Character parse(String content) {
            return content.charAt(0);
        }

        @Override
        public String formatDesc() {
            return "char type";
        }
    }
    
    public Double getA(){
        return 0d;
    }

    public static void main(String[] args) throws NoSuchMethodException {
        Class<PrimitiveParserFactory> primitiveParserFactoryClass = PrimitiveParserFactory.class;
        Method getA = primitiveParserFactoryClass.getMethod("getA");
        Class<?> returnType = getA.getReturnType();
        System.out.println(isPrimitiveOrWrapper(returnType));
    }


}

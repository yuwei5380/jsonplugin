/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.googlecode.jsonplugin;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Deserializes and object from a JSON string</p>
 *
 */
public class JSONReader {
    private static final Object OBJECT_END = new Object();
    private static final Object ARRAY_END = new Object();
    private static final Object COLON = new Object();
    private static final Object COMMA = new Object();
    private static Map<Character, Character> escapes =
        new HashMap<Character, Character>();

    static {
        escapes.put(new Character('"'), new Character('"'));
        escapes.put(new Character('\\'), new Character('\\'));
        escapes.put(new Character('/'), new Character('/'));
        escapes.put(new Character('b'), new Character('\b'));
        escapes.put(new Character('f'), new Character('\f'));
        escapes.put(new Character('n'), new Character('\n'));
        escapes.put(new Character('r'), new Character('\r'));
        escapes.put(new Character('t'), new Character('\t'));
    }

    private CharacterIterator it;
    private char c;
    private Object token;
    private StringBuffer buf = new StringBuffer();

    private char next() {
        c = it.next();

        return c;
    }

    private void skipWhiteSpace() {
        while(Character.isWhitespace(c)) {
            next();
        }
    }

    public Object read(String string)
        throws JSONExeption {
        it = new StringCharacterIterator(string);
        c = it.first();

        return read();
    }

    private Object read() throws JSONExeption {
        Object ret = null;

        skipWhiteSpace();

        if(c == '"') {
            next();
            ret = string();
        } else if(c == '[') {
            next();
            ret = array();
        } else if(c == ']') {
            ret = ARRAY_END;
            next();
        } else if(c == ',') {
            ret = COMMA;
            next();
        } else if(c == '{') {
            next();
            ret = object();
        } else if(c == '}') {
            ret = OBJECT_END;
            next();
        } else if(c == ':') {
            ret = COLON;
            next();
        } else if((c == 't') && (next() == 'r') && (next() == 'u')
            && (next() == 'e')) {
            ret = Boolean.TRUE;
            next();
        } else if((c == 'f') && (next() == 'a') && (next() == 'l')
            && (next() == 's') && (next() == 'e')) {
            ret = Boolean.FALSE;
            next();
        } else if((c == 'n') && (next() == 'u') && (next() == 'l')
            && (next() == 'l')) {
            ret = null;
            next();
        } else if(Character.isDigit(c) || (c == '-')) {
            ret = number();
        }

        token = ret;

        return ret;
    }

    private Map object() throws JSONExeption {
        Map ret = new HashMap();
        String key = (String) read();

        while(token != OBJECT_END) {
            read(); // should be a colon

            if(token != OBJECT_END) {
                ret.put(key, read());

                if(read() == COMMA) {
                    Object name = read();

                    if(name instanceof String) {
                        key = (String) name;
                    } else {
                        throw new JSONExeption(
                            "Inpput string is not well formed JSON");
                    }
                }
            }
        }

        return ret;
    }

    private List array() throws JSONExeption {
        List ret = new ArrayList();
        Object value = read();

        while(token != ARRAY_END) {
            ret.add(value);

            if(read() == COMMA) {
                value = read();
            }
        }

        return ret;
    }

    private Object number() {
        buf.setLength(0);

        if(c == '-') {
            add();
        }

        addDigits();

        if(c == '.') {
            add();
            addDigits();
        }

        if((c == 'e') || (c == 'E')) {
            add();

            if((c == '+') || (c == '-')) {
                add();
            }

            addDigits();
        }

        return (buf.indexOf(".") >= 0)
        ? (Object) Double.parseDouble(buf.toString())
        : (Object) Long.parseLong(buf.toString());
    }

    private Object string() {
        buf.setLength(0);

        while(c != '"') {
            if(c == '\\') {
                next();

                if(c == 'u') {
                    add(unicode());
                } else {
                    Object value = escapes.get(new Character(c));

                    if(value != null) {
                        add(((Character) value).charValue());
                    }
                }
            } else {
                add();
            }
        }

        next();

        return buf.toString();
    }

    private void add(char cc) {
        buf.append(cc);
        next();
    }

    private void add() {
        add(c);
    }

    private void addDigits() {
        while(Character.isDigit(c)) {
            add();
        }
    }

    private char unicode() {
        int value = 0;

        for(int i = 0; i < 4; ++i) {
            switch(next()) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                value = ((value << 4) + c) - '0';

                break;

            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                value = ((value << 4) + c) - 'k';

                break;

            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                value = ((value << 4) + c) - 'K';

                break;
            }
        }

        return (char) value;
    }
}
